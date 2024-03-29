package app;

import filereader.DocumentReader;
import models.Note;

import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Integer.parseInt;

public class AnkiHelperApp {

    private ArrayList<String> allPars;
    private ArrayList<ArrayList<ArrayList<String>>> allTables;
    private boolean deckIncluded;
    private String deckName;

    public static AnkiHelperApp initializeAnkiHelper() {
        return new AnkiHelperApp();
    }

    AnkiHelperApp() {
    }

    public void analyzeDocument(String filePath, String deckName) {
        if (filePath.endsWith(".docx") || filePath.endsWith(".doc"))
            this.allPars = DocumentReader.getAllParagraphs(filePath);
        else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls"))
            this.allTables = DocumentReader.getExcelTables(filePath);
        this.deckName = deckName;
        this.deckIncluded = false;
    }

    public void analyzeDocument(String filePath) {
        if (filePath.endsWith(".docx") || filePath.endsWith(".doc"))
            this.allPars = DocumentReader.getAllParagraphs(filePath);
        this.deckIncluded = true;
    }

    public ArrayList<Note> analyzeTables() {
        if (this.allTables.size() > 0) {
            return scanDocumentForTables();
        } else
            return new ArrayList<>();
    }

    public void clearAll() {
        allPars = null;
        allTables = null;
    }

    public ArrayList<Note> analyzeNameAndDescribe() {
       return scanDocumentForNameAndDescribe();
    }

    public ArrayList<Note> analyzeBasicFormat() {
        return scanDocumentForBasicFormat("Basic");
    }

    public ArrayList<Note> analyzeReversedFormat() {
        return scanDocumentForBasicFormat("Reversed");
    }

    private ArrayList<Note> scanDocumentForTables() {
        ArrayList<Note> tableNotes = new ArrayList<>();
        for (ArrayList<ArrayList<String>> table : allTables) {
            ArrayList<String> headerRow = table.get(0);
            String columnHeadings = String.join("</br>", headerRow.subList(1, headerRow.size()));
            boolean isHeader = true;
            for (ArrayList<String> row : table) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String front = headerRow.get(0)+ ": " + row.get(0);
                front += "</br></br>";
                front += columnHeadings;

                String back = String.join("</br>", row.subList(1, row.size()));
                String model = "Basic";

                Note note = new Note(this.deckName, model, front, back);

                tableNotes.add(note);
            }
        }

        return tableNotes;
    }

    private ArrayList<Note> scanDocumentForBasicFormat(String keyword) {

        ArrayList<Integer> basicIndices = new ArrayList<>();
        ArrayList<Note> basicNotes = new ArrayList<Note>();

        final String sideMarker = "/";
        final String noteMarker = "//";

        // Find Name and describe commands.
        for (String par : allPars) {
            if (par.startsWith(keyword)) {
                basicIndices.add(allPars.indexOf(par));
            }
        }

        for (int index : basicIndices) {
            ArrayList<Integer> sideMarkerIndices = new ArrayList<>();
            ArrayList<Integer> noteMarkerIndices = new ArrayList<>();

            int basicNoteCount = 0;

            int numItems = parseInt(allPars.get(index+1).replaceAll("[\\D]", ""));
            int i = index;

            if (deckIncluded) {
                this.deckName = allPars.get(index+1).substring(allPars.get(index+1).indexOf(" "));
            }

            while (basicNoteCount < numItems) {
                i++;
                if (allPars.get(i).equals(sideMarker)) {
                    sideMarkerIndices.add(i);
                }
                if (allPars.get(i).equals(noteMarker)) {
                    noteMarkerIndices.add(i);
                    basicNoteCount++;
                }
            }

            i = index+2;

            for (int j = 0; j < sideMarkerIndices.size(); j++) {
                String front = String.join("</br>", allPars.subList(i,sideMarkerIndices.get(j)));
                String back = String.join("</br>", allPars.subList(sideMarkerIndices.get(j)+1, noteMarkerIndices.get(j)));
                i = noteMarkerIndices.get(j)+1;

                String model = keyword.equals("Basic")?"Basic":"Basic (and reversed card)";
                Note note = new Note(deckName, model, front, back);

                basicNotes.add(note);
            }
        }

        return basicNotes;

    }


    private ArrayList<Note> scanDocumentForNameAndDescribe() {

        ArrayList<Integer> nameAndDescribeIndices = new ArrayList<>();
        ArrayList<Note> clozeNotes = new ArrayList<>();

        final String ndLiteral = "Name and describe";

        // Find Name and describe commands.
        for (String par : allPars) {
            if (par.startsWith(ndLiteral)) {
                nameAndDescribeIndices.add(allPars.indexOf(par));
            }
        }

        // For each Line containing Name and describe, iterate through the items and generate cloze notes.
        for (int index : nameAndDescribeIndices) {
            int numItems = parseInt(allPars.get(index+1).replaceAll("[\\D]", ""));

            boolean createMultiple = false;
            if (allPars.get(index+1).split(" ")[0].contains("M")) {
                createMultiple = true;
            }

            if (deckIncluded) {
                this.deckName = allPars.get(index+1).substring(allPars.get(index+1).indexOf(" "));
            }

            // Extract the title.
            String nameAndDescribeTitle = allPars.get(index).substring(ndLiteral.length()+4);

            // Extract the items.
            List<String> ndPairs = allPars.subList(index+2, index+numItems+2);

            // Iterate through the items, while separating the name from the description items and
            // adding them to a map where the key is the name and the description is the value.
            LinkedHashMap<String, String[]> nameAndDescribePairs = new LinkedHashMap<>();
            for (String pair : ndPairs) {
                String[] temp = pair.split(":");

                String name = temp[0];
                String[] descriptions = temp[1].split(";");

                nameAndDescribePairs.put(name, descriptions);
            }

            // Format the data adding the cloze markers, i.e. {{c1::cloze::hint}} and return a single string.
            String clozeNoteText = formatForCloze(nameAndDescribeTitle,nameAndDescribePairs, createMultiple);

            Note clozeNote = new Note(deckName, "Cloze", clozeNoteText, "");

            clozeNotes.add(clozeNote);
        }

        return clozeNotes;
    }

    private String formatForCloze(String nameAndDescribeTitle,
                                         LinkedHashMap<String, String[]> nameAndDescribePairs,
                                         boolean createMultiple) {

        // Create the title from the first line.
        String clozeNote = nameAndDescribeTitle.toUpperCase().trim();

        // Generate a default hint for the name - this assumes the first word in the title is
        // a plural noun.
        String[] temp = clozeNote.toLowerCase().split(" ");
        temp[0] = temp[0].substring(0, temp[0].length()-1);
        String hint = String.join(" ", temp);

        // Add line breaks - ANKI does not accept new line characters in the note field.
        clozeNote += "</br></br>";

        int clozeNum = 2;

        // Iterate through the map and add a cloze for each key and value pair.
        // If the descriptions are long format (marked with an M in the word document),
        // multiple cloze cards will be formed.
        for (Map.Entry<String,String[]> entry : nameAndDescribePairs.entrySet()) {
           clozeNote += "{{c1::" + entry.getKey() + "::" + hint +"}}";
           clozeNote += " - ";

           for (String value : entry.getValue()) {
               clozeNote += "{{c"+clozeNum+"::" + value + "}}";
               clozeNote += "; ";
           }

           clozeNote += "</br>";
           if (createMultiple) clozeNum++;
        }
        clozeNote += "</br>";

        return clozeNote;
    }
}

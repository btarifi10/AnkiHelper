import filereader.DOCXReader;
import models.AnkiRequestBody;
import models.Note;
import models.Params;
import services.AnkiService;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static java.lang.Integer.parseInt;

public class AnkiHelperApp {
    public static void main(String[] args) {
        String filePath = "./data/simple.docx";

        ArrayList<String> allPars = DOCXReader.getAllParagraphs(filePath);

        // Make sure the Anki API is open and able to communicate.
        if (!(testAnkiAPI())) {
            System.out.println("Unable to communicate with Anki API.");
            return;
        }

        scanDocumentForNameAndDescribe(allPars);

    }

    private static boolean testAnkiAPI() {
        String testRequest = "{ \"action\": \"version\", \"version\": 6 }";

        String response = AnkiService.makeAnkiAPICall(testRequest);
        if (response == null || response.contains("\"error\": null")) {
            return false;
        }

        return true;
    }

    private static void scanDocumentForNameAndDescribe(ArrayList<String> allPars) {

        ArrayList<Integer> nameAndDescribeIndices = new ArrayList<>();

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
            if (allPars.get(index+1).contains("M")) {
                createMultiple = true;
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
            String clozeNote = formatForCloze(nameAndDescribeTitle,nameAndDescribePairs, createMultiple);

            // Make the HTTP Post Request to the Anki API.
            postClozeNoteToAnki(clozeNote);

        }
    }

    private static void postClozeNoteToAnki(String clozeNote){

        // Generates the Cloze Note fields. There are many optional fields so for now, this will just
        // be done manually.

        String fields = "{"+
                "\"Text\":\"" + clozeNote + "\"," +
                "\"Back Extra\": \"\" }";

        Note note = new Note("Naruto", "Cloze", fields);

        AnkiRequestBody requestBody = new AnkiRequestBody("addNote", "6", new Params(note));

        String response = AnkiService.makeAnkiAPICall(requestBody.toJSON());

        if (response == null || response.contains("\"error\": null")) {
            System.out.println("API Call Failed");
        } else System.out.println("API Call Success");


    }

    private static String formatForCloze(String nameAndDescribeTitle,
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

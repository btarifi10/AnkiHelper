package ankihelper.server.managers;

import app.AnkiHelperApp;
import filereader.DocumentReader;
import models.AnkiRequestBody;
import models.Note;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class AnkiManager {
    private final AnkiHelperApp ankiHelper;
    private ArrayList<Note> clozeNotes;
    private ArrayList<Note> basicNotes;
    private ArrayList<Note> reversedNotes;
    private ArrayList<Note> tableNotes;

    public AnkiManager() {
        this.ankiHelper = AnkiHelperApp.initializeAnkiHelper();
        clozeNotes = new ArrayList<>();
        basicNotes = new ArrayList<>();
        reversedNotes = new ArrayList<>();
        tableNotes = new ArrayList<>();
    }


    public void processFile(String filePath, String deckName) {
        if (deckName.equals("")) {
            ankiHelper.analyzeDocument(filePath);
        } else {
            ankiHelper.analyzeDocument(filePath, deckName);
        }
        if (filePath.endsWith(".docx") || filePath.endsWith(".doc")) {
            performBasicReversedNoteAnalysis();
            performNameAndDescribeAnalysis();
            performBasicNoteAnalysis();
        } else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            performTableNoteAnalysis();
        }
    }

    private void performTableNoteAnalysis() {
        tableNotes = ankiHelper.analyzeTables();
    }

    private void performNameAndDescribeAnalysis() {
        clozeNotes = ankiHelper.analyzeNameAndDescribe();
    }

    private void performBasicNoteAnalysis() {
        basicNotes = ankiHelper.analyzeBasicFormat();
    }

    private void performBasicReversedNoteAnalysis() {
        reversedNotes = ankiHelper.analyzeReversedFormat();
    }

    public ArrayList<Note> getClozeNotes() {
        return clozeNotes;
    }

    public ArrayList<Note> getBasicNotes() {
        return basicNotes;
    }

    public ArrayList<Note> getReversedNotes() {
        return reversedNotes;
    }

    public ArrayList<Note> getTableNotes() { return tableNotes;
    }
}

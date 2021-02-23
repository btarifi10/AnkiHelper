package app;

import models.Note;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AnkiHelperAppTests {

    @Test
    public void basicNoteGeneration() {

        AnkiHelperApp ankiHelperApp = AnkiHelperApp.initializeAnkiHelper();

        ankiHelperApp.analyzeDocument("../data/test2.docx");
        ArrayList<Note> basicNotes = ankiHelperApp.analyzeBasicFormat();

        for (Note note : basicNotes) {
            System.out.println("Front: " + note.getFront());
            System.out.println("Back: " + note.getBack());
            System.out.println();
        }

    }

}
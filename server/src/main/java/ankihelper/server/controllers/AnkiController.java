package ankihelper.server.controllers;

import ankihelper.server.managers.AnkiManager;
import ankihelper.server.messages.ResponseMessage;
import ankihelper.server.models.AnkiResponseBody;
import ankihelper.server.models.FileInfo;
import ankihelper.server.services.AnkiService;
import ankihelper.server.services.FileStorageService;
import models.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class AnkiController {
    @Autowired
    FileStorageService storageService;
    @Autowired
    AnkiService ankiService;
    AnkiManager ankiManager;

    private ArrayList<String> clozeNotes;
    private ArrayList<String[]> basicNotes;
    private ArrayList<String[]> reversedNotes;
    private ArrayList<Note> notesFound;

    public AnkiController(AnkiManager ankiManager) {
        this.ankiManager = ankiManager;
    }

    @GetMapping("/home")
    @ResponseBody
    public ResponseEntity<String> home() {
        System.out.println("Cool");
        return ResponseEntity.status(HttpStatus.OK).body("Active");
    }

    @GetMapping("/test")
    @ResponseBody
    public boolean testConnection() {
        boolean active = ankiService.testConnection();

        return active;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage>
    uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.save(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(AnkiController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }



    @DeleteMapping ("/")
    @ResponseBody
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam String filename) {
        String message = "";
        try {
            storageService.deleteFile(filename);

            message = "Deleted the file successfully: " + filename;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename+ "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/proceed")
    @ResponseBody
    public ArrayList<Note> analyseFiles(@RequestParam String deckName) {

        ArrayList<String> fileNames = storageService.loadAll().map(path->storageService.getRoot().resolve(path).toString())
                .collect(Collectors.toCollection(ArrayList::new));

        this.clozeNotes = new ArrayList<>();
        this.basicNotes = new ArrayList<>();
        this.reversedNotes = new ArrayList<>();
        this.notesFound = new ArrayList<>();
        for (String file : fileNames) {
            ankiManager.processFile(file, deckName);
            this.notesFound.addAll(ankiManager.getClozeNotes());
            this.notesFound.addAll(ankiManager.getBasicNotes());
            this.notesFound.addAll(ankiManager.getReversedNotes());
        }


        ArrayList<String> noteTitles = new ArrayList<>();
        for (Note note : notesFound) {
            noteTitles.add(note.getFront().split("</br")[0]);
        }

        return notesFound;
    }

    @GetMapping("/confirm")
    @ResponseBody
    public List<AnkiResponseBody> createCards() {
        ArrayList<AnkiResponseBody> responses = new ArrayList<>();

        for (Note note : notesFound) {
            responses.add(ankiService.postNoteToAnki(note));
        }

        storageService.deleteAll();
        storageService.init();

        return responses;
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/decks")
    @ResponseBody
    public ArrayList<String> getDecks() {
        return ankiService.getAllDecks();
    }

}

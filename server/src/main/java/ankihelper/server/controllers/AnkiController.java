package ankihelper.server.controllers;

import ankihelper.server.managers.AnkiManager;
import ankihelper.server.messages.ResponseMessage;
import ankihelper.server.models.FileInfo;
import ankihelper.server.services.AnkiService;
import ankihelper.server.services.FileStorageService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:4200")
public class AnkiController {
    @Autowired
    FileStorageService storageService;
    @Autowired
    AnkiService ankiService;
    AnkiManager ankiManager;

    private ArrayList<String> clozeNotes;

    public AnkiController(AnkiManager ankiManager) {
        this.ankiManager = ankiManager;
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
    public ArrayList<String> analyseFiles() {

        ArrayList<String> fileNames = storageService.loadAll().map(path->storageService.getRoot().resolve(path).toString())
                .collect(Collectors.toCollection(ArrayList::new));

        this.clozeNotes = new ArrayList<>();
        for (String file : fileNames) {
                this.clozeNotes.addAll(ankiManager.performNameAndDescribeAnalysis(file));
        }


        ArrayList<String> clozeNoteTitles = new ArrayList<>();
        for (String note : clozeNotes) {
            clozeNoteTitles.add(note.split("</br")[0]);
        }

        return clozeNoteTitles;
    }

    @GetMapping("/confirm")
    @ResponseBody
    public ArrayList<String> createCards(@RequestParam String deckName) {
        ArrayList<String> responses = new ArrayList<>();

        for (String clozeNote : this.clozeNotes) {
            responses.add(ankiService.postClozeNoteToAnki(clozeNote, deckName));
        }

        return responses;
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}

package ankihelper.server;

import ankihelper.server.services.AnkiService;
import ankihelper.server.services.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Resource
    FileStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        storageService.deleteAll();
        storageService.init();

        String testRequest = "{ \"action\": \"version\", \"version\": 6 }";

        AnkiService ankiService = new AnkiService();

        String response = ankiService.makeAnkiAPICall(testRequest);
        if (response == null || response.contains("\"error\": null")) {
            throw new RuntimeException();
        }
    }
}

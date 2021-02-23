package ankihelper.server;

import ankihelper.server.models.AnkiResponseBody;
import ankihelper.server.services.AnkiService;
import ankihelper.server.services.FileStorageService;
import models.AnkiRequestBody;
import models.Params;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Resource
    FileStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
       // openHomePage();
    }

    @Override
    public void run(String... args) throws Exception {
        storageService.deleteAll();
        storageService.init();
    }

    /*
    private static void openHomePage() {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://localhost:8017");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}

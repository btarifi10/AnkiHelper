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
    }
}

package ankihelper.server;

import ankihelper.server.services.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Resource
    FileStorageService storageService;

    private static ApplicationContext context;

    public static void main(String[] args) {
       context = SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        storageService.deleteAll();
        storageService.init();
    }

    public static void close() {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

}

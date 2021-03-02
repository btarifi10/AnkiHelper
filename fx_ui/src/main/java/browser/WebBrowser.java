package browser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebBrowser extends Application {

    private static Thread serverThread;

    private static final String serverUrl = "http://localhost:8017";

    public WebBrowser() {
    }

    public static void main(String[] args) {

        runSpringBootServer();

        boolean active = false;

        while (!active) {
            try {
                Thread.sleep(5000);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(serverUrl + "/api/home"))
                        .version(HttpClient.Version.HTTP_2)
                        .GET().build();

                // Receive the response.
                HttpResponse<String> response = HttpClient
                        .newBuilder()
                        .proxy(ProxySelector.getDefault())
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().toString().contains("active")) {
                    active = true;
                };
            } catch (IOException | InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        Application.launch(WebBrowser.class, args);
    }

    private static void runSpringBootServer() {
        serverThread = new Thread(runServer);
        serverThread.start();
    }

    private static Runnable runServer = () -> {
        try {
            String pathToServerJar = "../../../server/build/libs/server-0.0.1-SNAPSHOT.jar";
            ProcessBuilder pb = new ProcessBuilder("java.exe", "-jar", pathToServerJar);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    };


    @Override
    public void start(Stage primaryStage) {


        // Allows the app to communicate to the API (Javalin)
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        String START_URL = "http://localhost:8017/";
        TextField locationField = new TextField(START_URL);
        WebView webView = new WebView();
        webView.getEngine().load(START_URL);

        webView.setContextMenuEnabled(false);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem reload = new MenuItem("Reload");
        reload.setOnAction(e -> webView.getEngine().load(webView.getEngine().getLocation()));
        contextMenu.getItems().addAll(reload);



        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        locationField.setOnAction(e -> {
            webView.getEngine().load(locationField.getText());
        });

        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("AnkiHelperApp");
        primaryStage.show();

    }

    @Override
    public void stop() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + "/actuator/shutdown"))
                    .version(HttpClient.Version.HTTP_2)
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .build();

            // Receive the response.
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
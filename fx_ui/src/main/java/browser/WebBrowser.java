package browser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.awt.Desktop;

public class WebBrowser extends Application {

    private static Thread serverThread;

    private static final String serverUrl = "http://localhost:8017";
    private WebView webView;

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

                if (response.body().toString().contains("Active")) {
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
            String pathToServerJar = "./bin/AnkiHelperApp.jar";
            String pathToJava = "./jre/bin/java.exe";
            ProcessBuilder pb = new ProcessBuilder(pathToJava, "-jar", pathToServerJar);
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
        webView = new WebView();
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
        /*
        webView.getEngine().locationProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, final String oldValue, String newValue)
            {
                try {
                    URI address = new URI(newValue);
                    System.out.println(address.toString());
                    if (address.toString().equals("https://ankiweb.net/shared/info/2055492159")) {
                        Desktop.getDesktop().browse(address);
                        webView.getEngine().
                    }
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

         */

        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>()
        {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
                                Worker.State newValue)
            {
                Desktop d = Desktop.getDesktop();
                URI address = null;
                try {
                    address = new URI(newValue.toString());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if (address.toString().equals("https://ankiweb.net/shared/info/2055492159")) {
                    try { Platform.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            webView.getEngine().load(oldValue.toString());
                        }
                    });
                        d.browse(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


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
package ui;

import javax.swing.*;
import java.awt.*;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnkiWrapperGui extends JFrame {
    private final String serverUrl = "http://localhost:8017/";
    JPanel mainPanel;
    JButton btnStopApp;
    JLabel lblAppName;
    private JLabel lblStatus;
    private JLabel lblIcon;
    private JButton btnOpen;

    public static void main(String[] args) {
        JFrame frame = new AnkiWrapperGui("Anki Helper App");
        frame.setVisible(true);
    }

    private Runnable waitForServerToStart = () -> {
        boolean active = false;
        while (!active) {
            try {
                Thread.sleep(5000);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(serverUrl + "api/home"))
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
                }
                ;
            } catch (IOException | InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        lblStatus.setText("App is running.");
        btnOpen.setEnabled(true);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(serverUrl));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    };

    private void checkServer() {
        Thread checkThread = new Thread(waitForServerToStart);
        checkThread.start();
    }

    private void runSpringBootServer() {
        Thread serverThread = new Thread(runServer);
        serverThread.start();
    }

    private Runnable runServer = () -> {
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

    public AnkiWrapperGui(String title) {
        super(title);

        init();

        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        btnStopApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shutdownSpringBootServer();
                System.exit(0);
            }
        });
        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(serverUrl));
                    } catch (IOException | URISyntaxException err){
                        err.printStackTrace();
                    }
                }
            }
        });
    }

    private void shutdownSpringBootServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + "actuator/shutdown"))
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

    private void init() {
        ImageIcon image = new ImageIcon("./wrapper-ui/src/main/resources/static/biglogo.png");
        Image img = image.getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH);

        setLocationRelativeTo(null);
        btnOpen.setEnabled(false);
        setResizable(false);

        this.setIconImage(image.getImage());

        lblStatus.setText("App is starting up...");
        lblIcon.setIcon(new ImageIcon(img));

        runSpringBootServer();
        checkServer();
    }
}

package services;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnkiService {
    public final static String AnkiURL = "http://localhost:8765";

    public static String makeAnkiAPICall(String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(AnkiURL))
                    .version(HttpClient.Version.HTTP_2)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Receive the response.

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.toString();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}

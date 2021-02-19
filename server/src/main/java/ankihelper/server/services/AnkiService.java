package ankihelper.server.services;

import models.AnkiRequestBody;
import models.Note;
import models.Params;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AnkiService {
    public final String AnkiURL = "http://localhost:8765";

    public String postClozeNoteToAnki(String clozeNote, String deckName){

        // Generates the Cloze Note fields. There are many optional fields so for now, this will just
        // be done manually.

        String fields = "{"+
                "\"Text\":\"" + clozeNote + "\"," +
                "\"Back Extra\": \"\" }";

        String modelName = "Cloze";
        Note note = new Note(deckName, modelName, fields);

        AnkiRequestBody requestBody = new AnkiRequestBody("addNote", "6", new Params(note));

        String response = makeAnkiAPICall(requestBody.toJSON());

        if (response == null || response.contains("\"error\": null")) {
            return "Anki API Call Failed";
        } else return response.toString();
    }

    public String makeAnkiAPICall(String requestBody) {
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

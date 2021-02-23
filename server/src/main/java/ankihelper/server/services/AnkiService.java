package ankihelper.server.services;

import ankihelper.server.models.AnkiResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.AnkiRequestBody;
import models.Note;
import models.Params;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnkiService {
    public final String AnkiURL = "http://localhost:8765";
    private final ObjectMapper objectMapper;

    public AnkiService() {
        this.objectMapper = new ObjectMapper();
    }

    public AnkiResponseBody postNoteToAnki(Note note){

        // Generates the Cloze Note fields. There are many optional fields so for now, this will just
        // be done manually.

        if (!deckExists(note.getDeckName()))
        {
            createDeck(note.getDeckName());
        }

        AnkiRequestBody requestBody = new AnkiRequestBody("addNote", 6, new Params(note));

        AnkiResponseBody responseBody = makeAnkiAPICall(requestBody.toJSON());

        return responseBody;
    }

    private void createDeck(String deckName) {
        String requestBody = "{" +
                "\"action\": \"createDeck\"," +
                "    \"version\": 6," +
                "    \"params\": {" +
                "        \"deck\": \""+ deckName + "\"" +
                "    } " +
                "}";

        makeAnkiAPICall(requestBody);
    }

    public boolean deckExists(String deckName) {
        ArrayList<String> deckNames = getAllDecks();

        return deckNames.contains(deckName);
    }

    public AnkiResponseBody makeAnkiAPICall(String requestBody) {
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

            return bindJSONResponse(response);

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public ArrayList<String> getAllDecks() {
        AnkiRequestBody ankiRequestBody = new AnkiRequestBody("deckNames", 6, new Params(null));

        AnkiResponseBody responseBody = makeAnkiAPICall(ankiRequestBody.toJSON());

        return (ArrayList<String>) responseBody.getResult();
    }

    private AnkiResponseBody bindJSONResponse(HttpResponse<String> response) {
        AnkiResponseBody responseBody = null;
        try {
            responseBody = objectMapper.readValue(response.body().toString(), AnkiResponseBody.class);
            return responseBody;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

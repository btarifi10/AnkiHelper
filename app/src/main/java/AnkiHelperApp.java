import filereader.DOCXReader;
import models.AnkiRequestBody;
import models.Note;
import models.Params;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static java.lang.Integer.parseInt;

public class AnkiHelperApp {
    public static void main(String[] args) {
        String filePath = "./data/simple.docx";

        ArrayList<String> allPars = DOCXReader.getAllParagraphs(filePath);

        ArrayList<Integer> nameAndDescribeIndices = new ArrayList<>();

        String ndLiteral = "Name and describe";

        for (String par : allPars) {
            if (par.startsWith(ndLiteral)) {
                nameAndDescribeIndices.add(allPars.indexOf(par));
            }
        }

        for (int index : nameAndDescribeIndices) {
            int numItems = parseInt(allPars.get(index+1));

            String nameAndDescribeTitle = allPars.get(index).substring(ndLiteral.length()+4);

            List<String> ndPairs = allPars.subList(index+2, index+numItems+2);

            LinkedHashMap<String, String[]> nameAndDescribePairs = new LinkedHashMap<>();

            for (String pair : ndPairs) {
                String[] temp = pair.split(":");

                String name = temp[0];
                String[] descriptions = temp[1].split(";");

                nameAndDescribePairs.put(name, descriptions);
            }

            String clozeNote = formatForCloze(nameAndDescribeTitle,nameAndDescribePairs);

            postClozeNoteToAnki(clozeNote);

        }

    }

    private static void postClozeNoteToAnki(String clozeNote){

        String fields = "{"+
                "\"Text\":\"" + clozeNote + "\"," +
                "\"Back Extra\": \"\" }";

        Note note = new Note("Naruto", "Cloze", fields);

        AnkiRequestBody requestBody = new AnkiRequestBody("addNote", "6", new Params(note));

        System.out.println(requestBody);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8765"))
                    .version(HttpClient.Version.HTTP_2)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSON()))
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }


    }

    private static String formatForCloze(String nameAndDescribeTitle, LinkedHashMap<String, String[]> nameAndDescribePairs) {
        String clozeNote = nameAndDescribeTitle.toUpperCase().trim();
        clozeNote += "</br></br>";

        for (Map.Entry<String,String[]> entry : nameAndDescribePairs.entrySet()) {
           clozeNote += "{{c1::" + entry.getKey() + "}}";
           clozeNote += " - ";

           for (String value : entry.getValue()) {
               clozeNote += "{{c2::" + value + "}}";
               clozeNote += "; ";
           }
           clozeNote += "</br>";
        }
        clozeNote += "</br>";

        return clozeNote;
    }
}

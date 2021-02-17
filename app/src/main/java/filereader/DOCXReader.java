package filereader;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DOCXReader {
    public static ArrayList<String> getAllParagraphs(String filePath) {

        try {
            XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(filePath)));

            List<XWPFParagraph> list = doc.getParagraphs();
            ArrayList<String> listText = new ArrayList<>();
            for (XWPFParagraph paragraph : list) {
                listText.add(paragraph.getText());
            }

            return listText;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();

    }
}

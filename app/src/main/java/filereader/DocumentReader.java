package filereader;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DocumentReader {
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

    public static ArrayList<ArrayList<ArrayList<String>>> getExcelTables(String filePath) {
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfSheets = 0;
        if (workbook != null) {
            numberOfSheets = workbook.getNumberOfSheets();
        }
        ArrayList<ArrayList<ArrayList<String>>> tableList = new ArrayList<>();
        for (int sheetIdx = 0; sheetIdx < numberOfSheets; sheetIdx++) {
            XSSFSheet sheet = workbook.getSheetAt(sheetIdx);
            List<XSSFTable> tables = sheet.getTables();
            for (XSSFTable t : tables) {
//                System.out.println(t.getDisplayName());
//                System.out.println(t.getName());
//                System.out.println(t.getNumerOfMappedColumns());

                int startRow = t.getStartCellReference().getRow();
                int endRow = t.getEndCellReference().getRow();
//                System.out.println("startRow = " + startRow);
//                System.out.println("endRow = " + endRow);

                int startColumn = t.getStartCellReference().getCol();
                int endColumn = t.getEndCellReference().getCol();

//                System.out.println("startColumn = " + startColumn);
//                System.out.println("endColumn = " + endColumn);
                ArrayList<ArrayList<String>> tbl = new ArrayList<>();
                int r = 0;
                int c = 0;
                for (int i = startRow; i <= endRow; i++) {
                    ArrayList<String> tblRow = new ArrayList<>();
                    for (int j = startColumn; j <= endColumn; j++) {
                        String cellVal = "";
                        XSSFCell cell = sheet.getRow(i).getCell(j);
                        if (cell != null) {
                            cellVal = cell.getStringCellValue();
                        }
                        // System.out.print(cellVal + "\t");
                        tblRow.add(cellVal);
                    }
                    // System.out.println();
                    tbl.add(tblRow);
                }
                tableList.add(tbl);
            }
        }
        return tableList;
    }
}

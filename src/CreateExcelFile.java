import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class CreateExcelFile {
    private final String fileName;
    private final HSSFWorkbook hssfWorkbook;
    private final HSSFSheet hssfSheet;
    private int counter;

    public CreateExcelFile (String fileName) {
        this.fileName = fileName;
        this.hssfWorkbook = new HSSFWorkbook();
        this.hssfSheet = hssfWorkbook.createSheet("DataNA");
        this.counter = 0;
    }

    public void createInitialExcelFile (String[] otherColumnsArray) {
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue("R - radio range");
        rowHead.createCell(1).setCellValue("fa - fraction of anchor nodes");
        for (int i = 0; i < otherColumnsArray.length; i++) {
            rowHead.createCell(2 + i).setCellValue(otherColumnsArray[i]);
        }
        counter++;
    }

    public void addRowForErrorLocalization (int R, int fa, double fLoc) {
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue(R);
        rowHead.createCell(1).setCellValue(fa);
        rowHead.createCell(2).setCellValue(fLoc);
        counter++;
    }

    public void addRowForErrorLocalization (int R, int fa, double ALE, double r) {
        HSSFRow rowHead = hssfSheet.createRow((short) counter);
        rowHead.createCell(0).setCellValue(R);
        rowHead.createCell(1).setCellValue(fa);
        rowHead.createCell(2).setCellValue(r);
        rowHead.createCell(3).setCellValue(ALE);
        counter++;
    }

    public void closeFile () throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName);
        hssfWorkbook.write(fileOut);
        //closing the Stream
        fileOut.close();
        //closing the workbook
        hssfWorkbook.close();
    }
}

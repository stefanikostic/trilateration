import java.io.IOException;

public class TrilaterationProgram {
    private static CreateExcelFile excelFileFractionLocatedNodesNA = null;
    private static CreateExcelFile excelFileErrorLocalizationNA = null;
    private static CreateExcelFile excelFileFractionLocatedNodesIA_h1 = null;
    private static CreateExcelFile excelFileErrorLocalizationIA_h1 = null;
    private static CreateExcelFile excelFileFractionLocatedNodesIA_h2 = null;
    private static CreateExcelFile excelFileErrorLocalizationIA_h2 = null;

    private static CreateExcelFile excelFileFractionLocatedNodesNA_3D = null;
    private static CreateExcelFile excelFileErrorLocalizationNA_3D = null;
    private static CreateExcelFile excelFileFractionLocatedNodesIA_h1_3D = null;
    private static CreateExcelFile excelFileErrorLocalizationIA_h1_3D = null;
    private static CreateExcelFile excelFileFractionLocatedNodesIA_h2_3D = null;
    private static CreateExcelFile excelFileErrorLocalizationIA_h2_3D = null;


    public static void createInitialExcelFiles () {
        excelFileFractionLocatedNodesNA = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-NA-1.1.xls");
        excelFileErrorLocalizationNA = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-NA-1.2.xls");
        excelFileFractionLocatedNodesIA_h1 = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-IA-h1-2.1.xls");
        excelFileErrorLocalizationIA_h1 = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-IA-h1-2.2.xls");
        excelFileFractionLocatedNodesIA_h2 = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-IA-h2-2.1.xls");
        excelFileErrorLocalizationIA_h2 = new CreateExcelFile("D:\\Lab1SS-trilateration-2d-IA-h2-2.2.xls");

        excelFileFractionLocatedNodesNA_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-NA-1.1.xls");
        excelFileErrorLocalizationNA_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-NA-1.2.xls");
        excelFileFractionLocatedNodesIA_h1_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-IA-h1-2.1.xls");
        excelFileErrorLocalizationIA_h1_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-IA-h1-2.2.xls");
        excelFileFractionLocatedNodesIA_h2_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-IA-h2-2.1.xls");
        excelFileErrorLocalizationIA_h2_3D = new CreateExcelFile("D:\\Lab1SS-trilateration-3d-IA-h2-2.2.xls");

        String[] columnsArray = new String[]{"fLoc - fraction of located nodes"};
        excelFileFractionLocatedNodesNA.createInitialExcelFile(columnsArray);
        excelFileFractionLocatedNodesIA_h1.createInitialExcelFile(columnsArray);
        excelFileFractionLocatedNodesIA_h2.createInitialExcelFile(columnsArray);
        excelFileFractionLocatedNodesNA_3D.createInitialExcelFile(columnsArray);
        excelFileFractionLocatedNodesIA_h1_3D.createInitialExcelFile(columnsArray);
        excelFileFractionLocatedNodesIA_h2_3D.createInitialExcelFile(columnsArray);

        columnsArray = new String[]{"Radio Range Error", "ALE - Average Localization Error"};
        excelFileErrorLocalizationNA.createInitialExcelFile(columnsArray);
        excelFileErrorLocalizationIA_h1.createInitialExcelFile(columnsArray);
        excelFileErrorLocalizationIA_h2.createInitialExcelFile(columnsArray);
        excelFileErrorLocalizationNA_3D.createInitialExcelFile(columnsArray);
        excelFileErrorLocalizationIA_h1_3D.createInitialExcelFile(columnsArray);
        excelFileErrorLocalizationIA_h2_3D.createInitialExcelFile(columnsArray);
    }

    public static void closeExcelFiles () throws IOException {
        excelFileFractionLocatedNodesNA.closeFile();
        excelFileErrorLocalizationNA.closeFile();
        excelFileFractionLocatedNodesIA_h1.closeFile();
        excelFileErrorLocalizationIA_h1.closeFile();
        excelFileFractionLocatedNodesIA_h2.closeFile();
        excelFileErrorLocalizationIA_h2.closeFile();

        excelFileFractionLocatedNodesNA_3D.closeFile();
        excelFileErrorLocalizationNA_3D.closeFile();
        excelFileFractionLocatedNodesIA_h1_3D.closeFile();
        excelFileErrorLocalizationIA_h1_3D.closeFile();
        excelFileFractionLocatedNodesIA_h2_3D.closeFile();
        excelFileErrorLocalizationIA_h2_3D.closeFile();
    }

    public static void main (String[] args) throws IOException {
        SensorNetwork sensorNetwork = new SensorNetwork();
        SensorNetwork3D sensorNetwork3D = new SensorNetwork3D();

        // create initial excel files for the result of the localization
        createInitialExcelFiles();

        // generate random 15 topologies for the wireless sensor network
        sensorNetwork.generateNetwork(excelFileFractionLocatedNodesNA, excelFileErrorLocalizationNA, false, 0);

        sensorNetwork.generateNetwork(excelFileFractionLocatedNodesIA_h1, excelFileErrorLocalizationIA_h1, true, 1);

        sensorNetwork.generateNetwork(excelFileFractionLocatedNodesIA_h2, excelFileErrorLocalizationIA_h2, true, 2);

        sensorNetwork3D.generateNetwork(excelFileFractionLocatedNodesNA_3D, excelFileErrorLocalizationNA_3D, false, 0);

        sensorNetwork3D.generateNetwork(excelFileFractionLocatedNodesIA_h1_3D, excelFileErrorLocalizationIA_h1_3D, true, 1);

        sensorNetwork3D.generateNetwork(excelFileFractionLocatedNodesIA_h2_3D, excelFileErrorLocalizationIA_h2_3D, true, 2);

        // the process of localization is finished
        // close the excel files
        closeExcelFiles();
    }

}

package main.java.com.zenika;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import main.java.com.zenika.utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


public class Workflow {

    // static Logger logger = Logger.getLogger(PropertiesFileLog4jExample.class);

    public String CURRENT_DIRECTORY = System.getProperty("user.dir") ;

    private static String FILENAME_SEPARATOR = "_" ;
    private static String DOT = "." ;
    private static String HYPHEN = "-" ;
    private static String TRANSACTIONS = "transactions" ;
    private static String REFERENCE_PROD = "reference_prod" ;
    private static String LISTING_PRODUIT = "listing_produit" ;
    private static String SET_PRODUIT = "set_produit" ;
    private static String SET_CA = "set_ca" ;
    private static String DATA = "data" ;
    private static String STAGE1 = "stage1" ;
    private static String STAGE2 = "stage2" ;
    private static String STAGE3 = "stage3" ;
    private static String TOP = "top" ;
    private static String GLOBAL = "GLOBAL" ;
    private static String J7 = "J7" ;
    private static String CA = "ca" ;
    private static String VENTES = "ventes" ;

    public static Set<String>  computeStage1(File transactionFile) {
        System.out.println("Starting compute of Stage1 ");
        long start = System.currentTimeMillis();

        TransactionFileMapper mapper = new TransactionFileMapper(transactionFile);
        Set<String> magasinsId = mapper.processTransactionFile();

        long end = System.currentTimeMillis();
        System.out.println("computeStage1 took " + String.valueOf(end-start) + " ms");
        return magasinsId;
    }

    public static Map<String, Integer> computeStage2(String magasinId, String dateString, int topN) {
        System.out.println("Starting compute of Stage2 ");

        File stage1File = FileBuilder.createStage1File(magasinId, dateString);
        Set<File> stage1FileSet = new HashSet<File>();
        stage1FileSet.add(stage1File) ;

        File stage2File = FileBuilder.createStage2File(magasinId, dateString);
        QteReducer reducerQtePerMagasin = new QteReducer(stage1FileSet, topN,
                stage2File,
                FileBuilder.createVenteMagasinFile(magasinId, dateString, topN));
        Map<String, Integer> productQteMap = reducerQtePerMagasin.reduce();
        return productQteMap;
    }

    public static Map<String, Float> computeStage3(String magasinId, String dateString, int topN) {
        System.out.println("Starting compute of Stage3 ");

        Stage2Processor processor = new Stage2Processor(magasinId, dateString, topN);
        Map<String, Float> productCAMap = processor.process();
        return productCAMap ;
    }

    public static Map<String, Integer> computeStage4_3(String magasinId, String dateString, int topN) {
        System.out.println("Starting compute of Stage4_3 ");
        File stage4_3File = FileBuilder.createStage4_3File(magasinId, dateString);
        Map<String, Integer> productVenteGlobal7JMap = null ;
        try {
            Set<File> stage2Last7DaysFiles = FileBuilder.createStage2Last7DaysFiles(magasinId, dateString);
            if (Workflow.allFilesExist(stage2Last7DaysFiles)) {
                QteReducer reducerQteFor7J = new QteReducer(stage2Last7DaysFiles, topN, stage4_3File, FileBuilder.createVenteMagasin7JFile(magasinId, dateString, topN));
                productVenteGlobal7JMap = reducerQteFor7J.reduce();
            } else {
                System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_3 ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return productVenteGlobal7JMap;
    }

    public static Map<String, Float> computeStage4_4(String magasinId, String dateString, int topN) {
        System.out.println("Starting compute of Stage4_4 ");
        File stage4_4File = FileBuilder.createStage4_4File(magasinId, dateString);
        Map<String, Float> productCAGlobal7JMap = null ;
        try {
            Set<File> stage3Last7DaysFiles = FileBuilder.createStage3Last7DaysFiles(magasinId, dateString);
            if (Workflow.allFilesExist(stage3Last7DaysFiles)) {
                CAReducer reducerCAFor7J = new CAReducer(stage3Last7DaysFiles, topN, stage4_4File, FileBuilder.createCAMagasin7JFile(magasinId, dateString, topN));
                productCAGlobal7JMap = reducerCAFor7J.reduce();
            } else {
                System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_4 ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return productCAGlobal7JMap;
    }

    public static Map<String, Integer> computeStage4_1(Set<String> magasinsIdToGlobal, String dateString, int topN) {
        System.out.println("Starting compute of Stage4_1 ");
        Set<File> allMagasinsVenteFiles = new HashSet<>();
        Map<String, Integer> productVenteGlobal = null ;
        for (Iterator<String> it = magasinsIdToGlobal.iterator(); it.hasNext(); ) {
            String magasin = it.next();
            allMagasinsVenteFiles.add(FileBuilder.createStage2File(magasin, dateString));
        }

        if (allFilesExist(allMagasinsVenteFiles)) {
            File stage4_1File = FileBuilder.createStage4_1File(dateString);
            QteReducer reducerVenteGlobal = new QteReducer(allMagasinsVenteFiles, topN, stage4_1File, FileBuilder.createVenteGlobalFile(dateString, topN));
            productVenteGlobal = reducerVenteGlobal.reduce();
        } else {
            System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_1 ");
        }
        return productVenteGlobal ;
    }

    public static Map<String, Float> computeStage4_2(Set<String> magasinsIdToGlobal, String dateString, int topN) {
        System.out.println("Starting compute of Stage4_2 ");
        Set<File> allMagasinsCAFiles = new HashSet<>() ;
        Map<String, Float> productCAGlobal = null ;

        for (Iterator<String> it = magasinsIdToGlobal.iterator(); it.hasNext(); ) {
            String magasin = it.next();
            allMagasinsCAFiles.add(FileBuilder.createStage3File(magasin, dateString));
        }

        if (allFilesExist(allMagasinsCAFiles)) {
            File stage4_2File = FileBuilder.createStage4_2File(dateString);
            CAReducer reducerCAGlobal = new CAReducer(allMagasinsCAFiles, topN, stage4_2File, FileBuilder.createCAGlobalFile(dateString, topN));
            productCAGlobal = reducerCAGlobal.reduce();
        } else {
            System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_1 ");
        }
        return productCAGlobal;
    }

    public static void computeStage5_1(String dateString, int topN) {
        System.out.println("Starting compute of Stage5_1 ");
        File stage5_1File = FileBuilder.createStage5_1File(dateString);
        try {
            Set<File> stage4_1Last7DaysFiles = FileBuilder.createStage4_1Last7DaysFiles(dateString);
            if (Workflow.allFilesExist(stage4_1Last7DaysFiles)) {
                QteReducer reducerQteGlobalFor7J = new QteReducer(stage4_1Last7DaysFiles, topN, stage5_1File, FileBuilder.createVenteGlobal7JFile(dateString, topN));
                reducerQteGlobalFor7J.reduce();
            } else {
                System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage5_1 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void computeStage5_2(String dateString, int topN) {
        System.out.println("Starting compute of Stage5_2 ");
        File stage5_2File = FileBuilder.createStage5_2File(dateString);
        try {
            Set<File> stage4_2Last7DaysFiles = FileBuilder.createStage4_2Last7DaysFiles(dateString);
            if (Workflow.allFilesExist(stage4_2Last7DaysFiles)) {
                CAReducer reducerCAGlobalFor7J = new CAReducer(stage4_2Last7DaysFiles, topN, stage5_2File, FileBuilder.createCAGlobal7JFile(dateString, topN));
                reducerCAGlobalFor7J.reduce();
            } else {
                System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage5_2 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void oneTransactionFileModeJob(File transactionFile, int topN) {

        /*
        Process given transaction file.
         */
        String dateString = FilenameUtil.extractDate(transactionFile.getName());
        Set<String> magasinsIdInTransactionFile = Workflow.computeStage1(transactionFile) ;




        /*
        For each magasin present in transaction file for date D, compute VENTES and CA files
         */
        for (Iterator<String> it = magasinsIdInTransactionFile.iterator(); it.hasNext(); ) {
            String magasinId = it.next();
            //TODO use Map/Set produced
            //TODO refactor CA/Qte REducers
            /*
            Compute stage2 file :
             */
            Workflow.computeStage2(magasinId,dateString, topN);

            /*
            Compute stage3 file
             */
            Workflow.computeStage3(magasinId,dateString,topN);

            /*
            Process Stage 4_3 file
            */
            Workflow.computeStage4_3(magasinId,dateString,topN);

            /*
            Process Stage 4_4 file
             */
            Workflow.computeStage4_4(magasinId,dateString,topN);

        }

        /*
        Process stage4_1 file
         */
        Workflow.computeStage4_1(magasinsIdInTransactionFile, dateString,topN);


        /*
        Process 4_2 file
         */
        Workflow.computeStage4_2(magasinsIdInTransactionFile,dateString,topN);




        /*
        Process 5_1 file
         */
        Workflow.computeStage5_1(dateString,topN);


        /*
        Process 5_2 file
         */
        Workflow.computeStage5_2(dateString,topN);


    }

    private static boolean allFilesExist(Set<File> files) {
        boolean allFilesExists = true;
        for (Iterator<File> it = files.iterator(); it.hasNext();) {
            File currFile = it.next() ;
            allFilesExists = allFilesExists && currFile.exists() ;
        }
        return allFilesExists;

    }

    public static void main(String[] args) throws IOException {
        /*String log4jConfigFile = System.getProperty("user.dir")
                + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        logger.debug("this is a debug log message");
        logger.info("this is a information log message");
        logger.warn("this is a warning log message");*/

        File transactionFile1 = new File("data","transactions_20170508.data") ;
        File transactionFile2 = new File("data","transactions_20170509.data") ;
        File transactionFile3 = new File("data","transactions_20170510.data") ;
        File transactionFile4 = new File("data","transactions_20170511.data") ;
        File transactionFile5 = new File("data","transactions_20170512.data") ;
        File transactionFile6 = new File("data","transactions_20170513.data") ;
        File transactionFile7 = new File("data","transactions_20170514.data") ;

        List<File> allFiles = new ArrayList<>();

        allFiles.add(transactionFile1) ;
        allFiles.add(transactionFile2) ;
        allFiles.add(transactionFile3) ;
        allFiles.add(transactionFile4) ;
        allFiles.add(transactionFile5) ;
        allFiles.add(transactionFile6) ;
        allFiles.add(transactionFile7) ;

        for (Iterator<File> itFile = allFiles.iterator(); itFile.hasNext();) {
            File currFile = itFile.next() ;
            long start = System.currentTimeMillis();

            Workflow.oneTransactionFileModeJob(currFile,100);
            long end = System.currentTimeMillis();
            System.out.println("Workflow took " + String.valueOf(end-start) + "ms for transaction file " + currFile.getName());
        }




        /*System.out.println(transactionFile.getName());
        System.out.println(transactionFile.getAbsolutePath());
        System.out.println(transactionFile.getCanonicalPath());
        System.out.println(transactionFile.getPath());
        System.out.println(transactionFile.getParent());*/

        /*TransactionFileMapper mapper = new TransactionFileMapper(transactionFile) ;
        if (FilenameUtil.extractDate(transactionFile.getName()) != null) {
            mapper.processTransactionFile();
        }*/




        /*
         * Modes :
         *
         * 1) On donne le chemin d'un fichier de transaction, ça calcule tout ce que ça peut
         * 2) On donne un magasin, une date et ça calcule tout ce que ça peut
         * 3) Automatique : on met un fichier dans le répertoire de données, ça le consomme et calcule tout ce que ça peut calculer.
         *
         *  WORKFLOW :
         *
         *  TransactionFileMapper
         *  foreach magasinId
         *      Stage1Reducer
         *      Stage2Processor
         *
         *
         *
         *
         *
         */

    }
}

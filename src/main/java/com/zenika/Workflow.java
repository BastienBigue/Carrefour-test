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

    public static void oneTransactionFileModeJob(File transactionFile, int topN) {

        /*
        Process given transaction file.
         */
        TransactionFileMapper mapper = new TransactionFileMapper(transactionFile) ;
        String dateString = FilenameUtil.extractDate(transactionFile.getName());
        Set<String> magasinsId = mapper.processTransactionFile();




        /*
        For each magasin present in transaction file for date D, compute VENTES and CA files
         */
        for (Iterator<String> it = magasinsId.iterator(); it.hasNext();) {
            String magasinId = it.next() ;

            Set<File> magasinFileSet = new HashSet<>() ;
            magasinFileSet.add(FileBuilder.createStage1File(magasinId, dateString));

            /*
            Compute stage2 file :
             */
            File stage2File = FileBuilder.createStage2File(magasinId, dateString) ;
            QteReducer reducerQtePerMagasin = new QteReducer(magasinFileSet, topN,
                    stage2File,
                    FileBuilder.createVenteMagasinFile(magasinId,dateString,topN));
            Map<String, Integer> productQteMap = reducerQtePerMagasin.reduce();


            /*
            Compute stage3 file
             */
            Stage2Processor processor1 = new Stage2Processor(magasinId, dateString,topN) ;
            processor1.process();

            /*
            Process Stage 4_3 file
            */
            System.out.println("Starting compute of Stage4_3 ");
            File stage4_3File = FileBuilder.createStage4_3File(magasinId,dateString) ;

            try {
                Set<File> stage2Last7DaysFiles  = FileBuilder.createStage2Last7DaysFiles(magasinId, dateString);
                System.out.println("here");
                if (Workflow.allFilesExist(stage2Last7DaysFiles)) {
                    System.out.println("here");
                    QteReducer reducerQteFor7J = new QteReducer(stage2Last7DaysFiles, topN, stage4_3File, FileBuilder.createVenteMagasin7JFile(magasinId, dateString, topN));
                    reducerQteFor7J.reduce();
                } else {
                    System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_3 ");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            Process Stage 4_4 file
             */
            //Voir comment faire un CAReducer
        }

        /*
        Process Stage 4_1 file
         */
        System.out.println("Starting compute of Stage4_1 ");
        Set<File> allMagasinsFiles = new HashSet<>() ;
        for (Iterator<String> it = magasinsId.iterator(); it.hasNext();) {
            String magasin = it.next();
            allMagasinsFiles.add(FileBuilder.createStage2File(magasin,dateString)) ;
        }
        if (allFilesExist(allMagasinsFiles)) {
            File stage4_1File = FileBuilder.createStage4_1File(dateString);
            QteReducer reducerVenteGlobal = new QteReducer(allMagasinsFiles , topN, stage4_1File, FileBuilder.createVenteGlobalFile(dateString, topN));
            reducerVenteGlobal.reduce();
        } else {
            System.out.println("Tous les fichiers n'existent pas pour créer le fichier stage4_1 ");
        }

        /*
        Process 4_2 file
         */
        //Voir comment faire un CAReducer

        /*
        Process 5_1 file
         */
        System.out.println("Starting compute of Stage5_1 ");
        File stage5_1File = FileBuilder.createStage4_1File(dateString);
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


        /*
        Process 5_2 file
         */
        //Voir comment faire un CAReducer





    }

    private static boolean allFilesExist(Set<File> files) {
        boolean allFilesExists = true;
        for (Iterator<File> it = files.iterator(); it.hasNext();) {
            File currFile = it.next() ;
            allFilesExists = allFilesExists && currFile.exists() ;
        }
        System.out.println(allFilesExists);
        return allFilesExists;

    }

    public static void main(String[] args) throws IOException {
        /*String log4jConfigFile = System.getProperty("user.dir")
                + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        logger.debug("this is a debug log message");
        logger.info("this is a information log message");
        logger.warn("this is a warning log message");*/

        File transactionFile = new File("data","transactions_20170514.data") ;

        long start = System.currentTimeMillis();

        Workflow.oneTransactionFileModeJob(transactionFile,100);
        long end = System.currentTimeMillis();
        System.out.println("workflow took " + String.valueOf(end-start) + "ms");


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

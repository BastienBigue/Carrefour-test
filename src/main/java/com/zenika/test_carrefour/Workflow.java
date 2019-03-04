package com.zenika.test_carrefour;

import com.zenika.test_carrefour.mappers.CAMapper;
import com.zenika.test_carrefour.mappers.TransactionFileMapper;
import com.zenika.test_carrefour.reducers.CAReducer;
import com.zenika.test_carrefour.reducers.QteReducer;
import com.zenika.test_carrefour.utils.FileBuilder;
import com.zenika.test_carrefour.utils.FilenameUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Workflow {

    static Logger log = LogManager.getLogger(Workflow.class);

    public static Set<String>  computeStage1(File transactionFile) {
        log.info("Starting compute of Stage1 with input file " + transactionFile.getName());
        long start = System.currentTimeMillis();

        TransactionFileMapper mapper = new TransactionFileMapper(transactionFile);
        Set<String> magasinsId = mapper.processTransactionFile();

        long end = System.currentTimeMillis();
        log.info("Compute of Stage1 done");
        log.debug("computeStage1 took " + String.valueOf(end-start) + " ms");
        return magasinsId;
    }

    public static Map<String, Integer> computeStage2(String magasinId, String dateString, int topN) {
        log.info("Starting compute of Stage2 for store " + magasinId + " ; date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage1File = FileBuilder.createStage1File(magasinId, dateString);
        Set<File> stage1FileSet = new HashSet<File>();
        stage1FileSet.add(stage1File) ;

        File stage2File = FileBuilder.createStage2File(magasinId, dateString);
        QteReducer reducerQtePerMagasin = new QteReducer(stage1FileSet, topN,
                stage2File,
                FileBuilder.createVenteMagasinFile(magasinId, dateString, topN));
        Map<String, Integer> productQteMap = reducerQtePerMagasin.reduce();

        long end = System.currentTimeMillis();
        log.info("Compute of Stage2 done");
        log.debug("computeStage2 took " + String.valueOf(end-start) + " ms");
        return productQteMap;
    }

    public static Map<String, Float> computeStage3(String magasinId, String dateString, int topN) {
        log.info("Starting compute of Stage3 for store " + magasinId + " ; date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage3File = FileBuilder.createStage3File(magasinId,dateString);
        File setProduitFile = FileBuilder.createStage2File(magasinId,dateString) ;
        File refPrixFile = FileBuilder.createReferenceProdFile(magasinId,dateString) ;

        CAMapper processor = new CAMapper(refPrixFile, setProduitFile, topN, stage3File, FileBuilder.createCAMagasinFile(magasinId,dateString,topN));
        Map<String, Float> productCAMap = processor.process();

        long end = System.currentTimeMillis();
        log.info("Compute of Stage3 done");
        log.debug("computeStage3 took " + String.valueOf(end-start) + " ms");
        return productCAMap ;
    }

    public static Map<String, Integer> computeStage4_3(String magasinId, String dateString, int topN) {
        log.info("Starting compute of Stage4_3 for store " + magasinId + " ; date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage4_3File = FileBuilder.createStage4_3File(magasinId, dateString);
        Map<String, Integer> productVenteGlobal7JMap = null ;
        Set<File> stage2Last7DaysFiles = null;
        try {
            stage2Last7DaysFiles = FileBuilder.createStage2Last7DaysFiles(magasinId, dateString);
        } catch (Exception e) {
            log.error("Error in createStage2Last7DaysFiles : files have not been created -- Exit") ;
            e.printStackTrace();
            System.exit(1);
        }

        if (Workflow.allFilesExist(stage2Last7DaysFiles)) {
            QteReducer reducerQteFor7J = new QteReducer(stage2Last7DaysFiles, topN, stage4_3File, FileBuilder.createVenteMagasin7JFile(magasinId, dateString, topN));
            productVenteGlobal7JMap = reducerQteFor7J.reduce();

            long end = System.currentTimeMillis();
            log.info("Compute of Stage4_3 done");
            log.debug("computeStage4_3 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage4_3 file (VenteMagasin7J)");
        }
        return productVenteGlobal7JMap;
    }

    public static Map<String, Float> computeStage4_4(String magasinId, String dateString, int topN) {
        log.info("Starting compute of Stage4_4 for store " + magasinId + " ; date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage4_4File = FileBuilder.createStage4_4File(magasinId, dateString);
        Map<String, Float> productCAGlobal7JMap = null ;
        Set<File> stage3Last7DaysFiles = null ;
        try {
            stage3Last7DaysFiles = FileBuilder.createStage3Last7DaysFiles(magasinId, dateString);
        } catch (Exception e) {
            log.error("Error in createStage3Last7DaysFiles : files have not been created -- Exit") ;
            e.printStackTrace();
            System.exit(1);
        }

        if (Workflow.allFilesExist(stage3Last7DaysFiles)) {
            CAReducer reducerCAFor7J = new CAReducer(stage3Last7DaysFiles, topN, stage4_4File, FileBuilder.createCAMagasin7JFile(magasinId, dateString, topN));
            productCAGlobal7JMap = reducerCAFor7J.reduce();
            long end = System.currentTimeMillis();
            log.info("Compute of Stage4_4 done");
            log.debug("computeStage4_4 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage4_4 file (CAMagasin7J)");
        }
        return productCAGlobal7JMap;
    }

    public static Map<String, Integer> computeStage4_1(Set<String> magasinsIdToGlobal, String dateString, int topN) {
        log.info("Starting compute of Stage4_1 for date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

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
            long end = System.currentTimeMillis();
            log.info("Compute of Stage4_1 done");
            log.debug("computeStage4_1 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage4_1 file (VenteGlobal)");
        }
        return productVenteGlobal ;
    }

    public static Map<String, Float> computeStage4_2(Set<String> magasinsIdToGlobal, String dateString, int topN) {
        log.info("Starting compute of Stage4_2 for date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

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
            long end = System.currentTimeMillis();
            log.info("Compute of Stage4_2 done");
            log.debug("computeStage4_2 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage4_2 file (CAGlobal)");
        }
        return productCAGlobal;
    }

    public static void computeStage5_1(String dateString, int topN) {
        log.info("Starting compute of Stage5_1 for date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage5_1File = FileBuilder.createStage5_1File(dateString);
        Set<File> stage4_1Last7DaysFiles = null ;
        try {
            stage4_1Last7DaysFiles = FileBuilder.createStage4_1Last7DaysFiles(dateString);
        } catch (Exception e) {
            log.error("Error in createStage4_1Last7DaysFiles : files have not been created -- Exit") ;
            e.printStackTrace();
            System.exit(1);
        }

        if (Workflow.allFilesExist(stage4_1Last7DaysFiles)) {
            QteReducer reducerQteGlobalFor7J = new QteReducer(stage4_1Last7DaysFiles, topN, stage5_1File, FileBuilder.createVenteGlobal7JFile(dateString, topN));
            reducerQteGlobalFor7J.reduce();
            long end = System.currentTimeMillis();
            log.info("Compute of Stage5_1 done");
            log.debug("computeStage5_1 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage5_1 file (VenteGlobal7J)");
        }


    }

    public static void computeStage5_2(String dateString, int topN) {
        log.info("Starting compute of Stage5_2 for date " + dateString + " ; topN " + topN);
        long start = System.currentTimeMillis();

        File stage5_2File = FileBuilder.createStage5_2File(dateString);
        Set<File> stage4_2Last7DaysFiles = null ;
        try {
            stage4_2Last7DaysFiles = FileBuilder.createStage4_2Last7DaysFiles(dateString);
        } catch (Exception e) {
            log.error("Error in createStage4_2Last7DaysFiles : files have not been created -- Exit") ;
            e.printStackTrace();
            System.exit(1);
        }

        if (Workflow.allFilesExist(stage4_2Last7DaysFiles)) {
            CAReducer reducerCAGlobalFor7J = new CAReducer(stage4_2Last7DaysFiles, topN, stage5_2File, FileBuilder.createCAGlobal7JFile(dateString, topN));
            reducerCAGlobalFor7J.reduce();
            long end = System.currentTimeMillis();
            log.info("Compute of Stage5_2 done");
            log.debug("computeStage5_2 took " + String.valueOf(end-start) + " ms");
        } else {
            log.info("There are files missing to create stage5_2 file (CAGlobal7J)");
        }
    }



    public static void oneTransactionFileModeJob(File transactionFile, int topN) {


        String dateString = FilenameUtil.extractDate(transactionFile.getName());
        Set<String> magasinsIdInTransactionFile = Workflow.computeStage1(transactionFile) ;

        for (Iterator<String> it = magasinsIdInTransactionFile.iterator(); it.hasNext(); ) {
            String magasinId = it.next();
            //TODO use Map/Set produced
            //TODO refactor CA/Qte REducers

            Workflow.computeStage2(magasinId,dateString, topN);
            Workflow.computeStage3(magasinId,dateString,topN);
            Workflow.computeStage4_3(magasinId,dateString,topN);
            Workflow.computeStage4_4(magasinId,dateString,topN);

        }
        Workflow.computeStage4_1(magasinsIdInTransactionFile, dateString,topN);
        Workflow.computeStage4_2(magasinsIdInTransactionFile,dateString,topN);
        Workflow.computeStage5_1(dateString,topN);
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
                + File.separator + "log4j2.xml";
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
         *      CAMapper
         *
         *
         *
         *
         *
         */

    }
}

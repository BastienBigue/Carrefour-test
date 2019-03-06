package com.zenika.test_carrefour.utils;

import com.zenika.test_carrefour.config.CommonConfig;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.Set;
import java.util.HashSet;


// This class builds all File objects and creates the necessary directory .
// It uses FilenameUtil to build any necessary type of file (any stage, transactions, reference-prod, results).
// Depending on type of file, it may/may not need the magasinId and the topN value.
// Date is mandatory.
public class FileBuilder {

    /*
    Example data files
     */
    public static File createReferenceProdFile(String magasin, String date) {
        File parent = new File(FilenameUtil.DATA) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.REF_PROD)) ;
    }

    public static File createTransactionFile(String date) {
        File parent = new File(FilenameUtil.DATA) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date,FilenameUtil.FileType.TRANSACTION_FILE));
    }


    /*
    Intermediary stages files
     */
    public static File createStage1File(String magasin, String date) {
        File parent = new File(FilenameUtil.STAGE1) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE1)) ;
    }

    public static File createStage2File(String magasin, String date) {
        File parent = new File(FilenameUtil.STAGE2) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE2)) ;
    }

    public static File createStage3File(String magasin, String date) {
        File parent = new File(FilenameUtil.STAGE3) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE3)) ;
    }

    public static File createStage4_1File(String date) {
        File parent = new File(FilenameUtil.STAGE4_1) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE4_1)) ;
    }

    public static File createStage4_2File(String date) {
        File parent = new File(FilenameUtil.STAGE4_2) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE4_2)) ;
    }

    public static File createStage4_3File(String magasin, String date) {
        File parent = new File(FilenameUtil.STAGE4_3) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE4_3)) ;
    }

    public static File createStage4_4File(String magasin, String date) {
        File parent = new File(FilenameUtil.STAGE4_4) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE4_4)) ;
    }

    public static File createStage5_1File(String date) {
        File parent = new File(FilenameUtil.STAGE5_1) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE5_1)) ;
    }

    public static File createStage5_2File(String date) {
        File parent = new File(FilenameUtil.STAGE5_2) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE5_2)) ;
    }

    /*
    This function creates
     */
    private static Set<File> createStageNLast7DaysFiles(String magasinsId, String dateString, FilenameUtil.FileType fileType) throws Exception {

        Set<File> lastSevenDays = new HashSet<>() ;
        DateFormat dateFormat = new SimpleDateFormat(CommonConfig.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(CommonConfig.TIMEZONE));
        Date date = dateFormat.parse(dateString) ;
        Instant instant = date.toInstant();

        for (int i=0 ; i<7 ; i++) {
            String priorDate = dateFormat.format(Date.from(instant.minusSeconds(CommonConfig.SECONDS_PER_DAY *i)));
            File file ;
            if (fileType == FilenameUtil.FileType.STAGE2) {
                file = FileBuilder.createStage2File(magasinsId,priorDate);
            } else if (fileType == FilenameUtil.FileType.STAGE3) {
                file = FileBuilder.createStage3File(magasinsId,priorDate);
            } else if (fileType == FilenameUtil.FileType.STAGE4_1) {
                file = FileBuilder.createStage4_1File(priorDate);
            } else if (fileType == FilenameUtil.FileType.STAGE4_2) {
                file = FileBuilder.createStage4_2File(priorDate);
            } else {
                throw new Exception("Error in createStageNLast7DaysFiles, unknown FilenameUtil.FileType provided") ;
            }
            lastSevenDays.add(file);
        }
        return lastSevenDays;
    }

    public static Set<File> createStage2Last7DaysFiles(String magasinsId, String dateString) throws Exception {
        return FileBuilder.createStageNLast7DaysFiles(magasinsId,dateString, FilenameUtil.FileType.STAGE2);
    }

    public static Set<File> createStage3Last7DaysFiles(String magasinsId, String dateString) throws Exception {
        return FileBuilder.createStageNLast7DaysFiles(magasinsId,dateString, FilenameUtil.FileType.STAGE3);
    }

    public static Set<File> createStage4_1Last7DaysFiles(String dateString) throws Exception {
        return FileBuilder.createStageNLast7DaysFiles(null,dateString, FilenameUtil.FileType.STAGE4_1);
    }

    public static Set<File> createStage4_2Last7DaysFiles(String dateString) throws Exception {
        return FileBuilder.createStageNLast7DaysFiles(null,dateString, FilenameUtil.FileType.STAGE4_2);
    }

    /*
    Results files
     */
    public static File createVenteMagasinFile(String magasin, String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_VENTES_MAGASIN, topN)) ;
    }

    public static File createCAMagasinFile(String magasin, String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_CA_MAGASIN, topN)) ;
    }

    public static File createVenteGlobalFile(String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_VENTES_GLOBAL, topN)) ;
    }

    public static File createCAGlobalFile(String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_CA_GLOBAL, topN)) ;
    }

    public static File createVenteMagasin7JFile(String magasin, String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_VENTES_MAGASIN_7J, topN)) ;
    }

    public static File createCAMagasin7JFile(String magasin, String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_CA_MAGASIN_7J, topN)) ;
    }

    public static File createVenteGlobal7JFile(String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_VENTES_GLOBAL_7J, topN)) ;
    }

    public static File createCAGlobal7JFile(String date, int topN) {
        File parent = new File(FilenameUtil.RESULT) ;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_CA_GLOBAL_7J, topN)) ;
    }
}

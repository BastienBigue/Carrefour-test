package main.java.com.zenika.utils;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class FileBuilder {

    private static final int SECONDS_PER_DAY = 86400 ;

    public static File createReferenceProdFile(String magasin, String date) {
        return new File(FilenameUtil.DATA, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.REF_PROD)) ;
    }

    /*
    FULL FILES, NOT SORTED
     */

    public static File createStage1File(String magasin, String date) {
        return new File(FilenameUtil.STAGE1, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE1)) ;
    }

    public static File createStage2File(String magasin, String date) {
        return new File(FilenameUtil.STAGE2, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE2)) ;
    }

    public static File createStage3File(String magasin, String date) {
        return new File(FilenameUtil.STAGE3, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE3)) ;
    }

    public static File createStage4_1File(String date) {
        return new File(FilenameUtil.STAGE4_1, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE4_1)) ;
    }

    public static File createStage4_2File(String date) {
        return new File(FilenameUtil.STAGE4_2, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE4_2)) ;
    }

    public static File createStage4_3File(String magasin, String date) {
        return new File(FilenameUtil.STAGE4_3, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE4_3)) ;
    }

    public static File createStage4_4File(String magasin, String date) {
        return new File(FilenameUtil.STAGE4_4, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.STAGE4_4)) ;
    }

    public static File createStage5_1File(String date) {
        return new File(FilenameUtil.STAGE5_1, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE5_1)) ;
    }

    public static File createStage5_2File(String date) {
        return new File(FilenameUtil.STAGE5_2, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.STAGE5_2)) ;
    }

    /*
    RESULTS FILES
     */

    public static File createVenteMagasinFile(String magasin, String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_VENTES_MAGASIN, topN)) ;
    }

    public static File createCAMagasinFile(String magasin, String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_CA_MAGASIN, topN)) ;
    }

    public static File createVenteGlobalFile(String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_VENTES_GLOBAL, topN)) ;
    }

    public static File createCAGlobalFile(String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_CA_GLOBAL, topN)) ;
    }

    public static File createVenteMagasin7JFile(String magasin, String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_VENTES_MAGASIN_7J, topN)) ;
    }

    public static File createCAMagasin7JFile(String magasin, String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(magasin,date, FilenameUtil.FileType.RESULT_CA_MAGASIN_7J, topN)) ;
    }

    public static File createVenteGlobal7JFile(String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_VENTES_GLOBAL_7J, topN)) ;
    }

    public static File createCAGlobal7JFile(String date, int topN) {
        return new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(null,date, FilenameUtil.FileType.RESULT_CA_GLOBAL_7J, topN)) ;
    }

    private static Set<File> createStageNLast7DaysFiles(String magasinsId, String dateString, FilenameUtil.FileType fileType) throws Exception {

        Set<File> lastSevenDays = new HashSet<>() ;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date date = dateFormat.parse(dateString) ;
        Instant instant = date.toInstant();

        for (int i=0 ; i<7 ; i++) {
            String priorDate = dateFormat.format(Date.from(instant.minusSeconds(SECONDS_PER_DAY*i)));
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
                throw new Exception() ;
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

}

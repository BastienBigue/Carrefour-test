package main.java.com.zenika.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameUtil {

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd") ;

    private static String DATE_REGEXP = "([0-9]{8})\\." ;
    private static String MAGASINID_REGEXP = "([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})" ;

    public static String FILENAME_SEPARATOR = "_" ;
    public static String DOT = "." ;
    public static String HYPHEN = "-" ;
    public static String TRANSACTIONS = "transactions" ;
    public static String REFERENCE_PROD = "reference_prod" ;
    public static String LISTING_PRODUIT = "listing_produit" ;
    public static String SET_PRODUIT = "set_produit" ;
    public static String SET_CA = "set_ca" ;
    public static String DATA = "data" ;
    public static String STAGE1 = "stage1" ;
    public static String STAGE2 = "stage2" ;
    public static String STAGE3 = "stage3" ;
    public static String STAGE4_1 = "stage4-1" ;
    public static String STAGE4_2 = "stage4-2" ;
    public static String STAGE4_3 = "stage4-3" ;
    public static String STAGE4_4 = "stage4-4" ;
    public static String STAGE5_1 = "stage5-1" ;
    public static String STAGE5_2 = "stage5-2" ;
    public static String TOP = "top" ;
    public static String GLOBAL = "GLOBAL" ;
    public static String J7 = "J7" ;
    public static String CA = "ca" ;
    public static String VENTES = "ventes" ;
    public static String RESULT = "result" ;



    public enum FileType {
        TRANSACTION_FILE, REF_PROD, STAGE1, STAGE2, STAGE3, STAGE4_1, STAGE4_2, STAGE4_3, STAGE4_4, STAGE5_1, STAGE5_2, RESULT_VENTES_MAGASIN, RESULT_CA_MAGASIN, RESULT_VENTES_GLOBAL, RESULT_CA_GLOBAL, RESULT_VENTES_MAGASIN_7J, RESULT_CA_MAGASIN_7J, RESULT_VENTES_GLOBAL_7J, RESULT_CA_GLOBAL_7J,
    }

    private static Pattern dateRegExpPattern = Pattern.compile(DATE_REGEXP) ;
    private static Pattern uuidRegExpPattern = Pattern.compile(MAGASINID_REGEXP) ;

    public static String extractDate(String filename) {
        return FilenameUtil.matchRegexp(filename, dateRegExpPattern);
    }

    public static String extractMagasinId(String filename) {
        return FilenameUtil.matchRegexp(filename, uuidRegExpPattern) ;
    }

    private static String matchRegexp(String filename, Pattern pattern) {
        Matcher matcher = pattern.matcher(filename) ;
        String match = null;
        if (matcher.find()) {
            match = matcher.group(1) ;
        }
        return match ;
    }

    public static String buildFileName(String magasinId, String date, FileType type) {

        if (date != null) {
            if (type == FileType.TRANSACTION_FILE) {
                return TRANSACTIONS.concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.REF_PROD && magasinId != null) {
                return REFERENCE_PROD.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.STAGE1 && magasinId != null) {
                return LISTING_PRODUIT.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(STAGE1);
            } else if (type == FileType.STAGE2 && magasinId != null) {
                return SET_PRODUIT.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(STAGE2);
            } else if (type == FileType.STAGE3 && magasinId != null) {
                return SET_CA.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(STAGE3);
            } else if (type == FileType.STAGE4_1) {
                return SET_PRODUIT.concat(HYPHEN).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(STAGE4_1);
            } else if (type == FileType.STAGE4_2) {
                return SET_CA.concat(HYPHEN).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(STAGE4_2);
            } else if (type == FileType.STAGE4_3 && magasinId != null) {
                return SET_PRODUIT.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(STAGE4_3);
            } else if (type == FileType.STAGE4_4 && magasinId != null) {
                return SET_CA.concat(HYPHEN).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(STAGE4_4);
            } else if (type == FileType.STAGE5_1) {
                return SET_PRODUIT.concat(HYPHEN).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(STAGE5_1);
            } else if (type == FileType.STAGE5_2) {
                return SET_CA.concat(HYPHEN).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(STAGE5_2);
            } else {
                System.out.println("Error in filename creation");
                return "";
            }
        }
        return "";
    }

    public static String buildFileName(String magasinId, String date, FileType type, int topN) {
        if (date != null) {
            if (type == FileType.RESULT_VENTES_MAGASIN && magasinId != null)  {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(VENTES).concat(FILENAME_SEPARATOR).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_CA_MAGASIN && magasinId != null) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(CA).concat(FILENAME_SEPARATOR).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_VENTES_GLOBAL) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(VENTES).concat(FILENAME_SEPARATOR).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_CA_GLOBAL) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(CA).concat(FILENAME_SEPARATOR).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_VENTES_MAGASIN_7J && magasinId != null) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(VENTES).concat(FILENAME_SEPARATOR).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_CA_MAGASIN_7J && magasinId != null) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(CA).concat(FILENAME_SEPARATOR).concat(magasinId).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_VENTES_GLOBAL_7J) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(VENTES).concat(FILENAME_SEPARATOR).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(DATA);
            } else if (type == FileType.RESULT_CA_GLOBAL_7J) {
                return TOP.concat(FILENAME_SEPARATOR).concat(String.valueOf(topN)).concat(FILENAME_SEPARATOR).concat(CA).concat(FILENAME_SEPARATOR).concat(GLOBAL).concat(FILENAME_SEPARATOR).concat(date).concat(HYPHEN).concat(J7).concat(DOT).concat(DATA);
            } else {
                System.out.println("Error in filename creation");
                return "" ;
            }
        }
        return "" ;
    }
}

package main.java.com.zenika.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameUtil {

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd") ;

    private static String DATE_REGEXP = "([0-9]{8})\\." ;
    private static String MAGASINID_REGEXP = "([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})" ;

    private static Pattern dateRegExpPattern = Pattern.compile(DATE_REGEXP) ;
    private static Pattern uuidRegExpPattern = Pattern.compile(MAGASINID_REGEXP) ;

    public static String extractDate(String filename) {
        return FilenameUtil.buildAndMatchRegexp(filename, dateRegExpPattern);
    }

    public static String extractMagasinId(String filename) {
        return FilenameUtil.buildAndMatchRegexp(filename, uuidRegExpPattern) ;
    }

    private static String buildAndMatchRegexp(String filename, Pattern pattern) {
        Matcher matcher = pattern.matcher(filename) ;
        String match = null;
        if (matcher.find()) {
            match = matcher.group(1) ;
        }
        return match ;
    }



}

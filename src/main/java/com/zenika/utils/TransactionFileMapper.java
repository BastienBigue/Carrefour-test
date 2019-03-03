package com.zenika.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionFileMapper {

    private static String STAGE_1_SUBDIRECTORY = "stage1" ;

    private File file ;
    private String date ;
    // private DateFormat dateFormat ;
    private Map<String, BufferedOutputStream> streamMap ;

    // Noms de fichiers : transactions_YYYYMMDD.data
    // txId|datetime|magasin|produit|qte

    public TransactionFileMapper(File file) {
        this.file = file ;
        this.streamMap = new HashMap<>() ;
        this.date = FilenameUtil.extractDate(this.file.getName()) ;
        // this.dateFormat = new SimpleDateFormat("yyyyMMdd") ;
    }

    public void processTransactionFile() throws IOException {

        File stage1Directory = new File(STAGE_1_SUBDIRECTORY) ;
        if (!stage1Directory.exists()) {
            stage1Directory.mkdir();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                // Est-ce que je crée pas des objet String[] et String a chaque itération là ?
                String[] currentLine = line.split("\\|") ;
                String magasin = currentLine[2] ;
                String produit = currentLine[3] ;
                String qte = currentLine[4] ;
                BufferedOutputStream outputStream = this.streamMap.get(magasin) ;
                if (outputStream == null) {
                    File currFile = new File(STAGE_1_SUBDIRECTORY, "listing_produit-".concat(magasin).concat("_").concat(this.date).concat(".stage1")) ;
                    currFile.createNewFile() ;
                    outputStream = new BufferedOutputStream(new FileOutputStream(currFile)) ;
                    this.streamMap.put(magasin, outputStream) ;
                }
                String outputLine = produit.concat("|").concat(qte) ;
                outputStream.write(outputLine.getBytes());
                outputStream.write(System.lineSeparator().getBytes());
            }

            for (BufferedOutputStream currentBuff : this.streamMap.values()) {
                currentBuff.close();
            }
        }
    }
}

package com.zenika.mappers;

import com.zenika.config.CommonConfig;
import com.zenika.utils.FileBuilder;
import com.zenika.utils.FilenameUtil;

import java.io.File;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransactionFileMapper {

    private File file ;
    private String date ;
    private Map<String, BufferedOutputStream> streamMap ;

    public TransactionFileMapper(File file) {
        this.file = file ;
        this.streamMap = new HashMap<>() ;
        this.date = FilenameUtil.extractDate(this.file.getName()) ;
    }

    public Set<String> processTransactionFile() {

        File stage1Directory = new File(CommonConfig.STAGE_1_SUBDIRECTORY) ;
        if (!stage1Directory.exists()) {
            stage1Directory.mkdirs();
        }

        String magasin;
        String produit;
        String[] currentLine;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                currentLine = line.split("\\|") ;
                magasin = currentLine[2] ;
                produit = currentLine[3] ;
                String qte = currentLine[4] ;
                BufferedOutputStream outputStream = this.streamMap.get(magasin) ;
                if (outputStream == null) {
                    File currFile = FileBuilder.createStage1File(magasin,date) ;
                    outputStream = new BufferedOutputStream(new FileOutputStream(currFile)) ;
                    this.streamMap.put(magasin, outputStream) ;
                }
                String outputLine = produit.concat(CommonConfig.CSV_SEPARATOR).concat(qte) ;
                outputStream.write(outputLine.getBytes());
                outputStream.write(System.lineSeparator().getBytes());
            }

            for (BufferedOutputStream currentBuff : this.streamMap.values()) {
                currentBuff.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return this.streamMap.keySet();
    }

  /*  public static void main(String[] args) {
        File transactionFile = new File(DATA_BASTIEN_SUBDIRECTORY,"transactions_20190302.data") ;

        TransactionFileMapper mapper = new TransactionFileMapper(transactionFile) ;
        if (FilenameUtil.extractDate(transactionFile.getName()) != null) {
                long start = System.currentTimeMillis() ;
                mapper.processTransactionFile();
                long end = System.currentTimeMillis() ;
                System.out.println("TransactionFile mapper = " + String.valueOf(end-start) + "ms");
        }
    }*/
}

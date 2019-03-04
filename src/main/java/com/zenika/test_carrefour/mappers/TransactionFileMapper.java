package com.zenika.test_carrefour.mappers;

import com.zenika.test_carrefour.config.CommonConfig;
import com.zenika.test_carrefour.utils.FileBuilder;
import com.zenika.test_carrefour.utils.FilenameUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TransactionFileMapper {

    static Logger log = LogManager.getLogger(TransactionFileMapper.class);

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
        } catch (FileNotFoundException e) {
            log.error("Error when processing transactionFile : could not find input or output file -- Exit");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when reading " + this.file + "or when writing to one of the stage2 output files -- Exit");
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

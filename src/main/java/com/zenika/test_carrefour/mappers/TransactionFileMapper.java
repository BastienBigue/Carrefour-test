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

    private static Logger LOG = LogManager.getLogger(TransactionFileMapper.class);

    private final static int INITIAL_CAPACITY = 2048; // 1200 magasins

    private File file ;
    private String date ;
    private Map<String, BufferedOutputStream> streamMap ;

    public TransactionFileMapper(File file) {
        this.file = file ;
        this.streamMap = new HashMap<>(INITIAL_CAPACITY) ;
        this.date = FilenameUtil.extractDate(this.file.getName()) ;
    }

    //Read TransactionFile, maintains a Map<magasinId, BufferedOutputStream> and maps each input line (only produit|qte) to appropriate output file.
    public Set<String> processTransactionFile() {

        String magasin;
        String produit;
        String[] currentLine;
        String qte ;
        String outputLine ;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                currentLine = line.split("\\|") ;
                magasin = currentLine[2] ;
                produit = currentLine[3] ;
                qte = currentLine[4] ;
                BufferedOutputStream outputStream = this.streamMap.get(magasin) ;
                if (outputStream == null) {
                    File currFile = FileBuilder.createStage1File(magasin,date) ;
                    outputStream = new BufferedOutputStream(new FileOutputStream(currFile)) ;
                    this.streamMap.put(magasin, outputStream) ;
                }
                outputLine = produit.concat(CommonConfig.CSV_SEPARATOR).concat(qte) ;
                outputStream.write(outputLine.getBytes());
                outputStream.write(System.lineSeparator().getBytes());
            }
        } catch (FileNotFoundException e) {
            LOG.error("Error when processing transactionFile : could not find input or output file -- Exit", e);
            System.exit(1);
        } catch (IOException e) {
            LOG.error("Error when reading " + this.file + "or when writing to one of the stage2 output files -- Exit", e);
            System.exit(1);
        } finally {
            closeWriters();
        }
        return this.streamMap.keySet();
    }

    private void closeWriters() {
        for (BufferedOutputStream currentBuff : this.streamMap.values()) {
            try {
                currentBuff.close();
            } catch (IOException e) {
                LOG.error("Error while closing buffered output stream");
            }
        }
    }
}

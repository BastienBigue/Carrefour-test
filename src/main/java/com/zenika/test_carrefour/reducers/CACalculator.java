package com.zenika.test_carrefour.reducers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class CACalculator extends FloatReducer {

    static Logger log = LogManager.getLogger(CACalculator.class);

    private File refPrixFile ;

    public CACalculator(File refPrixFile, Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
        this.refPrixFile = refPrixFile;
    }

    private void reBuildQteMap() {
        long start =  System.currentTimeMillis();
        String product;
        Float qte;
        String[] currentLine;


        if (this.filesToAggregate.size() != 1) {
            log.error("There must be only one file to join with the refPrixFile");
            System.exit(1);
        } else {
            File inputFile = this.filesToAggregate.iterator().next();


            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                for (String line; (line = br.readLine()) != null; ) {
                    currentLine = line.split("\\|");
                    parseAndInsertInMap(currentLine);
                }
            } catch (FileNotFoundException f) {
                log.error("CACalculator could not find file : " + inputFile.getName() + "-- Exit");
                f.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                log.error("Error when reading file " + inputFile.getName() + "-- Exit");
                e.printStackTrace();
                System.exit(1);
            }
        }
        long end =  System.currentTimeMillis();
        log.debug("reBuildQteMap using stage1 file took " + String.valueOf(end-start) + "ms");
    }

    private void buildCAMap() {
        long start = System.currentTimeMillis();
        String product;
        Float unitPrice ;
        String[] currentLine;

        HashSet<String> productToRemove = new HashSet<>(this.productMap.keySet()) ;

        try (BufferedReader br = new BufferedReader(new FileReader(refPrixFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                currentLine = line.split("\\|");
                product = currentLine[0];
                unitPrice = Float.valueOf(currentLine[1]);
                if (this.productMap.containsKey(product)) {
                    Float price = this.productMap.get(product) * unitPrice ;
                    this.productMap.put(product, price);
                    productToRemove.remove(product) ;
                }
            }
        } catch(FileNotFoundException f) {
            log.error("CACalculator could not find file : " + refPrixFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when reading file " + refPrixFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }

        for(Iterator<String> it = productToRemove.iterator(); it.hasNext();) {
            String unknownPrice = it.next() ;
            this.productMap.remove(unknownPrice) ;
            log.info("Removing product " + unknownPrice + "from list because no price has been found in " + this.refPrixFile + " prod file");
        }
        long end = System.currentTimeMillis();
        log.debug("Compute CA for all products took " + String.valueOf(end-start) + "ms");
    }


    public void process() {
        this.reBuildQteMap();
        this.buildCAMap();
        this.writeFullFile();
        String [] result = this.getTopN();
        this.writeSortedResultFile(result);
    }
}

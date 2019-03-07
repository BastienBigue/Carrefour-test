package com.zenika.test_carrefour.reducers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class CACalculator extends FloatReducer {

    private static Logger LOG = LogManager.getLogger(CACalculator.class);

    private File refPrixFile ;

    public CACalculator(File refPrixFile, Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
        this.refPrixFile = refPrixFile;
    }

    //Read a stage2 file (produit|qte) from one store. Rebuild the associated Map(produit|qte).
    private void reBuildQteMap() {
        long start =  System.currentTimeMillis();
        String[] currentLine;


        if (this.filesToAggregate.size() != 1) {
            LOG.error("There must be only one file to join with the refPrixFile");
            System.exit(1);
        } else {
            File inputFile = this.filesToAggregate.iterator().next();


            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                for (String line; (line = br.readLine()) != null; ) {
                    currentLine = line.split("\\|");
                    parseAndInsertInMap(currentLine);
                }
            } catch (FileNotFoundException f) {
                LOG.error("CACalculator could not find file : " + inputFile.getName() + "-- Exit", f);
                System.exit(1);
            } catch (IOException e) {
                LOG.error("Error when reading file " + inputFile.getName() + "-- Exit", e);
                System.exit(1);
            }
        }
        long end =  System.currentTimeMillis();
        LOG.debug("reBuildQteMap using stage1 file took " + String.valueOf(end-start) + "ms");
    }

    //Using Map<product, qte> and refPrixFile, computes CA for each product and stores it in the same map.
    //Maintains the list of products that have been found in refPrixFile. If a product is missing in this file, it is deleted from the map.
    //This avoids to have some records which are still <product, qte>.
    private void buildCAMap() {
        long start = System.currentTimeMillis();
        String product;
        Float unitPrice ;
        String[] currentLine;

        Set<String> productToRemove = new HashSet<>(this.productMap.keySet()) ;

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
                else {
                    LOG.debug("Product " + product + " is present in " + refPrixFile.getName() + " but is not part of transactions.");
                }
            }
        } catch(FileNotFoundException f) {
            LOG.error("CACalculator could not find file : " + refPrixFile.getName() + "-- Exit", f);
            System.exit(1);
        } catch (IOException e) {
            LOG.error("Error when reading file " + refPrixFile.getName() + "-- Exit", e);
            System.exit(1);
        }

        for(String unknownPrice : productToRemove) {
            this.productMap.remove(unknownPrice) ;
            LOG.info("Removing product " + unknownPrice + " from list because no price has been found in " + this.refPrixFile + " prod file");
        }
        long end = System.currentTimeMillis();
        LOG.debug("Compute CA for " + this.productMap.size() + " products took " + String.valueOf(end-start) + "ms");
    }

    //Realize the entire process. Write stage file and result files.
    public void process() {
        this.reBuildQteMap();
        this.buildCAMap();
        this.writeFullFile();
        String [] result = this.getTopN();
        this.writeSortedResultFile(result);
    }
}

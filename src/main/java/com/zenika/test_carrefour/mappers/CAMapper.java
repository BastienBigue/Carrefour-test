package com.zenika.test_carrefour.mappers;

import com.zenika.test_carrefour.config.CommonConfig;
import com.zenika.test_carrefour.data.MaxHeapProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class CAMapper {

    static Logger log = LogManager.getLogger(CAMapper.class);

    private Map<String, Float> productCAMap ;
    private File setProductFile ;
    private int topN ;
    private File refPrixFile ;
    private File outputFullFile ;
    private File outputTopNSortedFile ;

    public CAMapper(File refPrixFile, File setProductFile, int topN, File outputFullFile, File outputTopNSortedFile) {
        this.outputFullFile = outputFullFile;
        this.outputTopNSortedFile = outputTopNSortedFile;
        this.setProductFile = setProductFile;
        this.refPrixFile = refPrixFile;
        this.topN = topN ;
        this.productCAMap = new HashMap<>(131072) ;
        this.reBuildQteMap();
    }

    private void reBuildQteMap() {
        long start =  System.currentTimeMillis();
        String product;
        Float qte;
        String[] currentLine;

        try (BufferedReader br = new BufferedReader(new FileReader(setProductFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                currentLine = line.split("\\|");
                product = currentLine[0];
                qte = Float.valueOf(currentLine[1]);
                this.productCAMap.put(product, this.productCAMap.getOrDefault(product, 0f) + qte);
            }
        } catch (FileNotFoundException f) {
            log.error("CAMapper could not find file : " + setProductFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when reading file " + setProductFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }
        long end =  System.currentTimeMillis();
        log.debug("reBuildQteMap using stage1 file took " + String.valueOf(end-start) + "ms");
    }

    private void buildCAMap() {
        long start = System.currentTimeMillis();
        String product;
        Float unitPrice ;
        String[] currentLine;

        HashSet<String> productToRemove = new HashSet<>(this.productCAMap.keySet()) ;

        try (BufferedReader br = new BufferedReader(new FileReader(refPrixFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                currentLine = line.split("\\|");
                product = currentLine[0];
                unitPrice = Float.valueOf(currentLine[1]);
                if (this.productCAMap.containsKey(product)) {
                    Float price = this.productCAMap.get(product) * unitPrice ;
                    this.productCAMap.put(product, price);
                    productToRemove.remove(product) ;
                }
            }
        } catch(FileNotFoundException f) {
            log.error("CAMapper could not find file : " + refPrixFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when reading file " + refPrixFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }

        for(Iterator<String> it = productToRemove.iterator(); it.hasNext();) {
            String unknownPrice = it.next() ;
            this.productCAMap.remove(unknownPrice) ;
            log.info("Removing product " + unknownPrice + "from list because no price has been found in " + this.refPrixFile + " prod file");
        }
        long end = System.currentTimeMillis();
        log.debug("Compute CA for all products took " + String.valueOf(end-start) + "ms");
    }

    private void writeStage3File() {
        long start = System.currentTimeMillis();

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFullFile))) {
            String outputLine ;
            for (String k : this.productCAMap.keySet()) {
                outputLine = k.concat(CommonConfig.CSV_SEPARATOR).concat(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch(FileNotFoundException f) {
            log.error("CAMapper could not find file : " + outputFullFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when writing file " + outputFullFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        log.debug("Write file " + this.outputFullFile + "file took " + String.valueOf(end-start) + "ms");
    }

    private String[] getTopN() {
        long start = System.currentTimeMillis();
        MaxHeapProduct maxHeap = new MaxHeapProduct(this.productCAMap) ;
        String[] result = maxHeap.extractTopN(this.topN) ;
        long end = System.currentTimeMillis();
        log.debug("Get Top N elements based on CA took " + String.valueOf(end-start)+ "ms");
        return result ;
    }

    private void writeSortedResultFile(String[] result) {
        long start = System.currentTimeMillis();
        String outputLine ;

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(this.outputTopNSortedFile))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat(CommonConfig.CSV_SEPARATOR).concat(String.format(Locale.US, "%.2f", this.productCAMap.get(result[i])));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch(FileNotFoundException f) {
            log.error("CAMapper could not find file : " + outputTopNSortedFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when writing file " + outputTopNSortedFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        log.debug("Write file " + this.outputTopNSortedFile + "took " + String.valueOf(end-start)+ "ms");
    }

    public Map<String, Float> process() {
        this.buildCAMap();
        this.writeStage3File();
        String [] result = this.getTopN();
        this.writeSortedResultFile(result);
        return this.productCAMap ;
    }

    /*public static void main(String[] args) {
        File inputFile = new File(STAGE_2_SUBDIRECTORY, "set_produit-0b70efe8-7e44-4104-8b9d-ec5d2588812e_20190302.stage2") ;
        long start =  System.currentTimeMillis();
        //CAMapper st2 = new CAMapper(inputFile, 100) ;
        //st2.process();
        long end = System.currentTimeMillis();
        System.out.println("CAMapper total time = " + String.valueOf(end-start) + "ms");
    }*/
}

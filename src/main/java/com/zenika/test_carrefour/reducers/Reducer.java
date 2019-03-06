package com.zenika.test_carrefour.reducers;

import com.zenika.test_carrefour.data.MaxHeapProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public abstract class Reducer<T> {

    static Logger log = LogManager.getLogger(Reducer.class);

    protected Set<File> filesToAggregate ;
    protected Map<String, T> productMap ;
    private int topN ;
    private File outputFullFile ;
    private File outputTopNSortedFile ;

    public Reducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        this.topN = topN ;
        this.outputFullFile = outputFullFile ;
        this.outputTopNSortedFile = outputTopNSortedFile ;
        this.filesToAggregate = filesToAggregate;
        this.productMap = new HashMap<>(131072) ;
    }

    public abstract void parseAndInsertInMap(String[] value);

    public abstract String buildLine(String product, T value);

    private void buildMap() {
        long start = System.currentTimeMillis();
        String[] currentLine;

        for (File currFile : filesToAggregate) {
            try (BufferedReader br = new BufferedReader(new FileReader(currFile))) {
                for (String line; (line = br.readLine()) != null; ) {
                    currentLine = line.split("\\|");
                    parseAndInsertInMap(currentLine);
                }
            } catch(FileNotFoundException f) {
                log.error("Reducer could not find file : " + currFile.getName() + "-- Exit");
                f.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                log.error("Error when reading file " + currFile.getName() + "-- Exit");
                e.printStackTrace();
                System.exit(1);
            }
        }
        long end = System.currentTimeMillis();
        log.debug("buildMap took " + String.valueOf(end-start) + "ms");
    }

    protected void writeFullFile() {
        long start = System.currentTimeMillis();

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFullFile))) {
            String outputLine = null;
            for (String k : this.productMap.keySet()) {
                outputLine = this.buildLine(k, this.productMap.get(k));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch(FileNotFoundException f) {
            log.error("Reducer could not find file : " + outputFullFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when writing file " + outputFullFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        log.debug("Write file " + outputFullFile + " took " + String.valueOf(end-start) + "ms");
    }


    protected String[] getTopN() {
        long start = System.currentTimeMillis();
        MaxHeapProduct maxHeap = new MaxHeapProduct(productMap) ;
        String[] result = maxHeap.extractTopN(this.topN) ;
        long end = System.currentTimeMillis();
        log.debug("Get Top N elements based on CA took " + String.valueOf(end-start)+ "ms");
        return result ;
    }


    protected void writeSortedResultFile(String[] result) {
        long start = System.currentTimeMillis();
        String outputLine = null ;

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputTopNSortedFile))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = this.buildLine(result[i], this.productMap.get(result[i]));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch(FileNotFoundException f) {
            log.error("Reducer could not find file : " + outputTopNSortedFile.getName() + "-- Exit");
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            log.error("Error when writing file " + outputTopNSortedFile.getName() + "-- Exit");
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        log.debug("Write file " + outputTopNSortedFile + " took " + String.valueOf(end-start)+ "ms");

    }

    public Map<String,T> reduce() {
        this.buildMap();
        this.writeFullFile();
        String [] result = this.getTopN();
        this.writeSortedResultFile(result);
        return this.productMap ;
    }
}

package com.zenika.mappers;

import com.zenika.config.CommonConfig;
import com.zenika.data.MaxHeapProduct;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class CAMapper {

    private Map<String, Float> productCAMap ;
    private File setProductFile ;
    private int topN ;
    private File refPrixFile ;
    private File outputFullFile ;
    private File outputTopNSortedFile ;

    // On pourrait passer la map en arguement pour pas avoir à la recréer
    public CAMapper(File refPrixFile, File setProductFile, int topN, File outputFullFile, File outputTopNSortedFile) {
        this.outputFullFile = outputFullFile;
        this.outputTopNSortedFile = outputTopNSortedFile;
        this.setProductFile = setProductFile;
        this.refPrixFile = refPrixFile;
        this.topN = topN ;
        this.productCAMap = new HashMap<>() ;
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        long end =  System.currentTimeMillis();
        //DEBUG System.out.println("BuildMap from stage1 file took " + String.valueOf(end-start) + "ms");

    }

    private void buildCAMap() {
        long start = System.currentTimeMillis();
        String product;
        Float unitPrice ;
        String[] currentLine;

        HashSet<String> productToRemove = new HashSet<>(this.productCAMap.keySet()) ;

        try (BufferedReader br = new BufferedReader(new FileReader(refPrixFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                // Pas besoin de précompiler la regexp : ce n'est pas une regexp qui est utilisée car le pattern ne contient qu'un seul caractère.
                currentLine = line.split("\\|");
                product = currentLine[0];
                unitPrice = Float.valueOf(currentLine[1]);
                if (this.productCAMap.containsKey(product)) {
                    Float price = this.productCAMap.get(product) * unitPrice ;
                    this.productCAMap.put(product, price);
                    productToRemove.remove(product) ;
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for(Iterator<String> it = productToRemove.iterator(); it.hasNext();) {
            String unknownPrice = it.next() ;
            this.productCAMap.remove(unknownPrice) ;
        }
        long end = System.currentTimeMillis();
        //DEBUG System.out.println("Compute CA for all products took " + String.valueOf(end-start) + "ms");
    }

    private void writeStage3File() {
        long start = System.currentTimeMillis();
        File stage3Directory = new File(outputFullFile.getParent());
        if (!stage3Directory.exists()) {
            stage3Directory.mkdirs();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFullFile))) {
            String outputLine ;
            for (String k : this.productCAMap.keySet()) {
                outputLine = k.concat(CommonConfig.CSV_SEPARATOR).concat(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                bo.write(outputLine.getBytes());
                //StringBuilder sb = new StringBuilder().append(k).append(CSV_SEPARATOR).append(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                //bo.write(sb.toString().getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        //DEBUG System.out.println("Write stage3 file took " + String.valueOf(end-start) + "ms");
    }

    private String[] getTopN() {
        long start = System.currentTimeMillis();
        MaxHeapProduct maxHeap = new MaxHeapProduct(this.productCAMap) ;
        String[] result = maxHeap.extractTopN(this.topN) ;
        long end = System.currentTimeMillis();
        //DEBUG System.out.println("Get Top N elements based on CA took " + String.valueOf(end-start)+ "ms");
        return result ;
    }

    private void writeSortedResultFile(String[] result) {
        long start = System.currentTimeMillis();
        String outputLine ;

        File resultDirectory = new File(this.outputTopNSortedFile.getParent());
        if (!resultDirectory.exists()) {
            resultDirectory.mkdirs();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(this.outputTopNSortedFile))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat(CommonConfig.CSV_SEPARATOR).concat(String.format(Locale.US, "%.2f", this.productCAMap.get(result[i])));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        long end = System.currentTimeMillis();
        //DEBUG System.out.println("Write top_N_ca file took " + String.valueOf(end-start)+ "ms");
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

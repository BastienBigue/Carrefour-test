package main.java.com.zenika.utils;

import main.java.com.zenika.MaxHeapProduct;

import java.io.*;
import java.util.*;

public class Stage2Processor {

    private static String REFERENTIEL_PRODUIT = "reference_prod" ;
    private static String REFERENTIEL_PRODUIT_DIR = "data" ;
    private static String STAGE_3_SUBDIRECTORY = "stage3" ;
    private static String STAGE_2_SUBDIRECTORY = "stage2" ;
    private static String TOP_100_CA = "top_100_ca" ;
    private static String RESULT_SUBDIRECTORY = "result" ;
    private static String CSV_SEPARATOR = "|" ;


    private String magasin ;
    private String date ;
    private Map<String, Float> productCAMap ;
    private File setProductFile ;
    private int topN ;
    private File refPrixFile ;

    // On pourrait passer la map en arguement pour pas avoir à la recréer
    public Stage2Processor(String magasin, String date, int topN) {
        this.setProductFile = FileBuilder.createStage2File(magasin,date) ;
        this.productCAMap = new HashMap<>() ;
        this.date  = FilenameUtil.extractDate(this.setProductFile.getName()) ;
        this.magasin = FilenameUtil.extractMagasinId(this.setProductFile.getName()) ;
        this.topN = topN ;
        this.refPrixFile = FileBuilder.createReferenceProdFile(magasin,date) ;
        this.reBuildQteMap();
    }

    private void reBuildQteMap() {
        long start =  System.currentTimeMillis();
        String product;
        Float qte;
        String[] currentLine;

        try (BufferedReader br = new BufferedReader(new FileReader(setProductFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                // Pas besoin de précompiler la regexp : ce n'est pas une regexp qui est utilisée car le pattern ne contient qu'un seul caractère.
                currentLine = line.split("\\|");
                product = currentLine[0];
                qte = Float.valueOf(currentLine[1]);
                this.productCAMap.put(product, this.productCAMap.getOrDefault(product, new Float(0.0)) + qte);
            }
        } catch (IOException e) {
            String currentFunction = Thread.currentThread().getStackTrace()[1].getMethodName();
            System.out.println("IO EXCEPTION ; " + currentFunction );
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
            String currentFunction = Thread.currentThread().getStackTrace()[1].getMethodName();
            System.out.println("IO EXCEPTION ; " + currentFunction );
        }

        for(Iterator<String> it = productToRemove.iterator() ; it.hasNext();) {
            String unknownPrice = it.next() ;
            this.productCAMap.remove(unknownPrice) ;
        }
        long end = System.currentTimeMillis();
        //DEBUG System.out.println("Compute CA for all products took " + String.valueOf(end-start) + "ms");
    }

    private void writeStage3File() {
        long start = System.currentTimeMillis();
        File stage3Directory = new File(STAGE_3_SUBDIRECTORY);
        if (!stage3Directory.exists()) {
            stage3Directory.mkdir();
        }

        File outputFile = FileBuilder.createStage3File(this.magasin,this.date) ;
        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            String outputLine = null;
            for (String k : this.productCAMap.keySet()) {
                outputLine = k.concat("|").concat(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                bo.write(outputLine.getBytes());
                //StringBuilder sb = new StringBuilder().append(k).append(CSV_SEPARATOR).append(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                //bo.write(sb.toString().getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
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
        File outputFile = FileBuilder.createCAMagasinFile(magasin,date,topN) ;
        //String outputFile  = TOP_100_CA.concat("_").concat(this.magasin).concat("_").concat(this.date).concat(".data");
        String outputLine = null ;

        File resultDirectory = new File(RESULT_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat("|").concat(String.format(Locale.US, "%.2f", this.productCAMap.get(result[i])));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            String currentFunction = Thread.currentThread().getStackTrace()[1].getMethodName();
            System.out.println("IO EXCEPTION ; " + currentFunction );
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

    public static void main(String[] args) {
        File inputFile = new File(STAGE_2_SUBDIRECTORY, "set_produit-0b70efe8-7e44-4104-8b9d-ec5d2588812e_20190302.stage2") ;
        long start =  System.currentTimeMillis();
        //Stage2Processor st2 = new Stage2Processor(inputFile, 100) ;
        //st2.process();
        long end = System.currentTimeMillis();
        System.out.println("Stage2Processor total time = " + String.valueOf(end-start) + "ms");
    }
}

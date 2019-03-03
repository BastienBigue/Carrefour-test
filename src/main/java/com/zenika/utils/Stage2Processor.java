package main.java.com.zenika.utils;

import main.java.com.zenika.MaxHeapProduct;

import java.io.*;
import java.util.*;

public class Stage2Processor {

    private static String REFERENTIEL_PRODUIT = "reference_prod" ;
    private static String REFERENTIEL_PRODUIT_DIR = "bastien_data" ;
    private static String STAGE_3_SUBDIRECTORY = "stage3" ;
    private static String STAGE_2_SUBDIRECTORY = "stage2" ;
    private static String TOP_100_CA = "top_100_ca" ;
    private static String RESULT_SUBDIRECTORY = "result" ;


    private String magasin ;
    private String date ;
    private Map<String, Float> productCAMap ;
    private File setProductFile ;
    private int topN ;
    private File refPrixFile ;

    // On pourrait passer la map en arguement pour pas avoir à la recréer
    public Stage2Processor(File file, int topN) {
        this.setProductFile = file ;
        this.productCAMap = new HashMap<>() ;
        this.date  = FilenameUtil.extractDate(this.setProductFile.getName()) ;
        this.magasin = FilenameUtil.extractMagasinId(this.setProductFile.getName()) ;
        this.topN = topN ;
        this.refPrixFile = new File(REFERENTIEL_PRODUIT_DIR, REFERENTIEL_PRODUIT.concat("-").concat(magasin).concat("_").concat(date).concat(".data")) ;
    }

    private void reBuildQteMap() {
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
    }

    private void buildCAMap() {
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
    }

    private void writeStage3File() {

        File stage3Directory = new File(STAGE_3_SUBDIRECTORY);
        if (!stage3Directory.exists()) {
            stage3Directory.mkdir();
        }

        File outputFile = new File(STAGE_3_SUBDIRECTORY, "set_ca-".concat(magasin).concat("_").concat(this.date).concat(".stage3")) ;
        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            String outputLine = null;
            for (String k : this.productCAMap.keySet()) {
                outputLine = k.concat("|").concat(String.format (Locale.US, "%.2f", this.productCAMap.get(k)));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }
    }

    private String[] getTopN() {
        MaxHeapProduct maxHeap = new MaxHeapProduct(this.productCAMap) ;
        return maxHeap.extractTopN(this.topN) ;
    }

    private void writeSortedResultFile(String[] result) {
        String outputFile  = TOP_100_CA.concat("_").concat(this.magasin).concat("_").concat(this.date).concat(".data");
        String outputLine = null ;

        File resultDirectory = new File(RESULT_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File (RESULT_SUBDIRECTORY, outputFile)))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat("|").concat(String.format("%.2f", this.productCAMap.get(result[i])));
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            String currentFunction = Thread.currentThread().getStackTrace()[1].getMethodName();
            System.out.println("IO EXCEPTION ; " + currentFunction );
        }
    }

    public static void process(File file, int topN) {
        Stage2Processor st2 = new Stage2Processor(file, topN) ;
        //A ajouter une condition si on la récupère du stage précédent
        st2.reBuildQteMap();
        st2.buildCAMap();
        st2.writeStage3File();
        String [] result = st2.getTopN();
        st2.writeSortedResultFile(result);
    }

    public static void main(String[] args) {
        File inputFile = new File(STAGE_2_SUBDIRECTORY, "set_produit-0b0abf8c-5efc-464c-8cb4-bce873078508_20190302.stage2") ;
        Stage2Processor.process(inputFile,100);
    }
}

package main.java.com.zenika.utils;

import main.java.com.zenika.MaxHeapProduct;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.SplittableRandom;

/**
    Cette classe prend un fichier issu du stage 1
    Il maintient le classement du TOP N des meilleurs produits dans un TreeSet
    Il produit 2 fichiers de sortie :
        - top N des meilleurs produits
        - liste de la quantité totale de chaque produit, avec une seule occurence du produit par fichier
 */
public class Stage1Reducer {

    private static String STAGE_1_SUBDIRECTORY = "stage1" ;
    private static String RESULT_SUBDIRECTORY = "result" ;
    private static String STAGE_2_SUBDIRECTORY = "stage2" ;
    private static String TOP_100_VENTES = "top_100_ventes" ;
    private static String LISTING_PRODUCT = "listing_product" ;

    private File listingProductFile ;
    private String magasin ;
    private String date ;
    private Map<String, Integer> productQteMap ;
    private int topN ;

    public Stage1Reducer(File listingProductFile, int topN) {
        this.listingProductFile = listingProductFile ;
        this.productQteMap = new HashMap<>() ;
        this.date  = FilenameUtil.extractDate(this.listingProductFile.getName()) ;
        this.magasin = FilenameUtil.extractMagasinId(this.listingProductFile.getName()) ;
        this.topN = topN ;
    }


    private void buildMap() throws IOException {

        String product;
        Integer qte;
        String[] currentLine;

        try (BufferedReader br = new BufferedReader(new FileReader(listingProductFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                // Pas besoin de précompiler la regexp : ce n'est pas une regexp qui est utilisée car le pattern ne contient qu'un seul caractère.
                currentLine = line.split("\\|");
                product = currentLine[0];
                qte = Integer.valueOf(currentLine[1]);
                this.productQteMap.put(product, this.productQteMap.getOrDefault(product, 0) + qte);
            }
        }
    }

    private void writeStage2File() {

        File stage2Directory = new File(STAGE_2_SUBDIRECTORY);
        if (!stage2Directory.exists()) {
            stage2Directory.mkdir();
        }

        File outputFile = new File(STAGE_2_SUBDIRECTORY, "set_produit-".concat(magasin).concat("_").concat(this.date).concat(".stage2")) ;
        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            String outputLine = null;
            for (String k : this.productQteMap.keySet()) {
                outputLine = k.concat("|").concat(this.productQteMap.get(k).toString());
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }
    }

    private String[] getTopN() {
        MaxHeapProduct maxHeap = new MaxHeapProduct(productQteMap) ;
        return maxHeap.extractTopN(this.topN) ;
    }



    private void writeSortedResultFile(String[] result) {
        String outputFile  = TOP_100_VENTES.concat("_").concat(this.magasin).concat("_").concat(this.date).concat(".data");
        String outputLine = null ;

        File resultDirectory = new File(RESULT_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File (RESULT_SUBDIRECTORY, outputFile)))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat("|").concat(this.productQteMap.get(result[i]).toString());
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }

    }

    public Map<String,Integer> reduce() {
        //Stage1Reducer st1 = new Stage1Reducer(new File("stage1", "listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20170514.stage1")) ;
        try {
            this.buildMap();
            this.writeStage2File();
            String [] result = this.getTopN();
            this.writeSortedResultFile(result);

        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return this.productQteMap ;
    }


    public static void main(String[] args) {
        File inputFile = new File(STAGE_1_SUBDIRECTORY, "listing_produit-0b70efe8-7e44-4104-8b9d-ec5d2588812e_20190302.stage1") ;
        long start = System.currentTimeMillis() ;
        Stage1Reducer st1 = new Stage1Reducer(inputFile, 100) ;
        st1.reduce();
        long  end = System.currentTimeMillis() ;
        System.out.println("Stage1Reducer execution time = " + String.valueOf(end-start) + "ms");
    }
}

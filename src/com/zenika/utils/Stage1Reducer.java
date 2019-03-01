package com.zenika.utils;

import javafx.stage.Stage;

import java.io.*;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

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

    private File listingProductFile ;

    private String magasin ;
    private String date ;
    private Map<String, Integer> productQteMap ;

    public Stage1Reducer(File file) {
        this.listingProductFile = file ;
        this.productQteMap = new HashMap<>() ;
        this.date  = FilenameUtil.extractDate(this.listingProductFile.getName()) ;
        this.magasin = FilenameUtil.extractMagasinId(this.listingProductFile.getName()) ;
    }


    public void buildMap() throws IOException {

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

    public void mapToFile() {

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


    public static void main(String[] args) {
        Stage1Reducer st1 = new Stage1Reducer(new File("stage1", "listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20170514.stage1")) ;

        try {
            st1.buildMap();
            st1.mapToFile();
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

    }
}

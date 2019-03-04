package main.java.com.zenika.utils;

import main.java.com.zenika.MaxHeapProduct;
import sun.applet.resources.MsgAppletViewer;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class QteReducer {

    private Set<File> filesToAggregate ;
    private Map<String, Integer> productMap ;
    private int topN ;
    private File outputFullFile ;
    private File outputTopNSortedFile ;

    public QteReducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        this.topN = topN ;
        this.outputFullFile = outputFullFile ;
        this.outputTopNSortedFile = outputTopNSortedFile ;
        this.filesToAggregate = filesToAggregate;
        this.productMap = new HashMap<>() ;
    }

    private void buildMap() throws IOException {

        String product;
        Integer qte;
        String[] currentLine;
        for (Iterator<File> itFile = filesToAggregate.iterator(); itFile.hasNext();) {
            File currFile = itFile.next();
            try (BufferedReader br = new BufferedReader(new FileReader(currFile))) {
                for (String line; (line = br.readLine()) != null; ) {
                    // Pas besoin de précompiler la regexp : ce n'est pas une regexp qui est utilisée car le pattern ne contient qu'un seul caractère.
                    currentLine = line.split("\\|");
                    product = currentLine[0];
                    qte = Integer.valueOf(currentLine[1]);
                    this.productMap.put(product, this.productMap.getOrDefault(product, 0) + qte);
                }
            }
        }
    }

    private void writeFullFile() {

        File stage2Directory = new File(outputFullFile.getParent());
        if (!stage2Directory.exists()) {
            stage2Directory.mkdir();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFullFile))) {
            String outputLine = null;
            for (String k : this.productMap.keySet()) {
                outputLine = k.concat("|").concat(this.productMap.get(k).toString());
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }
    }

    private String[] getTopN() {
        MaxHeapProduct maxHeap = new MaxHeapProduct(productMap) ;
        return maxHeap.extractTopN(this.topN) ;
    }



    private void writeSortedResultFile(String[] result) {

        String outputLine = null ;

        File resultDirectory = new File(outputTopNSortedFile.getParent());
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputTopNSortedFile))) {
            for (int i = 0 ; i < result.length ; i++) {
                outputLine = result[i].concat("|").concat(this.productMap.get(result[i]).toString());
                bo.write(outputLine.getBytes());
                bo.write(System.lineSeparator().getBytes());
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
        }

    }

    public Map<String,Integer> reduce() {
        try {
            this.buildMap();
            this.writeFullFile();
            String [] result = this.getTopN();
            this.writeSortedResultFile(result);

        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return this.productMap ;
    }


    public static void main(String[] args) {

        Set<File> filesToCompute = new HashSet<>() ;
        filesToCompute.add(new File("stage2", "set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20170514.stage2")) ;
        filesToCompute.add(new File("stage2","set_produit-6af0502b-ce7a-4a6f-b5d3-516d09514095_20170514.stage2")) ;
        filesToCompute.add(new File("stage2","set_produit-8e588f2f-d19e-436c-952f-1cdd9f0b12b0_20170514.stage2")) ;
        filesToCompute.add(new File("stage2","set_produit-10f2f3e6-f728-41f3-b079-43b0aa758292_20170514.stage2")) ;
        filesToCompute.add(new File("stage2","set_produit-72a2876c-bc8b-4f35-8882-8d661fac2606_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-29366c83-eae9-42d3-a8af-f15339830dc5_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-af068240-8198-4b79-9cf9-c28c0db65f63_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-bdc2a431-797d-4b07-9567-67c565a67b84_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-bf0999da-ae45-49df-983e-89020198330b_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-d4bfbabf-0160-4e87-8688-78e0943a396a_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-dd43720c-be43-41b6-bc4a-ac4beabd0d9b_20170514.stage2"));
        filesToCompute.add(new File("stage2","set_produit-e3d54d00-18be-45e1-b648-41147638bafe_20170514.stage2"));

        File outputFullFile = new File(FilenameUtil.STAGE4_1, FilenameUtil.buildFileName(null, "20170514", FilenameUtil.FileType.STAGE4_1)) ;
        File outputTopNSortedFile = new File(FilenameUtil.RESULT, FilenameUtil.buildFileName(null, "20170514", FilenameUtil.FileType.RESULT_VENTES_GLOBAL, 100)) ;
        QteReducer qteReducer = new QteReducer(filesToCompute,100, outputFullFile, outputTopNSortedFile) ;

        qteReducer.reduce();

        /*File inputFile = new File(STAGE_1_SUBDIRECTORY, "listing_produit-0b70efe8-7e44-4104-8b9d-ec5d2588812e_20190302.stage1") ;
        long start = System.currentTimeMillis() ;
        QteReducer qte = new Stage1Reducer(inputFile, 100) ;
        st1.reduce();
        long  end = System.currentTimeMillis() ;
        System.out.println("Stage1Reducer execution time = " + String.valueOf(end-start) + "ms");*/
    }
}

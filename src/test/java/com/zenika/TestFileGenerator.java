package test.java.com.zenika;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class TestFileGenerator {

    private final String TRANSACTIONS = "transactions" ;
    private final String DATA_SUBDIRECTORY = "data" ;
    private final String REFERENCE_PROD = "reference_prod" ;
    private final String CSV_SEPARATOR = "|";
    private final int SECONDS_IN_DAY = 24*60*60;
    private final String DATA_BASTIEN_SUBDIRECTORY = "bastien_data" ;

    private int nbTransactions ;
    private int nbMagasin ;
    private int nbProduits ;
    private String dateString ;
    private Date date ;
    private Random random;

    private ArrayList<String> idMagasins ;

    public TestFileGenerator(int nbTransactions, int nbMagasin, int nbProduits, String dateString) {
        this.nbMagasin = nbMagasin;
        this.nbProduits = nbProduits ;
        this.nbTransactions = nbTransactions ;
        this.dateString = dateString ;
        this.random = new Random() ;
        this.idMagasins = generateIdMagasin();
        try {
            this.date = this.parseGivenDate();
        } catch (ParseException e) {
            System.out.println("Parse exception");
        }
    }

    private ArrayList<String> generateIdMagasin() {
        ArrayList<String> idMagasins = new ArrayList<>() ;
        for (int i = 0 ; i < this.nbMagasin ; i++) {
            idMagasins.add(UUID.randomUUID().toString());
        }
        return idMagasins ;
    }

    private Date parseGivenDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return dateFormat.parse(this.dateString) ;
    }


    private String generateRandomDate() {

        Instant randomInstant = this.date.toInstant().plusSeconds(random.nextInt(SECONDS_IN_DAY)) ;
        DateFormat randomDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.FRANCE);
        randomDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        return randomDateFormat.format(Date.from(randomInstant));
    }

    private void generateReferenceProduitFileForOneStore(String idMagasin) {

        File resultDirectory = new File(DATA_BASTIEN_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        String outputFileName  = REFERENCE_PROD.concat("-").concat(idMagasin).concat("_").concat(this.dateString).concat(".data");
        File outputFile = new File(DATA_BASTIEN_SUBDIRECTORY, outputFileName) ;

        if (outputFile.exists()) {
            System.out.println("Le fichier de référence produit existe déjà pour cette date et ce magasin");
        } else {
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                float randomPrice;
                for(int i = 0 ; i < this.nbProduits ; i++) {
                    randomPrice = 5000 * random.nextFloat();
                    StringBuilder sb = new StringBuilder().append(i).append(CSV_SEPARATOR).append(String.format (Locale.US, "%.2f",randomPrice));
                    bo.write(sb.toString().getBytes());
                    bo.write(System.lineSeparator().getBytes());
                }
            } catch (IOException e) {
                System.out.println("IO EXCEPTION");
            }
        }
    }

    public void  generateTransactionFile() {
        String outputFileName  = TRANSACTIONS.concat("_").concat(this.dateString).concat(".data");
        File outputFile = new File(DATA_BASTIEN_SUBDIRECTORY, outputFileName) ;
        int transactionId;
        String magasinId;
        int idProduit;
        int qte;
        String randomDate ;

        File resultDirectory = new File(DATA_BASTIEN_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdir();
        }

        if (outputFile.exists()) {
            System.out.println("Le fichier transaction existe déjà pour cette date");
        } else {
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                int cntTransaction = 0 ;
                int currTransactionId = 1 ;

                while(cntTransaction < this.nbTransactions) {

                    magasinId = this.idMagasins.get(random.nextInt(this.nbMagasin));
                    idProduit = this.random.nextInt(this.nbProduits);
                    qte = this.random.nextInt(10);
                    randomDate = this.generateRandomDate();
                    StringBuilder sb = new StringBuilder().append(String.valueOf(currTransactionId)).append(CSV_SEPARATOR).append(randomDate).append(CSV_SEPARATOR).append(magasinId).append(CSV_SEPARATOR).append(idProduit).append(CSV_SEPARATOR).append(qte);
                    bo.write(sb.toString().getBytes());
                    bo.write(System.lineSeparator().getBytes());

                    cntTransaction++;
                    //increment transactionID with proba=1/10
                    if (random.nextInt(10) == 0) {
                        currTransactionId++;
                    }
                }
            } catch (IOException e) {
                System.out.println("IO EXCEPTION");
            }
        }
    }

    public static void main(String[] args) {
        int nbProduits = 1000000 ;
        int nbMagasins = 120 ;
        TestFileGenerator gene = new TestFileGenerator(20000000,120,nbProduits,"20190302") ;
        gene.generateTransactionFile();
        System.out.println("TransactionFile generation done!");
        for (int i = 0 ; i<nbMagasins ; i++) {
            gene.generateReferenceProduitFileForOneStore(gene.idMagasins.get(i));
            if ((i % 100) == 0) {
                System.out.println("ReferenceProduitFile generation done for " + i + "stores!");
            }
        }
    }
}

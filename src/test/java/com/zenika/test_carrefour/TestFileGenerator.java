package com.zenika.test_carrefour;

import com.zenika.test_carrefour.config.CommonConfig;
import com.zenika.test_carrefour.utils.FileBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    static Logger log = LogManager.getLogger(TestFileGenerator.class);

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
        this.idMagasins = TestFileGenerator.generateIdMagasin(nbMagasin);
        try {
            this.date = this.parseGivenDate();
        } catch (ParseException e) {
            log.error("Error when parsing given date", e);
            System.exit(1);
        }
    }

    public TestFileGenerator(int nbTransactions, int nbMagasin, int nbProduits, String dateString, ArrayList<String> magasins) {
        this.nbMagasin = nbMagasin;
        this.nbProduits = nbProduits ;
        this.nbTransactions = nbTransactions ;
        this.dateString = dateString ;
        this.random = new Random() ;
        this.idMagasins = magasins;
        try {
            this.date = this.parseGivenDate();
        } catch (ParseException e) {
            log.error("Error when parsing given date", e);
            System.exit(1);
        }
    }

    private static ArrayList<String> generateIdMagasin(int nbMagasin) {
        ArrayList<String> idMagasins = new ArrayList<>() ;
        for (int i = 0 ; i < nbMagasin ; i++) {
            idMagasins.add(UUID.randomUUID().toString());
        }
        return idMagasins ;
    }

    private Date parseGivenDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(CommonConfig.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(CommonConfig.TIMEZONE));
        return dateFormat.parse(this.dateString) ;
    }


    private String generateRandomDate() {

        Instant randomInstant = this.date.toInstant().plusSeconds(random.nextInt(CommonConfig.SECONDS_PER_DAY)) ;
        DateFormat randomDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.FRANCE);
        randomDateFormat.setTimeZone(TimeZone.getTimeZone(CommonConfig.TIMEZONE));

        return randomDateFormat.format(Date.from(randomInstant));
    }

    private void generateReferenceProduitFileForOneStore(String idMagasin) {

        File outputFile = FileBuilder.createReferenceProdFile(idMagasin,this.dateString);

        if (outputFile.exists()) {
            log.warn("Reference-prod file already exists for this store and date. Not recreated.");
        } else {
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                float randomPrice;
                for(int i = 0 ; i < this.nbProduits ; i++) {
                    randomPrice = 5000 * random.nextFloat();
                    StringBuilder sb = new StringBuilder().append(i).append(CommonConfig.CSV_SEPARATOR).append(String.format (Locale.US, "%.2f",randomPrice));
                    bo.write(sb.toString().getBytes());
                    bo.write(System.lineSeparator().getBytes());
                }
            } catch (IOException e) {
                log.error("Error when writing to file " + outputFile.getName(), e);
                System.exit(1);
            }
        }
    }

    public void  generateTransactionFile() {

        log.info("Start generateTransactionFile for date " + this.dateString);

        File outputFile = FileBuilder.createTransactionFile(this.dateString);
        int transactionId;
        String magasinId;
        int idProduit;
        int qte;
        String randomDate ;

        if (outputFile.exists()) {
            log.warn("Transaction file already exists for this date. Not recreated. ");
        } else {
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                int cntTransaction = 0 ;
                int currTransactionId = 1 ;

                while(cntTransaction < this.nbTransactions) {

                    magasinId = this.idMagasins.get(random.nextInt(this.nbMagasin));
                    idProduit = this.random.nextInt(this.nbProduits);
                    qte = this.random.nextInt(10);
                    randomDate = this.generateRandomDate();
                    StringBuilder sb = new StringBuilder().append(String.valueOf(currTransactionId)).append(CommonConfig.CSV_SEPARATOR).append(randomDate).append(CommonConfig.CSV_SEPARATOR).append(magasinId).append(CommonConfig.CSV_SEPARATOR).append(idProduit).append(CommonConfig.CSV_SEPARATOR).append(qte);
                    bo.write(sb.toString().getBytes());
                    bo.write(System.lineSeparator().getBytes());

                    cntTransaction++;
                    //increment transactionID with proba=1/10
                    if (random.nextInt(10) == 0) {
                        currTransactionId++;
                    }
                }
            } catch (IOException e) {
                log.error("Error when writing to file " + outputFile.getName(),e);
                System.exit(1);
            }
        }
        log.info("Transaction file generator done for date " + dateString);
    }

    public static void generateFilesForXDays(String[] dates, int nbProduits, int nbMagasins, int nbTransactions) {

        ArrayList<String> idMagasins = TestFileGenerator.generateIdMagasin(nbMagasins) ;
        for (int i = 0 ; i < dates.length ; i++) {
            TestFileGenerator gene = new TestFileGenerator(nbTransactions,nbMagasins,nbProduits,dates[i], idMagasins) ;
            gene.generateTransactionFile();
            for (int j = 1 ; j<=nbMagasins ; j++) {
                gene.generateReferenceProduitFileForOneStore(gene.idMagasins.get(j-1));
                if ((j % 100) == 0) {
                    log.info("ReferenceProduitFile generation done for " + j + " stores for  date " + gene.dateString);
                }
            }
        }
    }

    public static void main(String[] args) {
        int nbProduits = 500 ;
        int nbMagasins = 500 ;
        int nbTransactions = 1000000 ;
        String[] dates = {"20190611"};
        //String[] dates = {"20190305", "20190306","20190307","20190308","20190309","20190310","20190311"};
        TestFileGenerator.generateFilesForXDays(dates,nbProduits,nbMagasins,nbTransactions);



    }
}

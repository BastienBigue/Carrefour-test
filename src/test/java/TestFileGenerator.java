import com.zenika.test_carrefour.config.CommonConfig;
import com.zenika.test_carrefour.utils.FileBuilder;

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
            e.printStackTrace();
            System.exit(1);
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

        File resultDirectory = new File(CommonConfig.DATA_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdirs();
        }

        File outputFile = FileBuilder.createReferenceProdFile(idMagasin,this.dateString);

        if (outputFile.exists()) {
            System.out.println("Le fichier de référence produit existe déjà pour cette date et ce magasin");
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
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void  generateTransactionFile() {

        File outputFile = FileBuilder.createTransactionFile(this.dateString);
        int transactionId;
        String magasinId;
        int idProduit;
        int qte;
        String randomDate ;

        File resultDirectory = new File(CommonConfig.DATA_SUBDIRECTORY);
        if (!resultDirectory.exists()) {
            resultDirectory.mkdirs();
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
                e.printStackTrace();
                System.exit(1);
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

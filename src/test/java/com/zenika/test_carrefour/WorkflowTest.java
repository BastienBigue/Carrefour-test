package com.zenika.test_carrefour;

import com.zenika.test_carrefour.utils.FileBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WorkflowTest {

    private static final String DATE_TEST_GIVEN_DATA = "20170514";
    private static final String[] DATE_TEST = {"19960610"} ;

    private static final String TEST_FILE = "transactions_20170514.data" ;
    private static final int TOP_N = 100 ;


    private static final String MAGASINID = "2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71" ;

    private static final String[] folders = {"stage1", "stage2", "stage3", "stage4-1", "stage4-2", "stage4-3", "stage4-4", "stage5-1", "stage5-2", "result", "data"};

    /*@BeforeClass
    public static void  createTestsFiles() {
        TestFileGenerator.generateFilesForXDays(DATE_TEST_GIVEN_DATA, NB_PRODUITS, NB_MAGASINS, NB_TRANSACTIONS);
    }

    @AfterClass
    public static void deleteTestFiles() {
        for (String currFolder : folders) {
            File currFolderFile = new File(currFolder);

            File[] filesToDelete = currFolderFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return FilenameUtil.extractDate(name).equals(DATE_TEST);
                }
            });

            for ( final File file : filesToDelete ) {
                if ( !file.delete() ) {
                    System.err.println( "Can't remove " + file.getAbsolutePath() );
                }
            }
        }
    }*/

    private class KeyValuePosition<E> {
        private String key ;
        private E value ;
        private int position ;

        public KeyValuePosition(String key, E value, int position) {
            this.key = key ;
            this.position = position;
            this.value = value;
        }

        public String getKey() {
            return this.key ;
        }

        public E getValue() {
            return this.value;
        }

        public int getPosition() {
            return this.position;
        }

    }


    public List<KeyValuePosition<Float>> read3FirstLinesOfCAFile(File file) {
        String product;
        Float qte ;
        String[] currentLine;
        String line;
        ArrayList<KeyValuePosition<Float>> firstThreeElements = new ArrayList<>() ;
        int position =  1 ;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null && position < 4) {
                currentLine = line.split("\\|");
                product = currentLine[0];
                qte = Float.valueOf(currentLine[1]);
                firstThreeElements.add(new KeyValuePosition<>(product, qte, position)) ;
                position++;
            }
        } catch(FileNotFoundException f) {
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return firstThreeElements;
    }


    public List<KeyValuePosition<Integer>> read3FirstLinesOfQteFile(File file) {

        String product;
        Integer qte ;
        String[] currentLine;
        String line;
        ArrayList<KeyValuePosition<Integer>> firstThreeElements = new ArrayList<>() ;

        int position =  1 ;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null && position < 4) {
                currentLine = line.split("\\|");
                product = currentLine[0];
                qte = Integer.valueOf(currentLine[1]);
                firstThreeElements.add(new KeyValuePosition<>(product, qte, position)) ;
                position++;
            }
        } catch(FileNotFoundException f) {
            f.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return firstThreeElements;
    }

    @Test
    public void  testWorkflow7J() {

    }

    @Test
    public void testWorkflowOneDay() {
        File transactionFile = new File("data", TEST_FILE);
        Workflow workflow = new Workflow();
        workflow.processWorkflow(transactionFile, TOP_N, Workflow.REQUESTABLE_STAGES.ALL);

        /*
        top_100_ventes_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20170514.data
         */

        List<KeyValuePosition<Integer>> top3VenteMagasin = read3FirstLinesOfQteFile(FileBuilder.createVenteMagasinFile(MAGASINID, DATE_TEST_GIVEN_DATA, TOP_N));
        Assert.assertEquals("789", top3VenteMagasin.get(0).getKey());
        Assert.assertTrue(top3VenteMagasin.get(0).getValue() == 62);
        Assert.assertEquals(1, top3VenteMagasin.get(0).getPosition());

        Assert.assertEquals("671", top3VenteMagasin.get(1).getKey());
        Assert.assertTrue(top3VenteMagasin.get(1).getValue() == 59);
        Assert.assertEquals(2, top3VenteMagasin.get(1).getPosition());

        Assert.assertEquals("635", top3VenteMagasin.get(2).getKey());
        Assert.assertTrue(top3VenteMagasin.get(2).getValue() == 55);
        Assert.assertEquals(3, top3VenteMagasin.get(2).getPosition());

        /*
        top_100_ca_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20170514.data
         */

        List<KeyValuePosition<Float>> top3CAMagasin = read3FirstLinesOfCAFile(FileBuilder.createCAMagasinFile(MAGASINID, DATE_TEST_GIVEN_DATA, TOP_N));
        Assert.assertEquals(top3CAMagasin.get(0).getKey(), "330");
        Assert.assertEquals(top3CAMagasin.get(0).getValue(),new Float(4231.08));
        Assert.assertEquals(top3CAMagasin.get(0).getPosition(), 1);

        Assert.assertEquals(top3CAMagasin.get(1).getKey(), "727");
        Assert.assertEquals(top3CAMagasin.get(1).getValue(),new Float(4114.85));
        Assert.assertEquals(top3CAMagasin.get(1).getPosition(), 2);

        Assert.assertEquals(top3CAMagasin.get(2).getKey(), "672");
        Assert.assertEquals(top3CAMagasin.get(2).getValue(),new Float(4105.50));
        Assert.assertEquals(top3CAMagasin.get(2).getPosition(), 3);


        /*
        top_100_ventes_GLOBAL_20170514.data
         */
        List<KeyValuePosition<Float>> top3VenteGlobal = read3FirstLinesOfCAFile(FileBuilder.createVenteGlobalFile(DATE_TEST_GIVEN_DATA, TOP_N));
        Assert.assertEquals(top3VenteGlobal.get(0).getKey(), "568");
        Assert.assertTrue(top3VenteGlobal.get(0).getValue() == 363);
        Assert.assertEquals(top3VenteGlobal.get(0).getPosition(), 1);

        Assert.assertEquals(top3VenteGlobal.get(1).getKey(), "952");
        Assert.assertTrue(top3VenteGlobal.get(1).getValue() == 351);
        Assert.assertEquals(top3VenteGlobal.get(1).getPosition(), 2);

        Assert.assertEquals(top3VenteGlobal.get(2).getKey(), "96");
        Assert.assertTrue(top3VenteGlobal.get(2).getValue() == 336);
        Assert.assertEquals(top3VenteGlobal.get(2).getPosition(), 3);

        /*
        top_100_ca_GLOBAL_20170514.data
         */
        List<KeyValuePosition<Float>> top3CAGlobal = read3FirstLinesOfCAFile(FileBuilder.createCAGlobalFile(DATE_TEST_GIVEN_DATA, TOP_N));
        Assert.assertEquals(top3CAGlobal.get(0).getKey(), "110");
        Assert.assertEquals(top3CAGlobal.get(0).getValue(),new Float(21157.00));
        Assert.assertEquals(top3CAGlobal.get(0).getPosition(), 1);

        Assert.assertEquals(top3CAGlobal.get(1).getKey(), "511");
        Assert.assertEquals(top3CAGlobal.get(1).getValue(),new Float(19839.78));
        Assert.assertEquals(top3CAGlobal.get(1).getPosition(), 2);

        Assert.assertEquals(top3CAGlobal.get(2).getKey(), "263");
        Assert.assertEquals(top3CAGlobal.get(2).getValue(),new Float(18358.46));
        Assert.assertEquals(top3CAGlobal.get(2).getPosition(), 3);

    }


}

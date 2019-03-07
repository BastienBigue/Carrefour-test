package com.zenika.test_carrefour.utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileBuilderTest {


    private static final String DATE_TEST = "19960610";
    private static final String MAGASINID = "2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71" ;
    private static final int TOP_N = 42 ;

    private static final String[] folders = {"stage1", "stage2", "stage3", "stage4-1", "stage4-2", "stage4-3", "stage4-4", "stage5-1", "stage5-2", "result", "data"};

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
    }

    @Test
    public void shouldBuildStage1File() throws IOException {
        File file = FileBuilder.createStage1File(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage2File() throws IOException {
        File file = FileBuilder.createStage2File(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage3File() throws IOException {
        File file = FileBuilder.createStage3File(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage41File() throws IOException {
        File file = FileBuilder.createStage4_1File(DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage42File() throws IOException {
        File file = FileBuilder.createStage4_2File(DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage43File() throws IOException {
        File file = FileBuilder.createStage4_3File(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage44File() throws IOException {
        File file = FileBuilder.createStage4_4File(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage51File() throws IOException {
        File file = FileBuilder.createStage5_2File(DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildStage52File() throws IOException {
        File file = FileBuilder.createStage5_1File(DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildReferenceProduitFile() throws IOException {
        File file = FileBuilder.createReferenceProdFile(MAGASINID, DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildTransactionFile() throws IOException {
        File file = FileBuilder.createTransactionFile(DATE_TEST);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildVenteMagasinFile() throws IOException {
        File file = FileBuilder.createVenteMagasinFile(MAGASINID, DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildCAMagasinFile() throws IOException {
        File file = FileBuilder.createCAMagasinFile(MAGASINID, DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildVenteGlobalFile() throws IOException {
        File file = FileBuilder.createVenteGlobalFile(DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildCAGlobalFile() throws IOException {
        File file = FileBuilder.createCAGlobalFile(DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildVenteMagasinJ7File() throws IOException {
        File file = FileBuilder.createVenteMagasin7JFile(MAGASINID, DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildCAMagasinJ7File() throws IOException {
        File file = FileBuilder.createCAMagasin7JFile(MAGASINID, DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildVenteGlobalJ7() throws IOException {
        File file = FileBuilder.createVenteGlobal7JFile(DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

    @Test
    public void shouldBuildCAGlobalJ7() throws IOException {
        File file = FileBuilder.createCAGlobal7JFile(DATE_TEST, TOP_N);
        file.createNewFile();
        Assert.assertTrue(file.exists());
    }

}
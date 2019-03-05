package com.zenika.test_carrefour.utils;

import org.junit.Assert;
import org.junit.Test;

public class FilenameUtilTest {

    private static final String DATE = "20190304" ;
    private static final String MAGASINID = "2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71" ;

    @Test
    public void shouldCreateTransactionFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.TRANSACTION_FILE), "transactions_20190304.data");
    }

    @Test
    public void shouldCreateReferenceProduitFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.REF_PROD), "reference_prod-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data");
    }

    @Test
    public void shouldCreateStage1FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE1), "listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage1");
    }

    @Test
    public void shouldCreateStage2FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE2), "set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage2");
    }

    @Test
    public void shouldCreateStage3FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE3), "set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage3");
    }

    @Test
    public void shouldCreateStage4_1FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE4_1), "set_produit-GLOBAL_20190304.stage4-1");
    }

    @Test
    public void shouldCreateStage4_2FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE4_2), "set_ca-GLOBAL_20190304.stage4-2");
    }

    @Test
    public void shouldCreateStage4_3FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE4_3), "set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-3");
    }

    @Test
    public void shouldCreateStage4_4FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE4_4), "set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-4");
    }

    @Test
    public void shouldCreateStage5_1FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE5_1), "set_produit-GLOBAL_20190304-J7.stage5-1");
    }

    @Test
    public void shouldCreateStage5_2FileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.STAGE5_2), "set_ca-GLOBAL_20190304-J7.stage5-2");
    }

    @Test
    public void shouldCreateTop200VentesMagasinFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_VENTES_MAGASIN, 200), "top_200_ventes_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data");
    }

    @Test
    public void shouldCreateTop200CAMagasinFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_CA_MAGASIN, 200), "top_200_ca_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data");
    }

    @Test
    public void shouldCreateTop200VentesGlobalFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_VENTES_GLOBAL, 200), "top_200_ventes_GLOBAL_20190304.data");
    }

    @Test
    public void shouldCreateTop200CAGlobalFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_CA_GLOBAL, 200), "top_200_ca_GLOBAL_20190304.data");
    }

    @Test
    public void shouldCreateTop200VentesMagasin7JFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_VENTES_MAGASIN_7J, 200), "top_200_ventes_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.data");
    }

    @Test
    public void shouldCreateTop200CAMagasin7JFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_CA_MAGASIN_7J, 200), "top_200_ca_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.data");
    }

    @Test
    public void shouldCreateTop200VentesGlobal7JFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_VENTES_GLOBAL_7J, 200), "top_200_ventes_GLOBAL_20190304-J7.data");
    }

    @Test
    public void shouldCreateTop200CAGlobal7JFileName() {
        Assert.assertEquals(FilenameUtil.buildFileName(MAGASINID, DATE, FilenameUtil.FileType.RESULT_CA_GLOBAL_7J, 200), "top_200_ca_GLOBAL_20190304-J7.data");
    }

    @Test
    public void shouldExtractDateGivenAllPossibleFilenamesWithPotentialMatchingInUUID() {
        Assert.assertEquals(DATE, FilenameUtil.extractDate("transactions_20190304.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("reference_prod-01234567-5aa2-4ad8-8ba9-012345678910_20190304.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("listing_produit-01234567-5aa2-4ad8-8ba9-012345678910_20190304.stage1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-01234567-5aa2-4ad8-8ba9-012345678910_20190304.stage2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-01234567-5aa2-4ad8-8ba9-012345678910_20190304.stage3"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-GLOBAL_20190304.stage4-2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-01234567-5aa2-4ad8-8ba9-012345678910_20190304-J7.stage4-3"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-01234567-5aa2-4ad8-8ba9-012345678910_20190304-J7.stage4-4"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-GLOBAL_20190304-J7.stage5-1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-GLOBAL_20190304-J7.stage5-2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ventes_01234567-5aa2-4ad8-8ba9-012345678910_20190304.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ca_01234567-5aa2-4ad8-8ba9-012345678910_20190304.data\""));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ventes_GLOBAL_20190304.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ca_GLOBAL_20190304.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ventes_01234567-5aa2-4ad8-8ba9-012345678910_20190304-J7.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ca_01234567-5aa2-4ad8-8ba9-012345678910_20190304-J7.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ventes_GLOBAL_20190304-J7.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("top_200_ca_GLOBAL_20190304-J7.data"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage3"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-GLOBAL_20190304.stage4-2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-3"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-4"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-GLOBAL_20190304-J7.stage5-1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-GLOBAL_20190304-J7.stage5-2"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals(DATE, FilenameUtil.extractDate("set_ca-GLOBAL_20190304.stage4-2"));

    }

    @Test
    public void shouldExtractFilenameGivenAllPossibleFilenames() {

        //Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("transactions_20190304.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("reference_prod-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage1"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage2"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage3"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_ca-GLOBAL_20190304.stage4-2"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-3"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-4"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_produit-GLOBAL_20190304-J7.stage5-1"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_ca-GLOBAL_20190304-J7.stage5-2"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("top_200_ventes_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("top_200_ca_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.data\""));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("top_200_ventes_GLOBAL_20190304.data"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("top_200_ca_GLOBAL_20190304.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("top_200_ventes_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("top_200_ca_2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.data"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("top_200_ventes_GLOBAL_20190304-J7.data"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("top_200_ca_GLOBAL_20190304-J7.data"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("listing_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage1"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage2"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304.stage3"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_ca-GLOBAL_20190304.stage4-2"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_produit-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-3"));
        Assert.assertEquals(MAGASINID, FilenameUtil.extractMagasinId("set_ca-2a4b6b81-5aa2-4ad8-8ba9-ae1a006e7d71_20190304-J7.stage4-4"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_produit-GLOBAL_20190304-J7.stage5-1"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_ca-GLOBAL_20190304-J7.stage5-2"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_produit-GLOBAL_20190304.stage4-1"));
        Assert.assertEquals("GLOBAL", FilenameUtil.extractMagasinId("set_ca-GLOBAL_20190304.stage4-2"));

    }


}

package com.zenika.test_carrefour.utils;


import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CLIOptions {

    private static final String VENTE_MAGASIN_ARG = "venteMagasin" ;

    private static final String CA_MAGASIN_ARG = "caMagasin" ;

    private static final String VENTE_GLOBAL_ARG = "venteGlobal" ;

    private static final String CA_GLOBAL_ARG = "caGlobal" ;

    private static final String VENTE_MAGASIN_J_7_ARG = "venteMagasinJ7" ;

    private static final String CA_MAGASIN_J_7_ARG = "caMagasinJ7" ;

    private static final String VENTE_GLOBAL_J_7_ARG = "venteGlobalJ7" ;

    private static final String CA_GLOBAL_J_7_ARG = "caGlobalJ7" ;

    private static final String ALL_ARG = "all" ;

    private static final String FILE_ARG = "file" ;

    private static final String TOP_N_ARG = "topN" ;

    private final ArgumentAcceptingOptionSpec<File> fileArgOpt;

    private final ArgumentAcceptingOptionSpec<Integer> topNOpt;

    private final OptionSet options;

    private final Set<OptionSpec> stages ;

    private final OptionParser parser;

    //Command line interface configuration
    public CLIOptions(String[] args) {

        this.parser = new OptionParser(false) ;

        this.fileArgOpt = parser.accepts(FILE_ARG).withRequiredArg().ofType(File.class);

        this.topNOpt = parser.accepts(TOP_N_ARG).withRequiredArg().ofType(Integer.class) ;

        OptionSpecBuilder venteMagasinSpecBuilder = parser.accepts(VENTE_MAGASIN_ARG);
        OptionSpecBuilder caMagasinSpecBuilder = parser.accepts(CA_MAGASIN_ARG);
        OptionSpecBuilder venteGlobalSpecBuilder = parser.accepts(VENTE_GLOBAL_ARG);
        OptionSpecBuilder caGlobalSpecBuilder = parser.accepts(CA_GLOBAL_ARG);

        OptionSpecBuilder venteMagasinJ7SpecBuilder = parser.accepts(VENTE_MAGASIN_J_7_ARG);
        OptionSpecBuilder caMagasinJ7SpecBuilder = parser.accepts(CA_MAGASIN_J_7_ARG);
        OptionSpecBuilder venteGlobalJ7SpecBuilder = parser.accepts(VENTE_GLOBAL_J_7_ARG);
        OptionSpecBuilder caGlobalJ7SpecBuilder = parser.accepts(CA_GLOBAL_J_7_ARG);

        OptionSpecBuilder allSpecBuilder = parser.accepts(ALL_ARG);

        stages = new HashSet<>();
        stages.add(venteMagasinSpecBuilder);
        stages.add(caMagasinSpecBuilder);
        stages.add(venteGlobalSpecBuilder);
        stages.add(caGlobalSpecBuilder);
        stages.add(venteMagasinJ7SpecBuilder);
        stages.add(caMagasinJ7SpecBuilder);
        stages.add(venteGlobalJ7SpecBuilder);
        stages.add(caGlobalJ7SpecBuilder);
        stages.add(allSpecBuilder);

        //User can only specify a single stage (or the all stage)
        parser.mutuallyExclusive(
                venteMagasinSpecBuilder,
                caMagasinSpecBuilder,
                venteGlobalSpecBuilder,
                caGlobalSpecBuilder,
                venteMagasinJ7SpecBuilder,
                caMagasinJ7SpecBuilder,
                venteGlobalJ7SpecBuilder,
                caGlobalJ7SpecBuilder,
                allSpecBuilder);

        options = parser.parse(args);
    }

    public boolean isStage2Command() {
        return options.has(VENTE_MAGASIN_ARG);
    }

    public boolean isStage3Command() {
        return options.has(CA_MAGASIN_ARG);
    }

    public boolean isStage4_1Command() {
        return options.has(VENTE_GLOBAL_ARG);
    }

    public boolean isStage4_2Command() {
        return options.has(CA_GLOBAL_ARG);
    }

    public boolean isStage4_3Command() {
        return options.has(VENTE_MAGASIN_J_7_ARG);
    }

    public boolean isStage4_4Command() {
        return options.has(CA_MAGASIN_J_7_ARG);
    }

    public boolean isStage5_1Command() {
        return options.has(VENTE_GLOBAL_J_7_ARG);
    }

    public boolean isStage5_2Command() {
        return options.has(CA_GLOBAL_J_7_ARG);
    }

    public boolean isFullWorkflowCommand() {
        return options.has(ALL_ARG);
    }

    public File fileOpt() {
        return this.fileArgOpt.value(options);
    }

    public int topNOpt() {
        return this.topNOpt.value(options);
    }

    public void checkArgs() {
        if (!options.has(TOP_N_ARG)) {
            System.err.println("Missing required argument : " + TOP_N_ARG);
            System.exit(1);
        }
        if (!options.has(FILE_ARG)) {
            System.err.println("Missing required argument : " + FILE_ARG);
            System.exit(1);
        }
        if (stages.stream().filter(options::has).count() != 1) {
            System.err.println("Please ask for single file type from : " + VENTE_MAGASIN_ARG + ";" + CA_MAGASIN_ARG + ";" + VENTE_GLOBAL_ARG + ";" + CA_GLOBAL_ARG + ";" + VENTE_MAGASIN_J_7_ARG + ";" + CA_MAGASIN_J_7_ARG + ";" + VENTE_GLOBAL_J_7_ARG + ";" + CA_GLOBAL_J_7_ARG + ";" + ALL_ARG);
            System.exit(1);
        }
    }
}

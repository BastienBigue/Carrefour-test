package com.zenika.test_carrefour.utils;

import joptsimple.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CLIOptions {

    private static final String venteMagasinArg = "venteMagasin" ;

    private static final String caMagasinArg = "caMagasin" ;

    private static final String venteGlobalArg = "venteGlobal" ;

    private static final String caGlobalArg = "caGlobal" ;

    private static final String venteMagasinJ7Arg = "venteMagasinJ7" ;

    private static final String caMagasinJ7Arg = "caMagasinJ7" ;

    private static final String venteGlobalJ7Arg = "venteGlobalJ7" ;

    private static final String caGlobalJ7Arg = "caGlobalJ7" ;

    private static final String allArg = "all" ;

    private static final String fileArg = "file" ;

    private static final String topNArg = "topN" ;

    private final ArgumentAcceptingOptionSpec<File> fileArgOpt;

    private final ArgumentAcceptingOptionSpec<Integer> topNOpt;

    private final OptionSet options;

    private final Set<OptionSpec> stages ;

    public final OptionParser parser;

    //Command line interface configuration
    public CLIOptions(String[] args) {

        this.parser = new OptionParser(false) ;

        this.fileArgOpt = parser.accepts(fileArg).withRequiredArg().ofType(File.class);

        this.topNOpt = parser.accepts(topNArg).withRequiredArg().ofType(Integer.class) ;

        OptionSpecBuilder venteMagasinSpecBuilder = parser.accepts(venteMagasinArg);
        OptionSpecBuilder caMagasinSpecBuilder = parser.accepts(caMagasinArg);
        OptionSpecBuilder venteGlobalSpecBuilder = parser.accepts(venteGlobalArg);
        OptionSpecBuilder caGlobalSpecBuilder = parser.accepts(caGlobalArg);

        OptionSpecBuilder venteMagasinJ7SpecBuilder = parser.accepts(venteMagasinJ7Arg);
        OptionSpecBuilder caMagasinJ7SpecBuilder = parser.accepts(caMagasinJ7Arg);
        OptionSpecBuilder venteGlobalJ7SpecBuilder = parser.accepts(venteGlobalJ7Arg);
        OptionSpecBuilder caGlobalJ7SpecBuilder = parser.accepts(caGlobalJ7Arg);

        OptionSpecBuilder allSpecBuilder = parser.accepts(allArg);

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
        parser.mutuallyExclusive(venteMagasinSpecBuilder, caMagasinSpecBuilder, venteGlobalSpecBuilder, caGlobalSpecBuilder, venteMagasinJ7SpecBuilder, caMagasinJ7SpecBuilder, venteGlobalJ7SpecBuilder, caGlobalJ7SpecBuilder, allSpecBuilder);

        options = parser.parse(args);
    }

    public boolean isStage2Command() {
        return options.has(venteMagasinArg);
    }

    public boolean isStage3Command() {
        return options.has(caMagasinArg);
    }

    public boolean isStage4_1Command() {
        return options.has(venteGlobalArg);
    }

    public boolean isStage4_2Command() {
        return options.has(caGlobalArg);
    }

    public boolean isStage4_3Command() {
        return options.has(venteMagasinJ7Arg);
    }

    public boolean isStage4_4Command() {
        return options.has(caMagasinJ7Arg);
    }

    public boolean isStage5_1Command() {
        return options.has(venteGlobalJ7Arg);
    }

    public boolean isStage5_2Command() {
        return options.has(caGlobalJ7Arg);
    }

    public boolean isFullWorkflowCommand() {
        return options.has(allArg);
    }

    public File fileOpt() {
        return this.fileArgOpt.value(options);
    }

    public int topNOpt() {
        return this.topNOpt.value(options);
    }

    public void checkArgs() {
        if (!options.has(topNArg)) {
            System.err.println("Missing required argument : " + topNArg);
            System.exit(1);
        }
        if (!options.has(fileArg)) {
            System.err.println("Missing required argument : " + fileArg);
            System.exit(1);
        }
        if (stages.stream().filter(options::has).count() != 1) {
            System.err.println("Please ask for single file type from : " + venteMagasinArg + ";" + caMagasinArg + ";" + venteGlobalArg + ";" + caGlobalArg + ";" + venteMagasinJ7Arg + ";" + caMagasinJ7Arg + ";" + venteGlobalJ7Arg + ";" + caGlobalJ7Arg + ";" + allArg);
            System.exit(1);
        }
    }
}

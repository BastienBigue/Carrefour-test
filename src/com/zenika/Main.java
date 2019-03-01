package com.zenika;

import com.zenika.utils.TransactionFileMapper;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    // static Logger logger = Logger.getLogger(PropertiesFileLog4jExample.class);

    public static void main(String[] args) {
        /*String log4jConfigFile = System.getProperty("user.dir")
                + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        logger.debug("this is a debug log message");
        logger.info("this is a information log message");
        logger.warn("this is a warning log message");*/

        TransactionFileMapper mapper = new TransactionFileMapper(new File("data","transactions_20170514.data")) ;
        if (mapper.extractDate()) {
            try{
                mapper.processTransactionFile();
            } catch (IOException e) {
                System.out.println("IOException !!!!") ;
            }
        };

    }
}

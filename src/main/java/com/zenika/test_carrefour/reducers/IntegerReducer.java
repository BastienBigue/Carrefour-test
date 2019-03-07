package com.zenika.test_carrefour.reducers;

import com.zenika.test_carrefour.config.CommonConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Set;

public class IntegerReducer extends Reducer<Integer> {

    private static Logger LOG = LogManager.getLogger(IntegerReducer.class);

    public IntegerReducer(Set<File> filesToAggregate,
                          int topN,
                          File outputFullFile,
                          File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
    }

    //Parse the line and expects to find a Float as currentLine[1]. Add each line to the productMap.
    //If a line can't be parser, it is discarded.
    public void parseAndInsertInMap(String[] currentLine) {
        String product = currentLine[0];
        try {
            this.productMap.put(product, this.productMap.getOrDefault(product, 0) + Integer.valueOf(currentLine[1]));
        } catch (NumberFormatException e) {
            LOG.error("Impossible to parse " + currentLine[1] + "as Integer. Line is discarded.") ;
        }
    }

    //Converts a record into a String writable in result or stage file.
    public String buildLine(String product, Integer value) {
        return product.concat(CommonConfig.CSV_SEPARATOR).concat(this.productMap.get(product).toString());
    }
}
package com.zenika.test_carrefour.reducers;

import com.zenika.test_carrefour.config.CommonConfig;

import java.io.File;
import java.util.Set;

public class IntegerReducer extends Reducer<Integer> {

    public IntegerReducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
    }

    public void parseAndInsertInMap(String[] currentLine) {
        String product = currentLine[0];
        try {
            this.productMap.put(product, this.productMap.getOrDefault(product, 0) + Integer.valueOf(currentLine[1]));
        } catch (NumberFormatException e) {
            log.error("Impossible to parse " + currentLine[1] + "as Integer. Line is discarded.") ;
        }
    }

    public String buildLine(String product, Integer value) {
        return product.concat(CommonConfig.CSV_SEPARATOR).concat(this.productMap.get(product).toString());
    }
}
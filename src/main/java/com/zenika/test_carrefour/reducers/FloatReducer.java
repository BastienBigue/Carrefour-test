package com.zenika.test_carrefour.reducers;

import com.zenika.test_carrefour.config.CommonConfig;

import java.io.File;
import java.util.Locale;
import java.util.Set;

public class FloatReducer extends Reducer<Float> {

    public FloatReducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
    }

    //Parse the line and expects to find a Float as currentLine[1]. Add each line to the productMap.
    //If a line can't be parser, it is discarded.
    public void parseAndInsertInMap(String[] currentLine) {
        String product = currentLine[0];
        try {
            this.productMap.put(product, this.productMap.getOrDefault(product, 0.0f) + Float.valueOf(currentLine[1]));
        } catch (NumberFormatException e) {
            log.error("Impossible to parse " + currentLine[1] + "as Float. Line is discarded.") ;
        }
    }

    //Converts a record into a String writable in result or stage file.
    public String buildLine(String product, Float value) {
        return product.concat(CommonConfig.CSV_SEPARATOR).concat(String.format(Locale.US, "%.2f", this.productMap.get(product)) );
    }
}

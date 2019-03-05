package com.zenika.test_carrefour.reducers;

import java.io.File;
import java.util.Set;

public class FloatReducer extends Reducer<Float> {

    public FloatReducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
    }

    public void parseAndInsertInMap(String[] currentLine) {
        String product = currentLine[0];
        this.productMap.put(product, this.productMap.getOrDefault(product, 0.0f) + Float.valueOf(currentLine[1]));
    }
}

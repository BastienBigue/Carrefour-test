package com.zenika.test_carrefour.reducers;

import java.io.File;
import java.util.Set;

public class IntegerReducer extends Reducer<Integer> {

    public IntegerReducer(Set<File> filesToAggregate, int topN, File outputFullFile, File outputTopNSortedFile) {
        super(filesToAggregate, topN, outputFullFile, outputTopNSortedFile);
    }

    public void parser(String[] currentLine) {
        String product = currentLine[0];
        this.productMap.put(product, this.productMap.getOrDefault(product, 0) + Integer.valueOf(currentLine[1]));
    }
}
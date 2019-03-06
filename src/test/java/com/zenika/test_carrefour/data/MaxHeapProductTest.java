package com.zenika.test_carrefour.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class MaxHeapProductTest {

    @Test
    public void shouldBuildUnsortedIntegerTreeGivenFilledHashmap() {

        HashMap<String, Integer> testInteger = new HashMap<>() ;

        testInteger.put("a", 5) ;
        testInteger.put("b", 3) ;
        testInteger.put("c", 17) ;
        testInteger.put("d", 10) ;
        testInteger.put("e", 84) ;
        testInteger.put("f", 56) ;
        testInteger.put("g", 19) ;
        testInteger.put("h", 6) ;
        testInteger.put("i", 22) ;
        testInteger.put("j", 9) ;
        testInteger.put("k", 56) ;


        MaxHeapProduct heap = new MaxHeapProduct(testInteger) ;
        String[] sortedTable = heap.extractTopN(10) ;
        for (int i = 1 ; i < 10 ; i++) {
            Assert.assertTrue((testInteger.get(sortedTable[i]) <= testInteger.get(sortedTable[i-1]))) ;
        }
    }

    @Test
    public void shouldBuildUnsortedFloatTreeGivenFilledHashmap() {
        HashMap<String, Float> testFloat = new HashMap<>();
        testFloat.put("a", 5.11f);
        testFloat.put("b", 3.99f);
        testFloat.put("c", 17.23f);
        testFloat.put("d", 10.54f);
        testFloat.put("e", 84.78f);
        testFloat.put("f", 56.40f);
        testFloat.put("g", 19.34f);
        testFloat.put("h", 6.45f);
        testFloat.put("i", 22.72f);
        testFloat.put("j", 9.29f);
        testFloat.put("k", 56.40f);

        MaxHeapProduct heap = new MaxHeapProduct(testFloat) ;
        String[] sortedTable = heap.extractTopN(10) ;
        for (int i = 1 ; i < 10 ; i++) {
            Assert.assertTrue((testFloat.get(sortedTable[i]) <= testFloat.get(sortedTable[i-1]))) ;
        }
    }
}
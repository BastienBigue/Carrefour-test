package com.zenika.test_carrefour.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MaxHeapProductTest {

    @Test
    public void shouldBuildUnsortedIntegerTreeGivenFilledHashmap() {

        HashMap<String, Integer> testInteger = new HashMap<>() ;

        testInteger.put("a", 5) ;
        testInteger.put("b", 3) ;
        testInteger.put("c", 17) ;
        testInteger.put("d", 10) ;
        testInteger.put("e", 84) ;
        testInteger.put("f", 19) ;
        testInteger.put("g", 6) ;
        testInteger.put("h", 22) ;
        testInteger.put("i", 9) ;
        testInteger.put("j", 56) ;


        MaxHeapProduct heap = new MaxHeapProduct(testInteger) ;
        String[] sortedTable = heap.extractTopN(10) ;
        for (int i = 1 ; i < 10 ; i++) {
            Assert.assertTrue((testInteger.get(sortedTable[i]) < testInteger.get(sortedTable[i-1]))) ;
        }
    }

    @Test
    public void shouldBuildUnsortedFloatTreeGivenFilledHashmap() {
        HashMap<String, Float> testFloat = new HashMap<>();
        testFloat.put("a", new Float(5.2));
        testFloat.put("b", new Float(3.2));
        testFloat.put("c", new Float(17.2));
        testFloat.put("d", new Float(10.2));
        testFloat.put("e", new Float(84.2));
        testFloat.put("f", new Float(19.2));
        testFloat.put("g", new Float(6.2));
        testFloat.put("h", new Float(22.2));
        testFloat.put("i", new Float(9.2));

        MaxHeapProduct heap = new MaxHeapProduct(testFloat) ;
        String[] sortedTable = heap.extractTopN(10) ;
        for (int i = 1 ; i < 10 ; i++) {
            Assert.assertTrue((testFloat.get(sortedTable[i]) < testFloat.get(sortedTable[i-1]))) ;
        }
    }
}
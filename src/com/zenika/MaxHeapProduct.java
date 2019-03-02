package com.zenika;


import java.util.HashMap;
import java.util.Map;

public class MaxHeapProduct<T extends Comparable<T>> {
    private String[] Heap;
    private int size;
    private Map<String, T> productMap ;

    // Constructor to initialize an
    // empty max heap with given maximum
    // capacity.
    public MaxHeapProduct(Map<String,T> productMap) {
        this.productMap = productMap ;
        this.size = productMap.size();
        Heap = new String[this.size];
        this.buildTree();
    }

    // Returns position of parent
    private int parent(int pos) {
        return (pos-1)/ 2;
    }

    // Below two functions return left and
    // right children.
    private int leftChild(int pos) {
        return (2 * pos + 1);
    }

    private int rightChild(int pos) {
        return (2 * pos) + 2;
    }

    // Returns true of given node is leaf
    private boolean isLeaf(int pos) {
        if (pos > (size / 2) && pos <= size) {
            return true;
        }
        return false;
    }

    private void swap(int fpos, int spos) {
        String tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    private void heapify(int i)
    {
        int largest = i; // Initialize largest as root
        int l = leftChild(i) ;
        int r = rightChild(i) ;

        // If left child is larger than root
        if (l < this.size && productMap.get(Heap[l]).compareTo(productMap.get(Heap[largest])) == 1)
            largest = l;

        // If right child is larger than largest so far
        if (r < this.size && productMap.get(Heap[r]).compareTo(productMap.get(Heap[largest]))== 1)
            largest = r;

        // If largest is not root
        if (largest != i)
        {
            swap(i, largest);
            // Recursively heapify the affected sub-tree
            heapify(largest);
        }
    }

    public void print() {
        for (int i = 0; i < this.size/2; i++) {
            System.out.print(" PARENT : " + productMap.get(Heap[i]) + " LEFT CHILD : " +
                    productMap.get(Heap[2 * i + 1]) + " RIGHT CHILD :" + productMap.get(Heap[2 * i + 2]));
            System.out.println();
        }
    }


    private void buildTree() {
        Heap = this.productMap.keySet().toArray(new String[0]) ;

        for (int i = Math.floorDiv(this.size,2)-1; i>=0 ; i--) {
            this.heapify(i);
        }
    }

    // Remove an element from max heap
    private String extractMax() {
        String popped = Heap[0];
        Heap[0] = Heap[--size];
        heapify(0);
        return popped;
    }

    //Returns topN elements from array. Removes them.
    public String[] extractTopN(int topN) {
        String [] result  = new String[topN];
        String currMax = null ;
        for (int i = 0; i < topN ; i++) {
            currMax =  this.extractMax() ;
            result[i] = currMax;
        }
        return result ;
    }

    public static void main(String[] arg)
    {

        HashMap<String, Integer> testMagasin = new HashMap<>() ;
        testMagasin.put("a", 5) ;
        testMagasin.put("b", 3) ;
        testMagasin.put("c", 17) ;
        testMagasin.put("d", 10) ;
        testMagasin.put("e", 84) ;
        testMagasin.put("f", 19) ;
        testMagasin.put("g", 6) ;
        testMagasin.put("h", 22) ;
        testMagasin.put("i", 9) ;

        MaxHeapProduct maxHeap = new MaxHeapProduct(testMagasin) ;
        maxHeap.print();
        String[] result = maxHeap.extractTopN(9);
        for (int i=0; i<result.length;i++) {
            System.out.print(result[i]+";");
        }
        System.out.println();

        for (int i=0; i<result.length;i++) {
            System.out.print(maxHeap.productMap.get(result[i])+";");
        }

        System.out.println();


        HashMap<String, Float> testMagasinFloat = new HashMap<>() ;
        testMagasinFloat.put("a", new Float(5.2)) ;
        testMagasinFloat.put("b", new Float(3.2)) ;
        testMagasinFloat.put("c", new Float(17.2)) ;
        testMagasinFloat.put("d", new Float(10.2)) ;
        testMagasinFloat.put("e", new Float(84.2)) ;
        testMagasinFloat.put("f", new Float(19.2)) ;
        testMagasinFloat.put("g", new Float(6.2)) ;
        testMagasinFloat.put("h", new Float(22.2)) ;
        testMagasinFloat.put("i", new Float(9.2)) ;

        MaxHeapProduct maxHeapFloat = new MaxHeapProduct(testMagasin) ;
        maxHeap.print();
        String[] resultFloat = maxHeapFloat.extractTopN(9);
        for (int i=0; i<resultFloat.length;i++) {
            System.out.print(resultFloat[i]+";");
        }

        System.out.println();
        for (int i=0; i<resultFloat.length;i++) {
            System.out.print(maxHeapFloat.productMap.get(resultFloat[i])+";");
        }

    }
}
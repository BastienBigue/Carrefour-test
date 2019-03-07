package com.zenika.test_carrefour.data;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxHeapProduct<T extends Comparable<T>> {

    private static Logger log = LogManager.getLogger(MaxHeapProduct.class);

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

    //Swap the 2 given nodes
    private void swap(int fpos, int spos) {
        String tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    //Heapify subtree whose root node is i.
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

    //Pretty print tree
    public void print() {
        for (int i = 0; i < this.size/2; i++) {
            System.out.print(" PARENT : " + productMap.get(Heap[i]) + " LEFT CHILD : " +
                    productMap.get(Heap[2 * i + 1]) + " RIGHT CHILD :" + productMap.get(Heap[2 * i + 2]));
            System.out.println();
        }
    }

    //Build tree with keys of map. Keys are inserted without heapifying. Heapify is done on the non-leaf nodes to order the entire tree.
    private void buildTree() {
        long start = System.currentTimeMillis() ;
        Heap = this.productMap.keySet().toArray(new String[0]) ;

        for (int i = Math.floorDiv(this.size,2)-1; i>=0 ; i--) {
            this.heapify(i);
        }
        long end = System.currentTimeMillis() ;
        log.debug("Build binary tree took " + String.valueOf(end-start) + "ms");
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
        int topNorAllproduct = Math.min(topN,this.size) ;
        if (topNorAllproduct == this.size) {
            log.warn("topN requested is greated than the number of products. Compute ranking over entire set of products");
        }
        String [] result  = new String[topN];
        String currMax = null ;
        for (int i = 0; i < topN ; i++) {
            currMax =  this.extractMax() ;
            result[i] = currMax;
        }
        return result ;
    }
}
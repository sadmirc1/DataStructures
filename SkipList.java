import java.util.*;

//Based off of Data Structures and Algorithms in Java, 6th edition. Chapter 10 has a SkipList explanation.

//Always sorted.
//Binary search done within a linked list (sort of).
//Storage wise, it's like a HashMap: a LinkedList where each node is a container for duplicate values.
//Instead of each level 0 entry and up acting like a bucket for a HashMap, 
//                          when you add an element it's randomly determined how many nodes 
//                          up you go for the express lanes.

//Potential improvements: make each "tower" a single Object like a HashMap's bucket in a way.
public class SkipList<E extends Comparable<? super E>> {
    private static class SkipListNode<E> {
        private E data;
        private SkipListNode<E> next, prev, above, below;
        public SkipListNode(E element) {
            this.data = element;
            next = prev = above = below = null;
        }
    }
    private SkipListNode<E> head;
    private int currentLevel;
    private int totalEntries;
    private Random randomGen;
    public SkipList(E headData) {
        currentLevel = totalEntries = 0;
        randomGen = new Random();
        head = new SkipListNode(headData); //head is the top left most node
        totalEntries = 1;
    }

    public SkipListNode<E> below(SkipListNode<E> p) {
        return p.below;
    }

    public SkipListNode<E> above(SkipListNode<E> p) {
        return p.above;
    }
    
    public SkipListNode<E> next(SkipListNode<E> p) {
        return p.next;
    }

    public SkipListNode<E> prev(SkipListNode<E> p) {
        return p.prev;
    }

    public SkipListNode<E> search(E e) {
        SkipListNode<E> p = head;
        while (below(p) != null) {
            p = below(p);
            while (e.compareTo(next(p).data) >= 0) {
                p = next(p);
            }
        }
        return p;
    }
    
    //My original version of the Node class had a field for each node's level in the SkipList
    //But that's not necessary because there is no need to track the level
    //The only reason I thought I needed is because the new variable "node" had to be on the same level as the parameter p
    //But they can be on the same level without any internal fields for int level by just setting them adjacent to each other horizontally.
    public SkipListNode<E> insertAfterAbove(SkipListNode<E> p, SkipListNode<E> q, E e) {
        SkipListNode<E> node = new SkipListNode(e);
        node.prev = p;
        node.next = p.next;
        p.next = node;
        node.below = q;
        //if (q != null) {} Going above q is not necessary 
        //because p should ALWAYS be in the level above q
        //at least in the looping in the textbook
        //when we make it per iteration there's nothing above the new node
        return node;
    }
        

    public SkipListNode<E> insert(E e) {
        SkipListNode<E> p = search(e);
        SkipListNode<E> q = null;
        int i = -1;
        boolean canAdd = true;
        while (canAdd) {
            i++;
            if (i >= currentLevel) {
                currentLevel++;
                //insertAfterAbove
                //insertAfterAbove
            }
            q = insertAfterAbove(p, q, e);
            while (above(p) == null) {
                p = prev(p);
            }
            p = above(p);
            canAdd = randomGen.nextBoolean();
        }   
        totalEntries++;
        return q;
    }

    //public SkipListNode<E> remove(E e) {
    public void remove(E e) {
        if (e == null) {
            //return null; //throw new NullPointerException
            return;
        }
        SkipListNode<E> searchResult = search(e);
        //SkipListNode<E> current = searchResult; in case I decide to return the removed node, not sure how I want to implement this.
        if (searchResult == null) {
            throw new NoSuchElementException();
        }
        if (!e.equals(searchResult.data)) {
            return;
        }
        while (searchResult != null) {
            searchResult.prev.next = searchResult.next;
            searchResult.next.prev = searchResult.prev;
        }
        //return searchResult;
    }
}
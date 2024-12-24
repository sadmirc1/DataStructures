import java.util.*;

//Based off of Data Structures and Algorithms in Java, 6th edition. Chapter 10 has a SkipList explanation.

//Always sorted.
//Binary search done within a linked list (sort of).
//Storage wise, it's like a HashMap: a LinkedList where each node is a container for duplicate values.
//Instead of each level 0 entry and up acting like a bucket for a HashMap, 
//                          when you add an element it's randomly determined how many nodes 
//                          up you go for the express lanes.
// Ideally I would overwrite an existing key if two keys are used as duplicates, but I'm not sure how to do that efficiently.
public class SkipList<E extends Comparable<? super E>> {
    private static class SkipListNode<E> {
        private int key;
        private E data;
        private SkipListNode<E> next, prev, above, below;
        public SkipListNode(int k, E element) {
            this.key = k;
            this.data = element;
            next = prev = above = below = null;
        }
        public String toString() {
            if (key == Integer.MIN_VALUE) {
                return "[-∞]"; //just for easier labeling
            } else if (key == Integer.MAX_VALUE) {
                return "[∞]";
            }
            return "[" + key + ", " + data.toString() + "]";
        }
    }
    private SkipListNode<E> head;
    private SkipListNode<E> tail;
    private int currentLevel;
    private int totalEntries;
    private Random randomGen;
    public SkipList() {
        currentLevel = totalEntries = 0;
        randomGen = new Random();
        head = new SkipListNode<>(Integer.MIN_VALUE, null); //head is the top left most node with no proper value
        tail = new SkipListNode<>(Integer.MAX_VALUE, null);
        head.next = tail;
        tail.prev = head;
        currentLevel = 0;
        totalEntries = 0;
    }

    private int key(SkipListNode<E> p) {
        return p.key;
    }

    private SkipListNode<E> below(SkipListNode<E> p) {
        return p.below;
    }

    private SkipListNode<E> above(SkipListNode<E> p) {
        return p.above;
    }
    
    private SkipListNode<E> next(SkipListNode<E> p) {
        return p.next;
    }

    private SkipListNode<E> prev(SkipListNode<E> p) {
        return p.prev;
    }

    //Returns the node that would be right before the node to be inserted or removed, based on key ordering
    public SkipListNode<E> search(int key) {
        SkipListNode<E> p = head;
        while (below(p) != null) {
            p = below(p);
            while (key >= key(next(p))) {
                p = next(p);
            }
        }
        return p;
    }
    
    //if p is null -> only for making new head
    //make new node: to right of p (same level), above q
    public SkipListNode<E> insertAfterAbove(SkipListNode<E> p, SkipListNode<E> q, int key, E e) {
        SkipListNode<E> node = new SkipListNode<>(key, e);
        node.prev = p;
        node.below = q;
        if (p != null) {
            node.next = p.next;
            p.next = node;
        }
        if (q != null) { //if not bottom level
            q.above = node;
        }
        return node;
    }
    
    public SkipListNode<E> insert(int key, E e) {
        SkipListNode<E> p = search(key);
        SkipListNode<E> q = null;
        int i = -1;
        boolean canAdd = true;
        while (canAdd) {
            i++;
            if (i >= currentLevel) {
                currentLevel++;
                tail = next(head);
                head = insertAfterAbove(null, head, Integer.MIN_VALUE, null); //Make a new level for the head column, keeping links
                insertAfterAbove(head, tail, Integer.MAX_VALUE, null); //Make a new node for the tail column that connects to the newly made head and tail underneath it
            }
            q = insertAfterAbove(p, q, key, e);
            while (above(p) == null) {
                p = prev(p);
            }
            p = above(p);
            canAdd = randomGen.nextBoolean();
        }   
        totalEntries++;
        return q;
    }

    public void remove(int key) {
        SkipListNode<E> searchResult = search(key);
        if (searchResult == null || key(searchResult) != key) {
            throw new NoSuchElementException();
        }
        while (searchResult != null) {
            if (searchResult.prev != null) {
                searchResult.prev.next = searchResult.next;
            }
            if (searchResult.next != null) {
                searchResult.next.prev = searchResult.prev;
            }
            searchResult = searchResult.above;
        }
        totalEntries--;
    }

    //An actual get method because the search method does not return an exact retrieval
    //With current implementation of existing duplicates this can have issues
    public E get(int key) {
        SkipListNode<E> node = search(key);
        if (key == key(node)) {
            return node.data;
        }
        return null;
    }

    

    public String toString() {
        StringBuilder sb = new StringBuilder();
        SkipListNode<E> currentHeadLevel = head;
        SkipListNode<E> currentNode = currentHeadLevel;
        System.out.println("Current level: " + currentLevel);
        for (int lvl = currentLevel; lvl >= 0; lvl--) {
            sb.append(lvl).append(": ");
            while (currentNode != null) {
                sb.append(currentNode.toString());
                currentNode = next(currentNode);
                if (currentNode != null) {
                    sb.append(" -> ");
                }
            }
            if (lvl > 0) {
                sb.append("\n");
                currentHeadLevel = currentNode = below(currentHeadLevel);
            }
            
        }
        return sb.toString();
    }
}
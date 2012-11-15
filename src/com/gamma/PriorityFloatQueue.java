package com.gamma;

import java.util.*;

public class PriorityFloatQueue
{
    private int size = 0;
    private final ArrayList<Float> elements;
    
    public PriorityFloatQueue()
    {
        elements = new ArrayList<Float>(32);
        elements.add(null); // first slot has index 1
    }

    public PriorityFloatQueue(Collection<Float> coll)
    {
        this();
        elements.addAll(coll);
        size = coll.size();

        for (int k = coll.size() / 2; k >= 1; --k)
            heapify(k);
    }

    /**
     * A new element <code>o</code> is added to the priority queue
     * in O(log n) time where n is the size of the priority queue.
     * <P>
     * The return value should be ignored, a boolean value must be
     * returned because of the requirements of the
     * <code>Collection</code> interface.
     *
     * @param o is the (Comparable) object added to the priority queue
     * @return true
     */
    
    public boolean add(float value)
    {
        elements.add(value);        // stored, but not correct location
        size++;             // added element, update count
        int k = size;       // location of new element

        while (k > 1 && elements.get(k >> 1) > value)
        {
            elements.set(k, elements.get(k >> 1));
            k >>= 1;
        }
        elements.set(k, value);
        
        return true;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * The smallest/minimal element is removed and returned
     * in O(log n) time where n is the size of the priority queue.
     *
     * @return the smallest element (and removes it)
     */
    
    public float remove()
    {
        if (!isEmpty())
        {
            float hold = elements.get(1);
            
            elements.set(1, elements.get(size));  // move last to top
            elements.remove(size);              // pop last off
            size--;
            
            if (size > 1)
                heapify(1);

            return hold;
        }
        
        throw new Error("PriorityFloatQueue was empty when remove was called!");
    }

    /**
     * Returns smallest element in O(1) time
     * @return the minimal element in the priority queue
     */
    
    public float peek()
    {
        return elements.get(1);
    }

    /**
     * works in O(log(size()-vroot)) time
     * @param vroot is the index at which re-heaping occurs
     * @precondition: subheaps of index vroot are heaps
     * @postcondition: heap rooted at index vroot is a heap
     */
    
    private void heapify(int vroot)
    {
        float last = elements.get(vroot);
        int child, k = vroot;
        
        while (k << 1 <= size)
        {
            child = k << 1;
            
            if (child < size && elements.get(child) > elements.get(child + 1))
                ++child;

            if (last > elements.get(child))
            {
                elements.set(k, elements.get(child));
                k = child;
            }
            else
            	break;
        }
        elements.set(k, last);
    }
}

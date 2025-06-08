package cp3.lab04.expandablearray;

/**
 * The class implements an adaptable array of arbitrary objects, i.e.,
 * the size of the array can be increased or decreased according to the number
 * of objects to be stored. The adaptation is realized by the method add():
 * If the array data is fully occupied when trying to add a new object,
 * the size of the array will be increased by allocating a larger array and
 * using the method arraycopy() from the java.lang.System class to copy the
 * content of the old array into the new array. Without the synchronization
 * included, the class cannot be used concurrently by more than one thread safely.
 * A conflict could occur if, e.g., two threads tried to perform an add()
 * operation at the same time.
 */
public class ExpandableArray
{

    private Object[] data;
    private int size = 0;

    /**
     * Initialize the array with an initial capacity of <code>cap</code>.
     *
     * @param cap the initial capacity
     */
    public ExpandableArray(int cap)
    {
        data = new Object[cap];
    }

    /**
     * Retrieves the current size of the array.
     * @return the current size of the array
     */
    public synchronized int size()
    {
        return size;
    }

    /**
     * Get the value stored at position <code>i</code> of the array.
     * @param i the index
     * @return the value at the given index
     * @throws ArrayIndexOutOfBoundsException if the given index is
     *         smaller than 0 or greater than the current size.
     */
    public synchronized Object get(int i) throws ArrayIndexOutOfBoundsException
    {
        if (i < 0 || i >= size)
        {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        return data[i];
    }

    /**
     * Add the object <code>x</code> to the end of the array.  The array will be
     * expanded if the current array is full.
     * @param x the object to add
     */
    public synchronized void add(Object x)
    {
        if (size == data.length) // too small
        {
            Object[] od = data;
            //data = new Object[3 * (size + 1)/2];
            data = new Object[size + 2];
            System.arraycopy(od, 0, data, 0, od.length);
        }
        data[size++] = x;
    }

    /**
     * Removes that last object in the array.
     *
     * @throws NullPointerException
     */
    public synchronized void removeLast() throws NullPointerException
    {
        if (size == 0)
        {
            throw new NullPointerException("Expandable Array Empty");
        }
        data[--size] = null;
    }

}

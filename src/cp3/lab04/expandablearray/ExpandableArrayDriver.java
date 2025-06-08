package cp3.lab04.expandablearray;

import java.lang.reflect.Array;

/**
 *
 */
public class ExpandableArrayDriver
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException
    {
        // Initialize ExpandableArray with an initial capacity of 1
        ExpandableArray ea = new ExpandableArray(1);
        ArrayManip am = new ArrayManip(ea);
        ArrayManip am2 = new ArrayManip(ea);
        ArrayManip am3 = new ArrayManip(ea);
        ArrayManip am4 = new ArrayManip(ea);
        ArrayManip am5 = new ArrayManip(ea);
        ArrayManip am6 = new ArrayManip(ea);
        ArrayManip am7 = new ArrayManip(ea);
        ArrayManip am8 = new ArrayManip(ea);
        ArrayManip am9 = new ArrayManip(ea);
        ArrayManip am10 = new ArrayManip(ea);


        System.out.println("Starting am...");
        //am.start(); // Start the first thread

        System.out.println("Starting am2...");
        am2.start(); // Start the second thread

        System.out.println("Starting am3...");
        am3.start(); // Start the third thread

        System.out.println("Starting am4...");
        am4.start(); // Start the fourth thread

        System.out.println("Starting am5...");
        am5.start(); // Start the fifth thread
        System.out.println("Starting am6...");
        am6.start(); // Start the sixth thread
        System.out.println("Starting am7...");
        am7.start(); // Start the seventh thread
        System.out.println("Starting am8...");
        am8.start(); // Start the eighth thread
        System.out.println("Starting am9...");
        am9.start(); // Start the ninth thread
        System.out.println("Starting am10...");
        am10.start(); // Start the tenth thread

        // Now, wait for both threads to complete their execution.
        // They are running concurrently up to this point.
    //     System.out.println("Waiting for am to finish...");
    //     // am.join();
    //     System.out.println("am finished.");

    //     System.out.println("Waiting for am2 to finish...");
    //     // am2.join();
    //     System.out.println("am2 finished.");


    // System.out.println("Waiting for am3 to finish...");
    //     // am3.join();
    //     System.out.println("am3 finished.");

    //         System.out.println("Waiting for am2 to finish...");
    //     // am2.join();
    //     System.out.println("am2 finished.");

    //     System.out.println("Waiting for am4 to finish...");
    //     // am4.join();
    //     System.out.println("am4 finished.");


    // System.out.println("Waiting for am5 to finish...");
    //     // am5.join();
    //     System.out.println("am5 finished.");

        System.out.println("Size now: " + ea.size());

        // Add 1000 elements to the ExpandableArray from the main thread
        System.out.println("Main thread adding 1000 elements...");
        for (int i = 0; i < 1000; i++)
        {
            ea.add(new Integer(i));
        }

        // Print the final size of the ExpandableArray
        System.out.println("final size: " + ea.size());

        // Print the elements of the ExpandableArray
       
        for (int i = 0; i < ea.size(); i++)
        {
            System.out.print(ea.get(i) + " ");
            
        }
        System.out.println();
        System.out.println("--------------------"); 

}

}

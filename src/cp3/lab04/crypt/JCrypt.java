package cp3.lab04.crypt;

import java.io.File;
import java.util.ArrayList; 
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;



/**
 * @author A daring CP3 student!
 */


 public class JCrypt {

    
// Single-threaded processing  without synchronisation
    static class SingleFileThread extends Thread {
        private JCryptUtil.Options opts;
        private int index;
        
        public SingleFileThread(JCryptUtil.Options opts, int index) {
            this.opts = opts;
            this.index = index;
        }
        
        @Override
        public void run() {
            try {
                process(opts, index);
            } catch (JCryptUtil.Problem e) {
                System.err.println("ERROR in thread: " + e.getMessage());
            }
        }
    }
// For multiple threads without synchronisation
    static class MultiThread extends Thread {
        private JCryptUtil.Options opts;
        private AtomicInteger fileCounter;
        private int totalFiles;
        
        public MultiThread(JCryptUtil.Options opts, AtomicInteger fileCounter, int totalFiles) {
            this.opts = opts;
            this.fileCounter = fileCounter;
            this.totalFiles = totalFiles;
        }
        
        @Override
        public void run() {
            int currentFileIndex;
            while ((currentFileIndex = fileCounter.getAndIncrement()) < totalFiles) {
                try {
                    process(opts, currentFileIndex);
                } catch (JCryptUtil.Problem e) {
                    System.err.println("ERROR in thread: " + e.getMessage());
                }
            }
        }
    }

    
// For CountDownLatch 
    static class SingleFileThreadWithLatch extends Thread {
        private JCryptUtil.Options opts;
        private int index;
        private CountDownLatch latch;
        
        public SingleFileThreadWithLatch(JCryptUtil.Options opts, int index, CountDownLatch latch) {
            this.opts = opts;
            this.index = index;
            this.latch = latch;
        }
        
        @Override
        public void run() {
            try {
                process(opts, index);
            } catch (JCryptUtil.Problem e) {
                System.err.println("ERROR in thread: " + e.getMessage());
            } finally {
                latch.countDown(); // Signal completion
            }
        }
    }

    // For CountDownLatch with multiple threads
    static class MultiThreadWithLatch extends Thread {
        private JCryptUtil.Options opts;
        private AtomicInteger fileCounter;
        private int totalFiles;
        private CountDownLatch latch;
        
        public MultiThreadWithLatch(JCryptUtil.Options opts, AtomicInteger fileCounter, int totalFiles, CountDownLatch latch) {
            this.opts = opts;
            this.fileCounter = fileCounter;
            this.totalFiles = totalFiles;
            this.latch = latch;
        }
        
        @Override
        public void run() {
            try {
                int currentFileIndex;
                while ((currentFileIndex = fileCounter.getAndIncrement()) < totalFiles) {
                    try {
                        process(opts, currentFileIndex);
                    } catch (JCryptUtil.Problem e) {
                        System.err.println("ERROR in thread: " + e.getMessage());
                    }
                }
            } finally {
                latch.countDown(); // Signal completion
            }
        }
    }
    

    
// For CyclicBarrier
    // Note: CyclicBarrier requires all threads to reach the barrier before proceeding
    static class SingleFileThreadWithBarrier extends Thread {
        private JCryptUtil.Options opts;
        private int index;
        private CyclicBarrier barrier;
        
        public SingleFileThreadWithBarrier(JCryptUtil.Options opts, int index, CyclicBarrier barrier) {
            this.opts = opts;
            this.index = index;
            this.barrier = barrier;
        }
        
        @Override
        public void run() {
            try {
                process(opts, index);
                barrier.await(); // Wait for all threads to complete
            } catch (JCryptUtil.Problem e) {
                System.err.println("ERROR in thread: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Barrier error: " + e.getMessage());
            }
        }
    }
    

    // For CyclicBarrier with multiple threads
    static class MultiThreadWithBarrier extends Thread {
        private JCryptUtil.Options opts;
        private AtomicInteger fileCounter;
        private int totalFiles;
        private CyclicBarrier barrier;
        
        public MultiThreadWithBarrier(JCryptUtil.Options opts, AtomicInteger fileCounter, int totalFiles, CyclicBarrier barrier) {
            this.opts = opts;
            this.fileCounter = fileCounter;
            this.totalFiles = totalFiles;
            this.barrier = barrier;
        }
        
        @Override
        public void run() {
            try {
                int currentFileIndex;
                while ((currentFileIndex = fileCounter.getAndIncrement()) < totalFiles) {
                    try {
                        process(opts, currentFileIndex);
                    } catch (JCryptUtil.Problem e) {
                        System.err.println("ERROR in thread: " + e.getMessage());
                    }                }
                barrier.await(); // Wait for all threads to complete
            } catch (Exception e) {
                System.err.println("Barrier error: " + e.getMessage());
            }
        }
    }
    
    // Alternative using ReentrantLock instead of AtomicInteger
    static class MultiThreadWithLock extends Thread {
        private JCryptUtil.Options options; 
        private int[] fileIndexCounter; 
        private int totalFilesToProcess;
        private ReentrantLock fileAccessLock; 
        
        public MultiThreadWithLock(JCryptUtil.Options opts, int[] counter, int totalFiles, ReentrantLock lock) {
            this.options = opts;
            this.fileIndexCounter = counter;
            this.totalFilesToProcess = totalFiles;
            this.fileAccessLock = lock;
        }
        
        @Override
        public void run() {
            int currentFileIndex; 
            while (true) {
                fileAccessLock.lock(); // lock to ensure thread-safe access to fileIndexCounter
                try {
                    if (fileIndexCounter[0] >= totalFilesToProcess) {
                        break; // No more files to process
                    }
                    currentFileIndex = fileIndexCounter[0]++;
                } finally {
                    fileAccessLock.unlock(); // Always release lock when done
                }
                
                try {
                    process(options, currentFileIndex);
                } catch (JCryptUtil.Problem e) {
                    System.err.println("ERROR in thread: " + e.getMessage());
                }
            }
        }
    }




    /**
     * The main method.
     * Note: you will have to modify this.
     */

    public static void main(String[] args) {

        JCryptUtil.Options opts = JCryptUtil.parseOptions(args);

        long starttime = System.nanoTime();    
        


                ////==============||||==============\\\\
                // CCheckpoint L4.3: Process files sequentially, comment out below try{} block ** \\
                ///-----------------------------------\\\
             
            
        // try {
        //     for (int i = 0; i < opts.filenames.length; i++) {
        //         process(opts, i);
        //     }
        //     System.out.println("Time taken (Serial): " + (System.nanoTime()-starttime)/1000000000.0 + "s");
        // } catch (JCryptUtil.Problem e) {
        //     System.err.println("ERROR: " + e.getMessage());
        //     System.exit(2);
        // }



                ////==============||||==============\\\\
                // Checkpoint L4.4: Parallel processing options -t <num_threads>
                ///-----------------------------------\\\

          try {
            if (opts.threads > 0) {

                //************************************************/
                // *** SYNCHRONISATION - OPTIONS:
                // STEP 1: Choose one of the following synchronisation methods, default is Option A Thread.join(): 
                //************************************************/
                int numberOfThreads = Math.min(opts.threads, opts.filenames.length); 

        
                System.out.println("Using " + opts.threads + " threads for " + opts.filenames.length + " files"); 
                
                AtomicInteger fileCounter = new AtomicInteger(0); // For default MultiThread or Latch/Barrier with AtomicInteger
                List<Thread> multiThreads = new ArrayList<>();
                
       
                // Option B: CountDownLatch (uncomment to use instead of join())
                 //CountDownLatch latch = new CountDownLatch(numberOfThreads); // numberOfThreads would be the count of threads actually created

                // Option C: CyclicBarrier (uncomment to experiment - requires modification in MultiThreadWithBarrier)
                  //CyclicBarrier barrier = new CyclicBarrier(Math.min(opts.threads, opts.filenames.length) + 1);
                  // Option D: ReentrantLock with shared counter 
                 ReentrantLock counterLock = new ReentrantLock(); 
                 int[] sharedFileCounter = {0}; // Shared counter for ReentrantLock approach


                // Option F: Thread Pool (ACTIVE) - Choose ONE approach below:                
                ExecutorService threadPool = Executors.newFixedThreadPool(numberOfThreads);
                
                // send individual file processings tasks to the pool
                for (int i = 0; i < opts.filenames.length; i++) {
                    final int fileIndex = i;
                    threadPool.submit(() ->  {
                        try {
                            process(opts, fileIndex);
                        } catch (JCryptUtil.Problem e) {
                            System.err.println("ERROR in thread: " + e.getMessage());
                        }
                    });
                }
                
                threadPool.shutdown();
                try {
                    threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Time taken (Thread Pool): " + (System.nanoTime()-starttime)/1000000000.0 + "s");

                //************************************************/
                //  Manual Thread Creation (COMMENTED OUT - Use Thread Pool instead)
                // STEP 2: Choose one of the following thread implementations:
                //************************************************/
                // for (int i = 0; i < numberOfThreads; i++) {

                // Option A: enable/disable for MultiThread using join() 
                    //Thread multiThread = new MultiThread(opts, fileCounter, opts.filenames.length);
                    
                // Option B: enable/disable for CountDownLatch (uncomment to use instead of join() default)
                    // Thread multiThread = new MultiThreadWithLatch(opts, fileCounter, opts.filenames.length, latch);
                    
                // Option C: enable/disable for CyclicBarrier
                    // Thread multiThread = new MultiThreadWithBarrier(opts, fileCounter, opts.filenames.length, barrier);
                    
                // Option D: enable/disable for ReentrantLock
                    // Thread multiThread = new MultiThreadWithLock(opts, sharedFileCounter, opts.filenames.length, counterLock);

                    // multiThreads.add(multiThread);
                    // multiThread.start();
                // }

                //*******************************************/
                // *** Manual Thread SYNCHRONISATION (COMMENTED OUT - Thread Pool handles this)
                // STEP 3: Uncomment the desired synchronisation method below:
                //************************************************/

                
                // Option A & D: enable for MultiThread using join() or ReentrantLock
                // for (Thread multiThread : multiThreads) {
                //     multiThread.join(); // This is also how we wait for ReentrantLock threads too, as they exit run() and join main
                //  }
                // System.out.println("Time taken: " + (System.nanoTime()-starttime)/1000000000.0 + "s");
                
                // Option B: CountDownLatch
                 //latch.await();
                //System.out.println("Time taken (latch): " + (System.nanoTime()-starttime)/1000000000.0 + "s"); 

                // Option C: CyclicBarrier 
                 //barrier.await();
                //System.out.println("Time taken (barrier):  " + (System.nanoTime()-starttime)/1000000000.0 + "s"); 

                System.out.println("Using " + opts.threads + " threads for " + opts.filenames.length + " files");

                }



                //!-----------------------------------------------------------------------------------------------------!


                ////==============||||==============\\\\
                // Checkpoint L4.3: one thread per file
                ///-----------------------------------\\\

            //     else {
            //     //threads list to hold all threads
            //     List<Thread> threads = new ArrayList<>();
                
            //     //************************************************/
            //     // *** SYNCHRONISATION - OPTIONS:
            //     // STEP 1: Choose one of the following synchronisation methods, default is Option A Thread.join(): 
            //     //************************************************/

            //     // Option B: enable/disable CountDownLatch (uncomment to use instead of join() default)
            //     // CountDownLatch latch = new CountDownLatch(opts.filenames.length);
                
            //     // Option C: enable/disable CyclicBarrier 
            //      CyclicBarrier barrier = new CyclicBarrier(opts.filenames.length + 1);
                
            //     // Option D: enable/disable ReentrantLock 
            //     // ReentrantLock counterLock = new ReentrantLock();



            //     //************************************************/
            //     // Create and start concurrent threads - OPTIONS:
            //     // STEP 2: Choose one of the following thread implementations:
            //     //************************************************/
                
            //     for (int i = 0; i < opts.filenames.length; i++) {

            //         // Option A: enable/disable for Regular using join() 
            //         //Thread t = new SingleFileThread(opts, i);
                    
            //         // Option B: enable/disable for CountDownLatch 
            //         //Thread t = new SingleFileThreadWithLatch(opts, i, latch);
                    
            //         // Option C: enable/disable For CyclicBarrier
            //         Thread t = new SingleFileThreadWithBarrier(opts, i, barrier);

            //         // Option D: enable/disable for ReentrantLock
            //         //Thread t = new MultiThreadWithLock(opts, sharedFileCounter, opts.filenames.length, counterLock);


            //         threads.add(t);
            //         t.start();
            //     }


            //     //*******************************************/
            //     // *** SYNCHRONISATION OPTIONS - Choose ONE approach:
            //     // STEP 3: Uncomment the desired synchronisation method below:
            //     //************************************************/


            //     // Option A: enable/disable t.join() (default)
            //    // for (Thread t : threads) {
            //      //   t.join(); // Wait for all threads to complete
            //   //  }
            //     //System.out.println("Time taken (with join): " + (System.nanoTime()-starttime)/1000000000.0 + "s");

            //     // Option B: enable/disable CountDownLatch
            //     // latch.await(); // await for all threads to finish
            //     // System.out.println("Time taken (with CountDownLatch): " + (System.nanoTime()-starttime)/1000000000.0 + "s");

            //     // Option C: CyclicBarrier enable/disable
            //     barrier.await();
            //     System.out.println("Time taken (with CyclicBarrier): " + (System.nanoTime()-starttime)/1000000000.0 + "s");

            // }
              } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(2);
        }
        // ---

    }

    /**
     * Encrypts or decrypts a file based on command-line options.
     * Note: you can modify this if you like.
     * @param opts JCrypt command-line options
     * @param index the index of the file in the command-line options (for processing multiple files)
     */
    public static void process(JCryptUtil.Options opts, int index) throws JCryptUtil.Problem {
        byte[] decryptedText = null;
        if (opts.decryptionPassword.length() > 0) { // option requests file to be decrypted
            System.out.println("Decrypting "+opts.filenames[index]);
            JCryptUtil.EncryptedData encryptedText = JCryptUtil.readEncryptedFile(opts.filenames[index]);
            decryptedText = JCryptUtil.decrypt(opts.decryptionPassword, encryptedText);
        } else if (opts.crack) { // option requests file to be cracked
            System.out.println("Cracking " + opts.filenames[index]);
            JCryptUtil.EncryptedData encryptedText = JCryptUtil.readEncryptedFile(opts.filenames[index]);
            decryptedText = crack(encryptedText);
        }
        if (opts.encryptionPassword.length() > 0) { // option requests file to be encrypted
            JCryptUtil.EncryptedData encryptedText;
            if (decryptedText == null) {
                System.out.println("Encrypting " + opts.filenames[index]);
                byte[] buf = JCryptUtil.readRawFile(opts.filenames[index]);
                encryptedText = JCryptUtil.encrypt(opts.encryptionPassword, buf);
            } else {
                System.out.println("Encrypting text");
                encryptedText = JCryptUtil.encrypt(opts.encryptionPassword, decryptedText);
            }
            if (opts.saveToFile) { // save encrypted data to file
                String outputPath = opts.outputDirectory != null ? 
                    opts.outputDirectory + File.separator + new File(opts.filenames[index]).getName() + ".encrypted" :
                    new File(opts.filenames[index]).getName() + ".encrypted";
                JCryptUtil.writeEncryptedFile(encryptedText, outputPath);
            } else { // print encrypted data to standard out
                System.out.println(new String(encryptedText.content));
            }
        } else if (decryptedText != null) {
            if (opts.saveToFile) { // save decrypted data to file
                String filename = (new File(opts.filenames[index])).getName();
                if (filename.substring(filename.length()-".encrypted".length()).equalsIgnoreCase(".encrypted")) {
                    filename = filename.substring(0, filename.length()-".encrypted".length());
                } else {
                    filename = filename + ".decrypted";
                }
                
                String outputFilePath;
                if (opts.outputDirectory != null) {
                    outputFilePath = opts.outputDirectory + File.separator + filename;
                } else {
                    String pathname = (new File(opts.filenames[index])).getParent();
                    if (pathname == null) {
                        outputFilePath = filename;
                    } else {
                        outputFilePath = pathname + File.separator + filename;
                    }
                }

                JCryptUtil.writeRawFile(decryptedText, outputFilePath);
            } else { // print decrypted data to standard out
                System.out.println(new String(decryptedText));
            }
        }
    }

    /**
     * Crack encrypted data without knowing the password.
     * Note: you are expected to implement this for Checkpoint 3.
     */
    public static byte[] crack(JCryptUtil.EncryptedData ciphertext) throws JCryptUtil.Problem {
        throw new JCryptUtil.Problem("Not implemented");
    }

    public static long getFileSize(String filename) {
        File file = new File(filename);
        return file.length();
    }

}

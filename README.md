# Multithreaded File Encryption/Decryption Tool

A Java-based GUI application for encrypting and decrypting files using multiple threading implementations. This tool provides a modern interface to process files with different threading strategies and monitor performance metrics in real-time.

## Features

- Modern GUI interface with real-time performance metrics
- Multiple threading implementations:
  - Single Thread Per File
  - Multiple Threads with Atomic Counter
  - Thread Pool with CountDownLatch
  - Thread Pool with CyclicBarrier
  - Thread Pool with Lock
- Real-time progress monitoring
- Performance metrics display:
  - Processing speed (MB/s)
  - Total data processed
  - Active thread count
  - Processing time
- Support for multiple file selection
- Custom output directory selection
- Password-based encryption/decryption

## Requirements

- Java 8 or higher
- Maven (for building)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/gammaploid/multithreaded-files-encryptor-decryptor.git
cd multithreaded-files-encryptor-decryptor
```

2. Build the project:
```bash
mvn clean package
```

## Usage

1. Run the application:
```bash
java -jar target/multithreaded-encryptor-1.0.jar
```

2. Using the GUI:
   - Select the threading implementation from the dropdown
   - Choose between Encrypt or Decrypt mode
   - Set the number of threads (defaults to available CPU cores)
   - Enter the encryption/decryption password
   - Select the output directory
   - Click "Select Files" to choose files for processing
   - Click "Start Processing" to begin

3. Monitor progress:
   - Watch the progress bar for overall completion
   - Check the metrics panel for real-time statistics
   - View the log area for detailed processing information

## Threading Implementations

1. **Single Thread Per File**: Creates a separate thread for each file
2. **Multiple Threads with Atomic Counter**: Uses an atomic counter to distribute work among threads
3. **Thread Pool with CountDownLatch**: Uses a thread pool with CountDownLatch for synchronization
4. **Thread Pool with CyclicBarrier**: Uses a thread pool with CyclicBarrier for synchronization
5. **Thread Pool with Lock**: Uses a thread pool with ReentrantLock for synchronization

## Performance Tips

- For best performance, set the thread count to match your CPU core count
- Larger files benefit more from parallel processing
- The "Thread Pool with Lock" implementation generally provides the best performance for most use cases

## License

This project is for educational purposes only.  
Starter files were provided by instructors at Flinders University and Oracle.
Do not redistribute or use for commercial purposes.

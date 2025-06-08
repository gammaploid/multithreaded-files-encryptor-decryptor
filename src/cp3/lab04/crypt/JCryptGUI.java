package cp3.lab04.crypt;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class JCryptGUI extends JFrame {
    private JComboBox<String> implementationCombo;
    private JComboBox<String> modeCombo;
    private JSpinner threadCountSpinner;
    private JTextField passwordField;
    private JTextField outputDirField;
    private JTextArea logArea;
    private JProgressBar progressBar;
    private JButton selectFilesButton;
    private JButton selectOutputDirButton;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel metricsLabel;
    private JLabel threadInfoLabel;
    private JPanel metricsPanel;
    
    private List<File> selectedFiles = new ArrayList<>();
    private File outputDirectory = new File(System.getProperty("user.dir"));
    private boolean isProcessing = false;
    private boolean shouldStop = false;
    private long totalBytesProcessed = 0;
    private long lastUpdateTime;
    private AtomicLong bytesProcessed = new AtomicLong(0);
    private AtomicInteger activeThreads = new AtomicInteger(0);
    private ExecutorService executorService;
    
    public JCryptGUI() {
        setTitle("File Encryption/Decryption Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        
        // Set modern styling
        setModernStyling();
    }
    
    private void setModernStyling() {
        // Set modern colors
        Color backgroundColor = new Color(245, 245, 245);
        Color accentColor = new Color(70, 130, 180);
        Color textColor = new Color(51, 51, 51);
        
        // Apply colors to components
        getContentPane().setBackground(backgroundColor);
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(textColor);
        statusLabel.setForeground(textColor);
        timeLabel.setForeground(textColor);
        metricsLabel.setForeground(textColor);
        threadInfoLabel.setForeground(textColor);
        
        // Style buttons
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(accentColor);
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setOpaque(true);
            }
        }
        
        // Style progress bar
        progressBar.setForeground(accentColor);
        progressBar.setBackground(new Color(230, 230, 230));
        
        // Style combo boxes and spinners
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JComboBox || comp instanceof JSpinner) {
                comp.setBackground(Color.WHITE);
                comp.setForeground(textColor);
            }
        }
    }
    
    private void initializeComponents() {
        // Implementation selection with descriptions
        String[] implementations = {
            "Single Thread Per File",
            "Multiple Threads with Atomic Counter",
            "Thread Pool with CountDownLatch",
            "Thread Pool with CyclicBarrier",
            "Thread Pool with Lock"
        };
        implementationCombo = new JComboBox<>(implementations);
        
        // Mode selection
        String[] modes = {"Encrypt", "Decrypt"};
        modeCombo = new JComboBox<>(modes);
        
        // Thread count spinner with better bounds
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            Runtime.getRuntime().availableProcessors(), // Default to available processors
            1, // Minimum
            Runtime.getRuntime().availableProcessors() * 2, // Maximum
            1 // Step
        );
        threadCountSpinner = new JSpinner(spinnerModel);
        
        // Password field with show/hide toggle
        passwordField = new JPasswordField(20);
        
        // Output directory field
        outputDirField = new JTextField(20);
        outputDirField.setEditable(false);
        outputDirField.setText(outputDirectory.getAbsolutePath());
        
        // Log area with better font
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        
        // Progress bar with better appearance
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Buttons with icons
        selectFilesButton = new JButton("Select Files");
        selectOutputDirButton = new JButton("Select Output Directory");
        startButton = new JButton("Start Processing");
        stopButton = new JButton("Stop Processing");
        stopButton.setEnabled(false);
        
        // Labels with better formatting
        statusLabel = new JLabel("Ready");
        timeLabel = new JLabel("Time: 0.0s");
        metricsLabel = new JLabel("Speed: 0 MB/s | Processed: 0 MB");
        threadInfoLabel = new JLabel("Active Threads: 0");
        
        // Metrics panel
        metricsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        metricsPanel.add(timeLabel);
        metricsPanel.add(metricsLabel);
        metricsPanel.add(threadInfoLabel);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel for controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add components to top panel
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Implementation:"), gbc);
        gbc.gridx = 1;
        topPanel.add(implementationCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Mode:"), gbc);
        gbc.gridx = 1;
        topPanel.add(modeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Threads:"), gbc);
        gbc.gridx = 1;
        topPanel.add(threadCountSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        topPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        topPanel.add(new JLabel("Output Directory:"), gbc);
        gbc.gridx = 1;
        topPanel.add(outputDirField, gbc);
        gbc.gridx = 2;
        topPanel.add(selectOutputDirButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        topPanel.add(selectFilesButton, gbc);
        
        // Center panel for log and progress
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        centerPanel.add(progressBar, BorderLayout.SOUTH);
        
        // Bottom panel for metrics and controls
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(metricsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add padding
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void setupEventHandlers() {
        selectFilesButton.addActionListener(e -> selectFiles());
        selectOutputDirButton.addActionListener(e -> selectOutputDirectory());
        startButton.addActionListener(e -> startProcessing());
        stopButton.addActionListener(e -> stopProcessing());
        
        // Add tooltips
        implementationCombo.setToolTipText("Select the threading implementation to use");
        modeCombo.setToolTipText("Choose between encryption and decryption");
        threadCountSpinner.setToolTipText("Number of threads to use (recommended: number of CPU cores)");
        passwordField.setToolTipText("Enter the encryption/decryption password");
        outputDirField.setToolTipText("Directory where processed files will be saved");
    }
    
    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFiles.clear();
            for (File file : fileChooser.getSelectedFiles()) {
                selectedFiles.add(file);
            }
            logArea.append("Selected " + selectedFiles.size() + " files\n");
            updateStatus("Ready to process " + selectedFiles.size() + " files");
        }
    }
    
    private void selectOutputDirectory() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = dirChooser.getSelectedFile();
            outputDirField.setText(outputDirectory.getAbsolutePath());
        }
    }
    
    private void startProcessing() {
        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select files first!");
            return;
        }
        
        if (passwordField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password!");
            return;
        }
        
        // Reset state
        isProcessing = true;
        shouldStop = false;
        totalBytesProcessed = 0;
        bytesProcessed.set(0);
        activeThreads.set(0);
        lastUpdateTime = System.nanoTime();
        progressBar.setValue(0);
        logArea.setText(""); // Clear log
        
        // Update UI state
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        selectFilesButton.setEnabled(false);
        selectOutputDirButton.setEnabled(false);
        
        // Create options
        JCryptUtil.Options opts = new JCryptUtil.Options();
        if (modeCombo.getSelectedItem().equals("Encrypt")) {
            opts.encryptionPassword = passwordField.getText();
        } else {
            opts.decryptionPassword = passwordField.getText();
        }
        opts.saveToFile = true;
        opts.threads = (Integer) threadCountSpinner.getValue();
        opts.outputDirectory = outputDirectory.getAbsolutePath();
        opts.filenames = selectedFiles.stream()
            .map(File::getAbsolutePath)
            .toArray(String[]::new);
        
        // Start processing in a separate thread
        new Thread(() -> {
            try {
                long startTime = System.nanoTime();
                
                switch (implementationCombo.getSelectedIndex()) {
                    case 0: // Single Thread Per File
                        processWithSingleThreadPerFile(opts);
                        break;
                    case 1: // Multiple Threads with Atomic Counter
                        processWithAtomicCounter(opts);
                        break;
                    case 2: // Thread Pool with CountDownLatch
                        processWithThreadPoolAndLatch(opts);
                        break;
                    case 3: // Thread Pool with CyclicBarrier
                        processWithThreadPoolAndBarrier(opts);
                        break;
                    case 4: // Thread Pool with Lock
                        processWithThreadPoolAndLock(opts);
                        break;
                }
                
                if (!shouldStop) {
                    double totalTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                    updateStatus("Processing completed in " + String.format("%.2f", totalTime) + " seconds");
                }
            } catch (Exception e) {
                updateStatus("Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                isProcessing = false;
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    selectFilesButton.setEnabled(true);
                    selectOutputDirButton.setEnabled(true);
                });
            }
        }).start();
        
        // Start progress update thread
        new Thread(() -> {
            while (isProcessing && !shouldStop) {
                try {
                    Thread.sleep(100);
                    updateProgress();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    private void stopProcessing() {
        shouldStop = true;
        if (executorService != null) {
            executorService.shutdownNow();
        }
        updateStatus("Stopping...");
    }
    
    private void updateProgress() {
        long currentBytes = bytesProcessed.get();
        long currentTime = System.nanoTime();
        double timeDiff = (currentTime - lastUpdateTime) / 1_000_000_000.0;
        
        if (timeDiff >= 0.1) { // Update every 100ms
            double speed = (currentBytes - totalBytesProcessed) / timeDiff / 1_000_000.0; // MB/s
            totalBytesProcessed = currentBytes;
            lastUpdateTime = currentTime;
            
            int progress = (int)((currentBytes * 100.0) / getTotalFileSize());
            
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(progress);
                metricsLabel.setText(String.format("Speed: %.2f MB/s | Processed: %.2f MB", 
                    speed, currentBytes / 1_000_000.0));
                threadInfoLabel.setText("Active Threads: " + activeThreads.get());
            });
        }
    }
    
    private long getTotalFileSize() {
        return selectedFiles.stream()
            .mapToLong(File::length)
            .sum();
    }
    
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            logArea.append(message + "\n");
        });
    }
    
    private void processWithSingleThreadPerFile(JCryptUtil.Options opts) throws Exception {
        List<Thread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(opts.filenames.length);
        
        for (int i = 0; i < opts.filenames.length && !shouldStop; i++) {
            final int fileIndex = i;
            Thread thread = new Thread(() -> {
                try {
                    activeThreads.incrementAndGet();
                    JCrypt.process(opts, fileIndex);
                    bytesProcessed.addAndGet(new File(opts.filenames[fileIndex]).length());
                } catch (Exception e) {
                    updateStatus("Error processing file " + opts.filenames[fileIndex] + ": " + e.getMessage());
                } finally {
                    activeThreads.decrementAndGet();
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        latch.await();
    }
    
    private void processWithAtomicCounter(JCryptUtil.Options opts) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        int numThreads = opts.threads;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads && !shouldStop; i++) {
            Thread thread = new Thread(() -> {
                try {
                    activeThreads.incrementAndGet();
                    int fileIndex;
                    while (!shouldStop && (fileIndex = counter.getAndIncrement()) < opts.filenames.length) {
                        JCrypt.process(opts, fileIndex);
                        bytesProcessed.addAndGet(new File(opts.filenames[fileIndex]).length());
                    }
                } catch (Exception e) {
                    updateStatus("Error in thread: " + e.getMessage());
                } finally {
                    activeThreads.decrementAndGet();
                    latch.countDown();
                }
            });
            thread.start();
        }
        
        latch.await();
    }
    
    private void processWithThreadPoolAndLatch(JCryptUtil.Options opts) throws Exception {
        executorService = Executors.newFixedThreadPool(opts.threads);
        CountDownLatch latch = new CountDownLatch(opts.filenames.length);
        
        for (int i = 0; i < opts.filenames.length && !shouldStop; i++) {
            final int fileIndex = i;
            executorService.submit(() -> {
                try {
                    activeThreads.incrementAndGet();
                    JCrypt.process(opts, fileIndex);
                    bytesProcessed.addAndGet(new File(opts.filenames[fileIndex]).length());
                } catch (Exception e) {
                    updateStatus("Error processing file " + opts.filenames[fileIndex] + ": " + e.getMessage());
                } finally {
                    activeThreads.decrementAndGet();
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executorService.shutdown();
    }
    
    private void processWithThreadPoolAndBarrier(JCryptUtil.Options opts) throws Exception {
        executorService = Executors.newFixedThreadPool(opts.threads);
        CyclicBarrier barrier = new CyclicBarrier(opts.filenames.length + 1);
        
        for (int i = 0; i < opts.filenames.length && !shouldStop; i++) {
            final int fileIndex = i;
            executorService.submit(() -> {
                try {
                    activeThreads.incrementAndGet();
                    JCrypt.process(opts, fileIndex);
                    bytesProcessed.addAndGet(new File(opts.filenames[fileIndex]).length());
                    barrier.await();
                } catch (Exception e) {
                    updateStatus("Error processing file " + opts.filenames[fileIndex] + ": " + e.getMessage());
                } finally {
                    activeThreads.decrementAndGet();
                }
            });
        }
        
        barrier.await();
        executorService.shutdown();
    }
    
    private void processWithThreadPoolAndLock(JCryptUtil.Options opts) throws Exception {
        executorService = Executors.newFixedThreadPool(opts.threads);
        ReentrantLock lock = new ReentrantLock();
        int[] counter = {0};
        
        for (int i = 0; i < opts.threads && !shouldStop; i++) {
            executorService.submit(() -> {
                while (!shouldStop) {
                    int fileIndex;
                    lock.lock();
                    try {
                        if (counter[0] >= opts.filenames.length) {
                            break;
                        }
                        fileIndex = counter[0]++;
                    } finally {
                        lock.unlock();
                    }
                    
                    try {
                        activeThreads.incrementAndGet();
                        JCrypt.process(opts, fileIndex);
                        bytesProcessed.addAndGet(new File(opts.filenames[fileIndex]).length());
                    } catch (Exception e) {
                        updateStatus("Error processing file " + opts.filenames[fileIndex] + ": " + e.getMessage());
                    } finally {
                        activeThreads.decrementAndGet();
                    }
                }
            });
        }
        
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JCryptGUI gui = new JCryptGUI();
            gui.setVisible(true);
        });
    }
} 
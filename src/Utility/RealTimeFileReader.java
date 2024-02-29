package Utility;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RealTimeFileReader {
    public static void start() {
        JFrame frame = new JFrame("RealTime OS-Debug");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setBackground(Color.BLACK);
        frame.setSize(400, 300);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        Thread readerThread = initThread(textArea);

        readerThread.start();

        frame.setVisible(true);
    }

    private static Thread initThread(JTextArea textArea) {
        String filename = "OSDebug.txt";

        Thread readerThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line;
                while (true) {
                    if ((line = reader.readLine()) != null) {
                        // Update the JTextArea with the new line
                        String finalLine = line;
                        SwingUtilities.invokeLater(() -> textArea.append(finalLine + "\n"));
                    } else {
                        Thread.sleep(100);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        return readerThread;
    }
}

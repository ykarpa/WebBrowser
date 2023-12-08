package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleWebBrowser browser = new SimpleWebBrowser();
                browser.loadBookmarksFromFile(); // Load bookmarks when the program starts
            }
        });
    }
}

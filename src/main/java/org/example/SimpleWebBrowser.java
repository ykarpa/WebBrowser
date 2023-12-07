package org.example;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SimpleWebBrowser extends JFrame {
    private JEditorPane editorPane;
    private JTextField urlField;
    private List<URL> history;
    private int historyIndex;
    private JMenu bookmarksMenu;

    public SimpleWebBrowser() {
        setTitle("Web Browser");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        history = new ArrayList<>();
        historyIndex = -1;

        createMenuBar();
        createToolBar();
        createMainPanel();
        createStatusBar();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePage();
            }
        });
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);

        bookmarksMenu = new JMenu("Bookmarks");
        JMenuItem addBookmarkItem = new JMenuItem("Add Bookmark");
        addBookmarkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBookmark();
            }
        });
        bookmarksMenu.add(addBookmarkItem);
        menuBar.add(bookmarksMenu);

        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        toolBar.add(backButton);

        JButton forwardButton = new JButton("Forward");
        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goForward();
            }
        });
        toolBar.add(forwardButton);

        urlField = new JTextField();
        urlField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputURL = urlField.getText();
                if (!inputURL.startsWith("http://") && !inputURL.startsWith("https://")) {
                    inputURL = "https://" + inputURL;
                }
                loadURL(inputURL);
            }
        });
        toolBar.add(urlField);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputURL = urlField.getText();
                if (!inputURL.startsWith("http://") && !inputURL.startsWith("https://")) {
                    inputURL = "https://" + inputURL;
                }
                loadURL(inputURL);
            }
        });
        toolBar.add(goButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private void createMainPanel() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    loadURL(e.getURL().toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createStatusBar() {
        JLabel statusBar = new JLabel("Ready");
        add(statusBar, BorderLayout.SOUTH);
    }

    private void savePage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Page");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.println(editorPane.getText());
                JOptionPane.showMessageDialog(this, "Page saved successfully", "Save Page", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving page", "Save Page", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void goBack() {
        if (historyIndex > 0) {
            historyIndex--;
            loadURL(history.get(historyIndex).toString());
        }
    }

    private void goForward() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            loadURL(history.get(historyIndex).toString());
        }
    }

    private void loadURL(String url) {
        try {
            editorPane.setPage(new URL(url));
            urlField.setText(url);
            if (historyIndex == -1 || !history.get(historyIndex).toString().equals(url)) {
                history.add(new URL(url));
                historyIndex++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading page", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBookmark() {
        String inputURL = JOptionPane.showInputDialog(this, "Enter URL for the bookmark:", "Bookmark URL Input", JOptionPane.PLAIN_MESSAGE);
        if (inputURL != null && !inputURL.isEmpty()) {
            try {
                if (!inputURL.startsWith("http://") && !inputURL.startsWith("https://")) {
                    inputURL = "https://" + inputURL;
                }

                String url = inputURL;

                // Check if the bookmark already exists
                if (!bookmarkExists(url)) {
                    URL bookmarkURL = new URL(url);
                    JMenuItem bookmarkItem = new JMenuItem(url);
                    bookmarkItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadURL(url);
                        }
                    });
                    bookmarksMenu.add(bookmarkItem);

                    // Save bookmarks to file after adding a new bookmark
                    saveBookmarksToFile();

                    JOptionPane.showMessageDialog(this, "Bookmark added successfully", "Add Bookmark", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Bookmark already exists", "Add Bookmark", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid URL", "Add Bookmark", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean bookmarkExists(String url) {
        for (int i = 0; i < bookmarksMenu.getItemCount(); i++) {
            JMenuItem item = bookmarksMenu.getItem(i);
            if (item != null) {
                String existingBookmark = item.getText().trim();
                if (existingBookmark.equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }


    private void saveBookmarksToFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("bookmarks.txt"));
             PrintWriter writer = new PrintWriter(new FileWriter("bookmarks.txt", true))) {

            // Read existing bookmarks from the file
            List<String> existingBookmarks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                existingBookmarks.add(line.trim());
            }

            // Check if the new bookmark already exists
            for (int i = 0; i < bookmarksMenu.getItemCount(); i++) {
                JMenuItem item = bookmarksMenu.getItem(i);
                if (item != null) {
                    String newBookmark = item.getText().trim();
                    if (!existingBookmarks.contains(newBookmark)) {
                        writer.println(newBookmark);
                    }
                }
            }

            //JOptionPane.showMessageDialog(this, "Bookmarks saved successfully", "Save Bookmarks", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving bookmarks", "Save Bookmarks", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookmarksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("bookmarks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String url = line.trim();
                addBookmarkFromFile(url);
            }
        } catch (IOException e) {
            // Ignore if the file doesn't exist or there is an error reading it
        }
    }

    private void addBookmarkFromFile(String url) {
        try {
            String finalURL = url;
            URL bookmarkURL = new URL(finalURL);
            JButton bookmarkButton = new JButton(finalURL);
            bookmarkButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadURL(finalURL);
                }
            });
            bookmarkButton.setToolTipText(finalURL);

// Replace "img/trash_icon.png" with the actual path to your icon
            ImageIcon trashIcon = new ImageIcon("img/trash_icon.png");
            trashIcon.setImage(trashIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

            JButton removeButton = new JButton(trashIcon);
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeBookmark(finalURL);
                }
            });

            JPanel bookmarkPanel = new JPanel(new BorderLayout());
            bookmarkPanel.add(bookmarkButton, BorderLayout.CENTER);
            bookmarkPanel.add(removeButton, BorderLayout.EAST);

            bookmarksMenu.add(bookmarkPanel);
        } catch (Exception e) {
            // Ignore if the URL is invalid
        }
    }

    private void removeBookmark(String url) {
        Component[] components = bookmarksMenu.getMenuComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel bookmarkPanel = (JPanel) component;
                JButton bookmarkButton = (JButton) bookmarkPanel.getComponent(0);

                if (bookmarkButton != null && bookmarkButton.getText().equals(url)) {
                    bookmarksMenu.remove(bookmarkPanel);
                    saveBookmarksToFile(); // Save bookmarks after removal
                    bookmarksMenu.revalidate();
                    bookmarksMenu.repaint();

                    // Remove the bookmark from the file
                    removeBookmarkFromFile(url);

                    return;
                }
            }
        }
    }

    private void removeBookmarkFromFile(String url) {
        try (BufferedReader reader = new BufferedReader(new FileReader("bookmarks.txt"));
             PrintWriter writer = new PrintWriter(new FileWriter("bookmarks_temp.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.equals(url)) {
                    writer.println(trimmedLine);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error removing bookmark from file", "Remove Bookmark", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Rename the temporary file to the original file
        File tempFile = new File("bookmarks_temp.txt");
        File originalFile = new File("bookmarks.txt");

        if (originalFile.delete()) {
            if (!tempFile.renameTo(originalFile)) {
                JOptionPane.showMessageDialog(this, "Error renaming temporary file", "Remove Bookmark", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting original file", "Remove Bookmark", JOptionPane.ERROR_MESSAGE);
        }
    }



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
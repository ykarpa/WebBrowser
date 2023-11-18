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
            if (historyIndex == -1 || !history.get(historyIndex).toString().equals(url)) {
                history.add(new URL(url));
                historyIndex++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading page", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBookmark() {
        String inputURL = JOptionPane.showInputDialog(this, "Enter URL for the bookmark:");
        if (inputURL != null && !inputURL.isEmpty()) {
            try {
                if (!inputURL.startsWith("http://") && !inputURL.startsWith("https://")) {
                    inputURL = "https://" + inputURL;
                }

                String url = inputURL;
                URL bookmarkURL = new URL(url);
                JMenuItem bookmarkItem = new JMenuItem(url);
                bookmarkItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadURL(url);
                    }
                });
                bookmarksMenu.add(bookmarkItem);
                JOptionPane.showMessageDialog(this, "Bookmark added successfully", "Add Bookmark", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid URL", "Add Bookmark", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimpleWebBrowser();
            }
        });
    }
}
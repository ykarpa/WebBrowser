package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private final SimpleWebBrowser parent;

    public SettingsDialog(SimpleWebBrowser parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Settings");
        setSize(300, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JRadioButton lightThemeButton = new JRadioButton("Light Theme");
        JRadioButton darkThemeButton = new JRadioButton("Dark Theme");
        ButtonGroup group = new ButtonGroup();
        group.add(lightThemeButton);
        group.add(darkThemeButton);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySettings(lightThemeButton.isSelected() ? "light" : "dark");
            }
        });

        setLayout(new GridLayout(3, 1));
        add(lightThemeButton);
        add(darkThemeButton);
        add(applyButton);

        setVisible(true);
    }

    private void applySettings(String theme) {
        parent.setTheme(theme);
        dispose();
    }
}
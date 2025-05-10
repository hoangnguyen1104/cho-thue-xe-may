package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private DataController dataController;

    public LoginFrame(DataController dataController) {
        this.dataController = dataController;
        setTitle("Đăng nhập hệ thống cho thuê xe máy");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Email field
        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);
        
        // Password field
        panel.add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        // Login button
        panel.add(new JLabel());
        loginButton = new JButton("Đăng nhập");
        panel.add(loginButton);
        
        add(panel);

        // Add key listeners for Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        loginButton.addKeyListener(enterKeyListener);

        // Add action listener for login button
        loginButton.addActionListener(e -> performLogin());

        // Set focus traversal
        emailField.setFocusTraversalKeysEnabled(true);
        passwordField.setFocusTraversalKeysEnabled(true);
        loginButton.setFocusTraversalKeysEnabled(true);
    }

    private void performLogin() {
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());
        User user = checkLogin(email, pass);
        if (user != null) {
            MainFrame mainFrame = new MainFrame(user, dataController);
            mainFrame.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(LoginFrame.this, 
                "Sai email hoặc mật khẩu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private User checkLogin(String email, String pass) {
        for (User u : dataController.getUsers()) {
            if (u.getUsername().equals(email) && u.getPassword().equals(pass)) {
                return u;
            }
        }
        return null;
    }
} 
package com.urbankart.ui;

import com.urbankart.dao.UserDAO;
import com.urbankart.dao.UserDAOImpl;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("UrbanKart - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("UrbanKart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.BLACK);
        registerButton.setBackground(new Color(102, 102, 102));
        registerButton.setForeground(Color.BLACK);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setupEventListeners();
    }

    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistration();
            }
        });

        // Enter key listener for login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.authenticateUser(username, password)) {
            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                openMainDashboard(user);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistration() {
        new RegisterFrame().setVisible(true);
        dispose();
    }

    private void openMainDashboard(User user) {
        // This would open the main dashboard based on user role
        new MainDashboardFrame(user).setVisible(true);
        dispose();
    }
}
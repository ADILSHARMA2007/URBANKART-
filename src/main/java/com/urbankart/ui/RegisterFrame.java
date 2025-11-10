package com.urbankart.ui;

import com.urbankart.dao.UserDAO;
import com.urbankart.dao.UserDAOImpl;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<User.UserRole> roleComboBox;
    private JButton registerButton;
    private JButton backButton;
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("UrbanKart - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        formPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(User.UserRole.values());
        // Remove ADMIN role from registration - only BUYER and SELLER
        DefaultComboBoxModel<User.UserRole> model = new DefaultComboBoxModel<>();
        model.addElement(User.UserRole.BUYER);
        model.addElement(User.UserRole.SELLER);
        roleComboBox.setModel(model);
        formPanel.add(roleComboBox);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(102, 102, 102));
        backButton.setForeground(Color.BLACK);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setupEventListeners();
    }

    private void setupEventListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void register() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        User.UserRole role = (User.UserRole) roleComboBox.getSelectedItem();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username or email already exists
        if (userDAO.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            JOptionPane.showMessageDialog(this,
                    "Email already registered",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In production, this should be hashed
        newUser.setRole(role);
        newUser.setActive(true);

        if (userDAO.createUser(newUser)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Please try again.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
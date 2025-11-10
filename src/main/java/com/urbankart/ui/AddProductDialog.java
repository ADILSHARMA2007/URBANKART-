package com.urbankart.ui;

import com.urbankart.dao.ProductDAO;
import com.urbankart.model.Product;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class AddProductDialog extends JDialog {
    private boolean productAdded = false;
    private ProductDAO productDAO;
    private User currentUser;

    private JTextField nameField;
    private JTextArea descArea;
    private JTextField categoryField;
    private JTextField imageField;
    private JComboBox<Product.ListingType> typeCombo;
    private JTextField priceField;
    private JTextField startPriceField;

    public AddProductDialog(Frame parent, User user, ProductDAO productDAO) {
        super(parent, "Add New Product", true);
        this.currentUser = user;
        this.productDAO = productDAO;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Add New Product", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        formPanel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descArea = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll);

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Image Path:"));
        imageField = new JTextField();
        formPanel.add(imageField);

        formPanel.add(new JLabel("Listing Type:"));
        typeCombo = new JComboBox<>(Product.ListingType.values());
        typeCombo.addActionListener(e -> updatePriceFields());
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Price/Rental Fee:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Starting Price (Auction):"));
        startPriceField = new JTextField();
        startPriceField.setEnabled(false); // Initially disabled
        formPanel.add(startPriceField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Product");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> saveProduct());

        cancelButton.setBackground(new Color(102, 102, 102));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void updatePriceFields() {
        Product.ListingType selectedType = (Product.ListingType) typeCombo.getSelectedItem();

        if (selectedType == Product.ListingType.BID) {
            priceField.setEnabled(false);
            priceField.setText("");
            startPriceField.setEnabled(true);
        } else {
            priceField.setEnabled(true);
            startPriceField.setEnabled(false);
            startPriceField.setText("");
        }
    }

    private void saveProduct() {
        // Validation
        String name = nameField.getText().trim();
        String description = descArea.getText().trim();
        String category = categoryField.getText().trim();
        String imagePath = imageField.getText().trim();
        Product.ListingType listType = (Product.ListingType) typeCombo.getSelectedItem();

        if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (Name, Description, Category).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Product product = new Product();
            product.setSellerId(currentUser.getId());
            product.setName(name);
            product.setDescription(description);
            product.setCategory(category);
            product.setImagePath(imagePath.isEmpty() ? "/images/default.jpg" : imagePath);
            product.setListType(listType);
            product.setAvailabilityStatus(Product.AvailabilityStatus.AVAILABLE);
            product.setActive(true);

            if (listType == Product.ListingType.BUY || listType == Product.ListingType.LEND) {
                if (priceField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Price is required for Buy/Rental products.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                product.setPrice(price);

                if (listType == Product.ListingType.BUY) {
                    product.setStartingPrice(price); // For buy items, starting price = price
                }
            }

            if (listType == Product.ListingType.BID) {
                if (startPriceField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Starting price is required for Auction products.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal startingPrice = new BigDecimal(startPriceField.getText().trim());
                product.setStartingPrice(startingPrice);
                product.setCurrentBid(startingPrice);
                // Set auction end time (7 days from now)
                product.setAuctionEndTime(java.time.LocalDateTime.now().plusDays(7));
            }

            if (productDAO.createProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Product added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                productAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add product. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for prices.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isProductAdded() {
        return productAdded;
    }
}
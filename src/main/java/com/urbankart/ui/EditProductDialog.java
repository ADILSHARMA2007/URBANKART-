package com.urbankart.ui;

import com.urbankart.dao.ProductDAO;
import com.urbankart.model.Product;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class EditProductDialog extends JDialog {
    private boolean productUpdated = false;
    private ProductDAO productDAO;
    private Product product;

    private JTextField nameField;
    private JTextArea descArea;
    private JTextField categoryField;
    private JTextField imageField;
    private JComboBox<Product.ListingType> typeCombo;
    private JTextField priceField;
    private JTextField startPriceField;
    private JComboBox<Product.AvailabilityStatus> statusCombo;

    public EditProductDialog(Frame parent, Product product, ProductDAO productDAO) {
        super(parent, "Edit Product - " + product.getName(), true);
        this.product = product;
        this.productDAO = productDAO;
        initializeUI();
        populateFields();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Edit Product", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
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
        typeCombo.setEnabled(false); // Cannot change listing type after creation
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Price/Rental Fee:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Starting Price (Auction):"));
        startPriceField = new JTextField();
        startPriceField.setEnabled(false); // Only for auctions
        formPanel.add(startPriceField);

        formPanel.add(new JLabel("Availability Status:"));
        statusCombo = new JComboBox<>(Product.AvailabilityStatus.values());
        formPanel.add(statusCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveChanges());

        cancelButton.setBackground(new Color(102, 102, 102));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void populateFields() {
        nameField.setText(product.getName());
        descArea.setText(product.getDescription());
        categoryField.setText(product.getCategory());
        imageField.setText(product.getImagePath() != null ? product.getImagePath() : "");
        typeCombo.setSelectedItem(product.getListType());
        statusCombo.setSelectedItem(product.getAvailabilityStatus());

        if (product.getPrice() != null) {
            priceField.setText(product.getPrice().toString());
        }

        if (product.getStartingPrice() != null) {
            startPriceField.setText(product.getStartingPrice().toString());
        }

        // Enable/disable fields based on listing type
        if (product.getListType() == Product.ListingType.BID) {
            priceField.setEnabled(false);
            startPriceField.setEnabled(true);
        } else {
            priceField.setEnabled(true);
            startPriceField.setEnabled(false);
        }
    }

    private void saveChanges() {
        // Validation
        String name = nameField.getText().trim();
        String description = descArea.getText().trim();
        String category = categoryField.getText().trim();
        String imagePath = imageField.getText().trim();

        if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (Name, Description, Category).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            product.setName(name);
            product.setDescription(description);
            product.setCategory(category);
            product.setImagePath(imagePath.isEmpty() ? "/images/default.jpg" : imagePath);
            product.setAvailabilityStatus((Product.AvailabilityStatus) statusCombo.getSelectedItem());

            if (product.getListType() == Product.ListingType.BUY || product.getListType() == Product.ListingType.LEND) {
                if (priceField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Price is required for Buy/Rental products.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                product.setPrice(price);
            }

            if (product.getListType() == Product.ListingType.BID && !startPriceField.getText().trim().isEmpty()) {
                BigDecimal startingPrice = new BigDecimal(startPriceField.getText().trim());
                product.setStartingPrice(startingPrice);
                // Don't update current bid as it might have higher bids
            }

            if (productDAO.updateProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Product updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                productUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update product. Please try again.",
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
                    "Error updating product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isProductUpdated() {
        return productUpdated;
    }
}
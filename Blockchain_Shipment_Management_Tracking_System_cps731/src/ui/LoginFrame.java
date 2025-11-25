package ui;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LoginFrame
 * - Dark "blockchain" themed login screen.
 * - Seeds a few demo users in memory.
 * - On successful login opens MainUI with the logged-in User.
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    private final List<User> demoUsers = new ArrayList<>();

    // Shared “in-memory backend” so MainUI doesn’t recreate everything
    private final BlockchainNetwork blockchainNetwork;
    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorage offChainStorage;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;
    private final ShipmentLifecycleController lifecycleController;
    private final ShipmentComplianceController complianceController;

    public LoginFrame() {
        // ---------- Backend wiring (single instance for whole app) ----------
        blockchainNetwork = new BlockchainNetwork();
        blockchainGateway = new BlockchainNetworkGateway(blockchainNetwork);

        offChainStorage = new OffChainStorage();
        offChainAdapter = new OffChainStorageAdapter(offChainStorage);

        smartContract = new SmartContract();
        lifecycleController = new ShipmentLifecycleController(
                blockchainGateway,
                offChainAdapter,
                smartContract);
        complianceController = new ShipmentComplianceController(
                blockchainNetwork,
                blockchainGateway,
                smartContract);

        seedDemoUsers();

        // ---------- Look & feel ----------
        setTitle("Blockchain Shipment Tracking – Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        Color bg = new Color(5, 10, 25);
        Color card = new Color(12, 18, 40);
        Color accent = new Color(59, 130, 246); // blockchain blue
        Color text = new Color(230, 235, 255);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(bg);
        add(root);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(card);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(420, 260));

        JLabel title = new JLabel("Blockchain Console Login");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Enter your username and password to access the ledger.");
        subtitle.setForeground(new Color(160, 170, 210));
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        JPanel titleWrap = new JPanel();
        titleWrap.setBackground(card);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(title);
        titleWrap.add(Box.createVerticalStrut(4));
        titleWrap.add(subtitle);

        // ---------- Form ----------
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(text);
        usernameField = new JTextField(18);
        styleTextField(usernameField, card, text);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(text);
        passwordField = new JPasswordField(18);
        styleTextField(passwordField, card, text);

        JButton loginButton = createPrimaryButton("Log in", accent);
        JButton exitButton = createSecondaryButton("Exit");

        loginButton.addActionListener(e -> attemptLogin());
        exitButton.addActionListener(e -> {
            // Close entire application
            dispose();
            System.exit(0);
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(exitButton);
        buttonRow.add(loginButton);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(252, 165, 165));

        cardPanel.add(titleWrap);
        cardPanel.add(Box.createVerticalStrut(18));
        cardPanel.add(userLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(passLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(18));
        cardPanel.add(buttonRow);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(statusLabel);

        root.add(cardPanel);

        setVisible(true);
    }

    private void styleTextField(JTextField field, Color bg, Color fg) {
        field.setBackground(new Color(8, 14, 32));
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 64, 175)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private JButton createPrimaryButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(209, 213, 219));
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------- Demo users ----------

    private void seedDemoUsers() {
        demoUsers.clear();

        Shipper shipper = new Shipper(
                1,
                "shipper",
                "shipper",
                "shipper@example.com",
                "Global Shipper Inc.", // companyName
                "123 Dock Street", // address
                "Primary Shipper" // shipperRole
        );
        demoUsers.add(shipper);

        // Buyer
        Buyer buyer = new Buyer();
        buyer.setUserID(2);
        buyer.setUsername("buyer");
        buyer.setPassword("buyer");
        buyer.setEmail("buyer@example.com");
        buyer.setRole("BUYER");
        buyer.setRetailID("2001");
        buyer.setBuyerRole("Retail Customer");
        demoUsers.add(buyer);

        // Logistics provider
        LogisticsProvider lp = new LogisticsProvider();
        lp.setUserID(3);
        lp.setUsername("logistics");
        lp.setPassword("logistics");
        lp.setEmail("logistics@example.com");
        lp.setRole("LOGISTICS_PROVIDER");
        lp.setVehicleID(501);
        lp.setRouteInfo("Default Route");
        demoUsers.add(lp);

        // Warehouse
        Warehouse wh = new Warehouse();
        wh.setUserID(4);
        wh.setUsername("warehouse");
        wh.setPassword("warehouse");
        wh.setEmail("warehouse@example.com");
        wh.setRole("WAREHOUSE");
        wh.setWarehouseID(10);
        wh.setCapacity(5000);
        wh.setAddress("Main Warehouse");
        demoUsers.add(wh);

        // Customs
        CustomsOfficer co = new CustomsOfficer();
        co.setUserID(5);
        co.setUsername("customs");
        co.setPassword("customs");
        co.setEmail("customs@example.com");
        co.setRole("CUSTOMS_OFFICER");
        co.setOfficerID(77);
        co.setAgencyName("Border Agency");
        demoUsers.add(co);

        // Auditor
        Auditor auditor = new Auditor();
        auditor.setUserID(6);
        auditor.setUsername("auditor");
        auditor.setPassword("auditor");
        auditor.setEmail("auditor@example.com");
        auditor.setRole("AUDITOR");
        auditor.setAuditID(900);
        auditor.setOrganization("Independent Audit Co.");
        demoUsers.add(auditor);

        // Administrator
        Administrator admin = new Administrator();
        admin.setUserID(7);
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setEmail("admin@example.com");
        admin.setRole("ADMIN");
        demoUsers.add(admin);
    }

    // ---------- Login logic ----------

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        User found = null;
        for (User u : demoUsers) {
            if (u.getUsername().equalsIgnoreCase(username)
                    && u.getPassword().equals(password)) {
                found = u;
                break;
            }
        }

        if (found == null) {
            statusLabel.setText("Invalid credentials. Try 'shipper/shipper', 'buyer/buyer', etc.");
            return;
        }

        // Success – open the main console UI and close login
        User loggedIn = found;
        SwingUtilities.invokeLater(() -> {
            new MainUI(
                    loggedIn,
                    blockchainNetwork,
                    blockchainGateway,
                    offChainStorage,
                    offChainAdapter,
                    smartContract,
                    lifecycleController,
                    complianceController);
        });
        dispose();
    }
}

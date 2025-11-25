package ui;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MainUI
 * - Single console for all roles.
 * - Left nav with rounded “block” buttons.
 * - Center card area for each action.
 * - Bottom activity log with terminal look.
 */
public class MainUI extends JFrame {

    private final User currentUser;

    // Shared backend objects (passed from LoginFrame)
    private final BlockchainNetwork blockchainNetwork;
    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorage offChainStorage;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;
    private final ShipmentLifecycleController lifecycleController;
    private final ShipmentComplianceController complianceController;

    // Swing components
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JTextArea activityLogArea = new JTextArea();

    // Create Shipment fields
    private JTextField csOriginField;
    private JTextField csDestinationField;
    private JTextField csDescriptionField;

    // Upload Document fields
    private JTextField udShipmentIdField;
    private JTextField udDocNameField;
    private JTextArea udContentArea;

    // Update Status fields
    private JTextField usShipmentIdField;
    private JTextField usNewStatusField;

    // Query / Audit fields
    private JTextField qaShipmentIdField;
    private JTextArea qaResultArea;

    // Confirm Delivery Fields
    private JTextField cdShipmentIdField;

    private final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public MainUI(
            User user,
            BlockchainNetwork blockchainNetwork,
            BlockchainNetworkGateway blockchainGateway,
            OffChainStorage offChainStorage,
            OffChainStorageAdapter offChainAdapter,
            SmartContract smartContract,
            ShipmentLifecycleController lifecycleController,
            ShipmentComplianceController complianceController) {

        this.currentUser = user;
        this.blockchainNetwork = blockchainNetwork;
        this.blockchainGateway = blockchainGateway;
        this.offChainStorage = offChainStorage;
        this.offChainAdapter = offChainAdapter;
        this.smartContract = smartContract;
        this.lifecycleController = lifecycleController;
        this.complianceController = complianceController;

        initFrame();
        buildLayout();
        setVisible(true);
    }

    // ---------- Frame & base layout ----------

    private void initFrame() {
        setTitle("Blockchain Shipment Tracking – " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 650));
    }

    private void buildLayout() {
        // “Blockchain” dark-neon palette
        Color bg = new Color(4, 9, 24);
        Color navBg = new Color(8, 14, 34);
        Color navButtonBg = new Color(15, 23, 42);
        Color cardBg = new Color(10, 20, 48);
        Color accent = new Color(56, 189, 248); // cyan
        Color accentBorder = new Color(37, 99, 235); // indigo border
        Color text = new Color(230, 235, 255);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        setContentPane(root);

        // ---------- Left navigation ----------
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(navBg);
        nav.setBorder(new EmptyBorder(20, 18, 20, 18));
        nav.setPreferredSize(new Dimension(260, 0));

        JLabel title = new JLabel("Blockchain Console");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel loggedIn = new JLabel("Logged in as: " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        loggedIn.setForeground(new Color(148, 163, 184));
        loggedIn.setFont(loggedIn.getFont().deriveFont(12f));

        addRoleBasedMenu(nav, navButtonBg, accentBorder);

        JButton logout = new JButton("Log out");
        logout.setBackground(navButtonBg);
        logout.setForeground(text);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentBorder, 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        logout.addActionListener(e -> doLogout());
        nav.add(logout);

        root.add(nav, BorderLayout.WEST);

        // ---------- Center cards ----------
        cardPanel.setBackground(bg);
        cardPanel.setBorder(new EmptyBorder(24, 24, 8, 24));

        cardPanel.add(buildCreateShipmentCard(cardBg, text, accent, accentBorder), "CREATE");
        cardPanel.add(buildUploadDocumentCard(cardBg, text, accent, accentBorder), "UPLOAD");
        cardPanel.add(buildUpdateStatusCard(cardBg, text, accent, accentBorder), "STATUS");
        cardPanel.add(buildQueryAuditCard(cardBg, text, accent, accentBorder), "QUERY");
        cardPanel.add(buildConfirmDeliveryCard(cardBg, text, accent, accentBorder), "CONFIRM_DELIVERY");
        cardPanel.add(buildUploadDocumentCard(cardBg, text, accent, accentBorder), "DISPUTE");
        cardPanel.add(buildUpdateStatusCard(cardBg, text, accent, accentBorder), "VERIFY_DOCUMENT");
        cardPanel.add(buildQueryAuditCard(cardBg, text, accent, accentBorder), "CLEARANCE");
        cardPanel.add(buildCreateShipmentCard(cardBg, text, accent, accentBorder), "COMPLIANCE");
        cardPanel.add(buildUploadDocumentCard(cardBg, text, accent, accentBorder), "AUDIT");
        cardPanel.add(buildUpdateStatusCard(cardBg, text, accent, accentBorder), "FRAUD");
        cardPanel.add(buildQueryAuditCard(cardBg, text, accent, accentBorder), "MANAGE_USERS");
        cardPanel.add(buildCreateShipmentCard(cardBg, text, accent, accentBorder), "ASSIGN_ROLES");

        root.add(cardPanel, BorderLayout.CENTER);

        // ---------- Activity log ----------
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new EmptyBorder(4, 18, 8, 18));
        logPanel.setBackground(new Color(3, 7, 18));

        JLabel logLabel = new JLabel("Activity Log");
        logLabel.setForeground(new Color(148, 163, 184));
        logLabel.setFont(logLabel.getFont().deriveFont(12f));

        activityLogArea.setEditable(false);
        activityLogArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        activityLogArea.setBackground(Color.BLACK);
        activityLogArea.setForeground(new Color(45, 212, 191)); // teal terminal text
        activityLogArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        activityLogArea.setLineWrap(true);
        activityLogArea.setWrapStyleWord(true);

        JScrollPane logScroll = new JScrollPane(activityLogArea);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(31, 41, 55)));

        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(0, 130));

        root.add(logPanel, BorderLayout.SOUTH);

        showCard("CREATE");
    }

    private JButton createNavButton(String text, Color bg, Color borderColor,
            java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setForeground(new Color(226, 232, 255));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        btn.addActionListener(listener);
        return btn;
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    private void log(String message) {
        String ts = LocalDateTime.now().format(logTimeFormat);
        activityLogArea.append("[" + ts + "] " + message + "\n");
        activityLogArea.setCaretPosition(activityLogArea.getDocument().getLength());
    }

    // ---------- Create Shipment card ----------

    private JComponent buildCreateShipmentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Create Shipment", SwingConstants.CENTER);
        title.setForeground(text);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        csOriginField = createLabeledField(card, "Origin", text, borderColor);
        csDestinationField = createLabeledField(card, "Destination", text, borderColor);
        csDescriptionField = createLabeledField(card, "Description", text, borderColor);

        JButton createBtn = new JButton("Create Shipment");
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(accent);
        createBtn.setFocusPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        createBtn.addActionListener(e -> handleCreateShipment());

        card.add(Box.createVerticalStrut(20));
        card.add(createBtn);

        outer.add(card);
        return outer;
    }

    private JTextField createLabeledField(JPanel container, String label,
            Color textColor, Color borderColor) {
        JLabel l = new JLabel(label);
        l.setForeground(textColor);
        container.add(l);
        container.add(Box.createVerticalStrut(4));

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setBackground(new Color(8, 14, 32));
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(4, 8, 4, 8)));

        container.add(field);
        container.add(Box.createVerticalStrut(10));
        return field;
    }

    private void handleCreateShipment() {
        // Only shippers can create shipments
        if (!(currentUser instanceof Shipper)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only SHIPPER role can create shipments.",
                    "Not allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String origin = csOriginField.getText().trim();
        String destination = csDestinationField.getText().trim();
        String description = csDescriptionField.getText().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Origin and Destination are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipper shipper = (Shipper) currentUser;

        // Simple ID generator for the UI
        String shipmentId = "S" + System.currentTimeMillis();

        Shipment shipment = lifecycleController.createShipment(
                shipper,
                shipmentId,
                origin,
                destination,
                description);

        log("Shipment created: " + shipment.getShipmentId()
                + " by " + currentUser.getUsername());

        JOptionPane.showMessageDialog(
                this,
                "Shipment created with ID: " + shipment.getShipmentId(),
                "Created",
                JOptionPane.INFORMATION_MESSAGE);

        csOriginField.setText("");
        csDestinationField.setText("");
        csDescriptionField.setText("");
    }

    // ---------- Upload Document card ----------

    private JComponent buildUploadDocumentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 340));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Upload Document");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        udShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);
        udDocNameField = createLabeledField(card, "Document Name", text, borderColor);

        JLabel contentLabel = new JLabel("Content");
        contentLabel.setForeground(text);
        card.add(contentLabel);
        card.add(Box.createVerticalStrut(4));

        udContentArea = new JTextArea(5, 40);
        udContentArea.setLineWrap(true);
        udContentArea.setWrapStyleWord(true);
        udContentArea.setBackground(new Color(8, 14, 32));
        udContentArea.setForeground(text);
        udContentArea.setCaretColor(text);
        udContentArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane contentScroll = new JScrollPane(udContentArea);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        card.add(contentScroll);
        card.add(Box.createVerticalStrut(18));

        JButton uploadBtn = new JButton("Upload Document");
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setBackground(accent);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        uploadBtn.addActionListener(e -> handleUploadDocument());

        card.add(uploadBtn);
        outer.add(card);
        return outer;
    }

    private void handleUploadDocument() {
        String shipmentId = udShipmentIdField.getText().trim();
        String docName = udDocNameField.getText().trim();
        String content = udContentArea.getText();

        if (shipmentId.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID and Document Name are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Document doc = lifecycleController.uploadDocument(shipment, docName, content);
        offChainAdapter.connect();
        offChainAdapter.uploadFile(doc);

        log("Document '" + docName + "' uploaded for shipment " + shipmentId);
        JOptionPane.showMessageDialog(this,
                "Document uploaded and stored off-chain.",
                "Uploaded",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Update Status card ----------

    private JComponent buildUpdateStatusCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Update Shipment Status");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        usShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);
        usNewStatusField = createLabeledField(card,
                "New Status (e.g., IN_TRANSIT, DELIVERED)", text, borderColor);

        JButton updateBtn = new JButton("Update Status");
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setBackground(accent);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        updateBtn.addActionListener(e -> handleUpdateStatus());

        card.add(Box.createVerticalStrut(12));
        card.add(updateBtn);

        outer.add(card);
        return outer;
    }

    private void handleUpdateStatus() {
        String shipmentId = usShipmentIdField.getText().trim();
        String newStatus = usNewStatusField.getText().trim();

        if (shipmentId.isEmpty() || newStatus.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID and new status are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = lifecycleController.updateShipmentStatus(shipment, newStatus);
        log(result);
        JOptionPane.showMessageDialog(this, result, "Status updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Query / Audit card ----------

    private JComponent buildQueryAuditCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 340));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Query Shipment / Audit");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        qaShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        buttonRow.setOpaque(false);

        JButton queryBtn = new JButton("Query Shipment");
        queryBtn.setForeground(text);
        queryBtn.setBackground(new Color(15, 23, 42));
        queryBtn.setFocusPainted(false);
        queryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        queryBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 18, 8, 18)));

        JButton auditBtn = new JButton("Generate Audit Trail");
        auditBtn.setForeground(text);
        auditBtn.setBackground(new Color(15, 23, 42));
        auditBtn.setFocusPainted(false);
        auditBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        auditBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 18, 8, 18)));

        buttonRow.add(queryBtn);
        buttonRow.add(auditBtn);

        qaResultArea = new JTextArea(8, 50);
        qaResultArea.setEditable(false);
        qaResultArea.setLineWrap(true);
        qaResultArea.setWrapStyleWord(true);
        qaResultArea.setBackground(new Color(8, 14, 32));
        qaResultArea.setForeground(text);
        qaResultArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane resultScroll = new JScrollPane(qaResultArea);
        resultScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

        queryBtn.addActionListener(e -> handleQueryShipment());
        auditBtn.addActionListener(e -> handleGenerateAudit());

        card.add(buttonRow);
        card.add(Box.createVerticalStrut(12));
        card.add(resultScroll);

        outer.add(card);
        return outer;
    }

    private void handleQueryShipment() {
        String shipmentId = qaShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        String result = complianceController.queryShipmentStatus(shipment);
        qaResultArea.setText(result);
        log("Query shipment " + shipmentId + " → " + result);
    }

    private void handleGenerateAudit() {
        String shipmentId = qaShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        Report report = complianceController.generateAuditTrail(shipment);
        qaResultArea.setText(report.toString());

        log("Generated audit trail for shipment " + shipmentId);
    }

    // ---------- Confirm Delivery card ----------
    private JComponent buildConfirmDeliveryCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 340));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Confirm Delivery");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        cdShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JButton confirmBtn = new JButton("Confirm Delivery");
        confirmBtn.setForeground(text);
        confirmBtn.setBackground(new Color(15, 23, 42));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        confirmBtn.addActionListener(e -> handleConfirmDelivery());

        card.add(Box.createVerticalStrut(12));
        card.add(confirmBtn);

        outer.add(card);
        return outer;
    }

    private void handleConfirmDelivery() {
        
        // Permission check (only Buyer)
        if (!(currentUser instanceof Buyer)) {
            JOptionPane.showMessageDialog(this, "Only BUYER role can confirm deliveries.", "Not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = cdShipmentIdField.getText().trim();

        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call controller
        String result = lifecycleController.confirmDelivery(shipment);

        log(result);
        JOptionPane.showMessageDialog(this, result);
        cdShipmentIdField.setText("");
    }

    // Method to add main menu features/options based on the user role (CHANGE THIS)
    private void addRoleBasedMenu(JPanel nav, Color bg, Color border) {
        String role = currentUser.getRole();
        switch (role) {
            case "shipper":
                nav.add(createNavButton("Create Shipment", bg, border, e -> showCard("CREATE")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Upload Document", bg, border, e -> showCard("UPLOAD")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment / History", bg, border, e -> showCard("QUERY")));
                break;
            case "logistics provider":
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("QUERY")));
                break;
            case "warehouse":
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("QUERY")));
                break;
            case "buyer":
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Confirm Delivery", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Raise Dispute", bg, border, e -> showCard("UPLOAD"))); 
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Verify Document", bg, border, e -> showCard("QUERY")));
                break;
            case "customs officer":
                nav.add(createNavButton("Clearance Approval", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Review Documents", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("QUERY")));
                break;
            case "auditor":
                nav.add(createNavButton("Compliance Report", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Generate Audit Trail", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Verify Documents", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Fraud Detection", bg, border, e -> showCard("QUERY")));
                break;
            case "admin":
                nav.add(createNavButton("Manage Users", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Assign Roles", bg, border, e -> showCard("STATUS")));
                break;
            default:
                nav.add(new JLabel("Unknown role: " + role));
        }
    }

    // ---------- Logout ----------
    private void doLogout() {
        dispose();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

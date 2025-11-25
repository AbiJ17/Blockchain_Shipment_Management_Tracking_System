package controller;

import java.util.List;

import external.BlockchainNetwork;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.Document;
import model.Report;
import model.Shipment;
import model.SmartContract;

public class ShipmentComplianceController {

    private final BlockchainNetwork blockchainNetwork;
    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;

    public ShipmentComplianceController(BlockchainNetwork blockchainNetwork,
            BlockchainNetworkGateway blockchainGateway, 
            OffChainStorageAdapter offChainAdapter,
            SmartContract smartContract) {

        this.blockchainNetwork = blockchainNetwork;
        this.blockchainGateway = blockchainGateway;
        this.offChainAdapter = offChainAdapter; 
        this.smartContract = smartContract;
    }

    // In this simplified version we let the caller pass the Shipment
    // (e.g., retrieved from ShipmentLifecycleController).

    public String queryShipmentStatus(Shipment shipment) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        return "Shipment " + shipment.getShipmentId() +
                " status: " + shipment.getStatus();
    }

    public Report generateAuditTrail(Shipment shipment) {
        if (shipment == null) {
            return new Report("Audit Trail", "No shipment found.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Audit trail for shipment ").append(shipment.getShipmentId()).append("\n\n");

        List<model.Event> history = shipment.getHistory();
        for (model.Event e : history) {
            sb.append(e.getTimestamp())
                    .append("  -  ")
                    .append(e.getMessage())
                    .append("\n");
        }

        // Simulate querying blockchain
        blockchainGateway.queryLedger("AuditTrail#" + shipment.getShipmentId());

        return new Report("Audit Trail - Shipment " + shipment.getShipmentId(), sb.toString());
    }

    public boolean ensureLedgerIntegrity(Shipment shipment) {
        // For now we simply delegate to the smart contract stub
        return smartContract.verifyLedgerIntegrity(shipment);
    }

    public String logDispute(Shipment shipment, String description) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        if (description == null || description.isBlank()) {
            return "Dispute description cannot be empty.";
        }

        // Smart contract validation
        if (!smartContract.canRaiseDispute(shipment)) {
            return "Smart contract rejected dispute for shipment " + shipment.getShipmentId();
        }

        shipment.addHistoryEvent("Dispute raised: " + description);

        // Blockchain log
        blockchainGateway.connect();
        blockchainGateway.sendTransaction("DISPUTE#" + shipment.getShipmentId());

        return "Dispute filed for shipment " + shipment.getShipmentId();
    }

    public String verifyDocument(Shipment shipment, String documentName) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        if (documentName == null || documentName.isBlank()) {
            return "Document name is required.";
        }

        for (Document doc : shipment.getDocuments()) {
            if (doc.getName().equalsIgnoreCase(documentName)) {

                boolean ok = offChainAdapter.verifyIntegrity(doc);

                return ok
                    ? "Document '" + documentName + "' is VALID (hash verified)."
                    : "Document '" + documentName + "' FAILED verification (hash mismatch).";
            }
        }

        return "Document not found: " + documentName;
    }
}

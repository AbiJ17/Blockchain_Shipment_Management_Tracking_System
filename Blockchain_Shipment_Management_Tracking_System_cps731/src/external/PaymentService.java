package external;

import model.Shipment;
import model.Document; 
import java.util.Date; 

/**
 * Simulated external payment service.
 */
public class PaymentService {

    private int transactionID; 
    private float amount; 
    private String status; 

    // DEFAULT CONSTRUCTOR
    public PaymentService() {
        this.transactionID = -1;
        this.amount = 0.0f;
        this.status = "PENDING";
    }

    public PaymentService(int transactionID, float amount, String status) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.status = status;
    }

    // METHODS
    /**
     * Processes payment for the given shipment.
     * Returns true if "successful" (simulated).
     */
    public boolean processPayment(Shipment shipment) {
        System.out.println("[PaymentService] Processing payment for shipment "
                + shipment.getShipmentID());

        // Simulated payment logic
        if (amount > 0) {
            this.status = "SUCCESS";
            return true;
        }

        this.status = "FAILED";
        return false;
    }

    // Triggers an automatic payment (simulation).
    public void triggerAutoPayment() {
        System.out.println("[PaymentService] Triggering auto-payment...");
        this.status = "AUTO_TRIGGERED";
    }

    // Generates a simple receipt document.
    public Document generateReceipt() {

        String content = "Receipt for Transaction #" + transactionID
                + "\nAmount: $" + amount
                + "\nStatus: " + status;

        // Create the Document object
        Document receipt = new Document(
                transactionID,
                "receipt_" + transactionID + ".txt",
                null,   
                "receipts/receipt_" + transactionID + ".txt",
                content,                            
                new Date()
        );

        // Generate hash from content
        receipt.generateHash();

        // Return the completed receipt document
        return receipt;
    }

    // GETTERS AND SETTERS

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

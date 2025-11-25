package model;

public class Buyer extends User {

    private String retailID; // kept as String to match your LoginFrame usage
    private String buyerRole;

    public Buyer() {
        setRole("BUYER");
    }

    public Buyer(int userID,
            String username,
            String password,
            String email,
            String retailID,
            String buyerRole) {

        super(userID, username, password, "BUYER", email);
        this.retailID = retailID;
        this.buyerRole = buyerRole;
    }

    // ---- Domain behaviour ----

    public Shipment trackShipment(Shipment shipment) {
        // Caller usually already has Shipment; this method is just a stub.
        return shipment;
    }

    public void confirmDelivery(Shipment shipment) {
        if (shipment == null)
            return;
        shipment.setStatus("DELIVERED");
        shipment.addHistoryEvent("Delivery confirmed by buyer " + getUsername());
    }

    public void raiseDispute(Shipment shipment, String reason) {
        if (shipment == null)
            return;
        shipment.addHistoryEvent("Dispute raised by buyer: " + reason);
    }

    public boolean verifyDocument(Document document) {
        // Real verification handled by controllers / adapters.
        return document != null;
    }

    // ---- Getters / setters ----

    public String getRetailID() {
        return retailID;
    }

    public void setRetailID(String retailID) {
        this.retailID = retailID;
    }

    public String getBuyerRole() {
        return buyerRole;
    }

    public void setBuyerRole(String buyerRole) {
        this.buyerRole = buyerRole;
    }
}

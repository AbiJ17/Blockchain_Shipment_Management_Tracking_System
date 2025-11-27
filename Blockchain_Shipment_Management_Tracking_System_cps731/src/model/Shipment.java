package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Shipment {

    private String shipmentID;
    private String origin;
    private String destination;
    private String description;
    private String status; // no ShipmentStatus enum anymore
    private Date dispatchDate;
    private Date deliveryDate;

    private final List<Document> documents = new ArrayList<>();
    private final List<Event> history = new ArrayList<>();

    // ✅ this is the constructor Shipper.createShipment(...) expects:
    public Shipment(String shipmentID, String origin, String destination, String description) {
        this.shipmentID = shipmentID;
        this.origin = origin;
        this.destination = destination;
        this.description = description;
        this.status = "CREATED";
        this.dispatchDate = new Date(); // when created
        addHistoryEvent("Shipment created: " + description);
    }

    // --- core getters/setters ---

    public String getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(String shipmentID) { 
        this.shipmentID = shipmentID; 
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public List<Event> getHistory() {
        return history;
    }

    // ✅ PUBLIC so Shipper can call it
    public void addHistoryEvent(String message) {
        history.add(new Event(new Date(), message));
    }

    // small helpers if you ever want them
    public void addDocument(Document document) {
        if (document != null) {
            documents.add(document);
            addHistoryEvent("Document added: " + document.getName());
        }
    }

    public void confirmDelivery() {
        this.status = "DELIVERED";
        this.deliveryDate = new Date();
        addHistoryEvent("Shipment marked as DELIVERED");
    }
}

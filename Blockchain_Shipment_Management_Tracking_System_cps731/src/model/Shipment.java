package model;

import java.util.Date;
import java.util.List;

import org.w3c.dom.events.Event;

public class Shipment {

    public int shipmentID; 
    public String origin;
    public String destination;
    public String status;
    public Date dispatchDate;
    public Date deliveryDate;
    public String assignedLogistics; 
    public String assignedWarehouse; 
    public List<Document> documents;
    public SmartContract smartContract;
    
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }

    public List<Event> getShipmentHistory() {
        
        return null;
    }
    
    public void confirmDelivery() {
        
    }

    public int getShipmentID() {
        return shipmentID;
    }
    public void setShipmentID(int shipmentID) {
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
    public String getAssignedLogistics() {
        return assignedLogistics;
    }
    public void setAssignedLogistics(String assignedLogistics) {
        this.assignedLogistics = assignedLogistics;
    }
    public String getAssignedWarehouse() {
        return assignedWarehouse;
    }
    public void setAssignedWarehouse(String assignedWarehouse) {
        this.assignedWarehouse = assignedWarehouse;
    }
    public List<Document> getDocuments() {
        return documents;
    }
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    public SmartContract getSmartContract() {
        return smartContract;
    }
    public void setSmartContract(SmartContract smartContract) {
        this.smartContract = smartContract;
    }

}

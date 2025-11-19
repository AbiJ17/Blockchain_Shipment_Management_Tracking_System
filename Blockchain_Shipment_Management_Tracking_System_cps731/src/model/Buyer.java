package model;

public class Buyer extends User{

    public int retailID; 
    public String buyerRole; 

    // public Shipment trackShipment(id: int) {

    // }
    
    public void makePayment() {}

    public int getRetailID() {
        return retailID;
    }

    public void setRetailID(int retailID) {
        this.retailID = retailID;
    }

    public String getBuyerRole() {
        return buyerRole;
    }
    
    public void setBuyerRole(String buyerRole) {
        this.buyerRole = buyerRole;
    }
    
}

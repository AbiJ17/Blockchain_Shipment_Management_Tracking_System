package model;

public class Shipper extends User {

    public String companyName; 
    public String address;
    public String shipperRole; 

    public void createShipmentRequest() {}
    public void uploadDocuments() {}

    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShipperRole() {
        return shipperRole;
    }

    public void setShipperRole(String shipperRole) {
        this.shipperRole = shipperRole;
    }
    
    
}

package model;

import java.util.List;

public class SmartContract {

    public int contractID; 
    public String contractType; 
    public List<String> rules; 

    public boolean validateShipment(Shipment shipment) {
        return false;
    }

    public void enforceDeliveryConfirmation() { 

    }

    public void triggerInsuranceClaim() { 

    }

    public boolean enforceRolePermissions(User user) {
        return false;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
    
}

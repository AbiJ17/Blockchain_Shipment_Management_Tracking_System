package gateway;

import external.PaymentService;

public class PaymentServiceAdapter {
    
    public boolean connected;
    public int lastTransactionID;
    public PaymentService paymentService;

    public boolean connect() {
        return false;
    }

    public boolean triggerPayment(String policyID) { 
        return false;
    }

    public void recordTransaction(int txID) {
        
    }

    public void disconnect() {

    }
    
    
}

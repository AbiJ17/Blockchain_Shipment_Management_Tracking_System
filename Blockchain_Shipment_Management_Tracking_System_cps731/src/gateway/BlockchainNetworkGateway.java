package gateway;

import external.BlockchainNetwork;

public class BlockchainNetworkGateway {

    public boolean connected;
    public int lastTransactionID;
    public BlockchainNetwork blockchain; 


    public boolean connect() {
        return false;
    }

    public boolean sendTransaction(String data) {
        return false;
    }

    public String queryLedger(int txID) { 
        return null;
    }

    public boolean validateBlock(String hash) { 
        return false;
    }

    public void disconnect() {

    }
    
}

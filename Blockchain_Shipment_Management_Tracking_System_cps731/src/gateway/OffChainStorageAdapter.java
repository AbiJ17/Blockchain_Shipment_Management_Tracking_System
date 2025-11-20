package gateway;

import external.OffChainStorage;
import javax.swing.text.Document;

public class OffChainStorageAdapter {

    public boolean connected;
    public int lastTransactionID;
    public OffChainStorage offChainStorage;

    public boolean connect() {
        return false;
    }

    // public String uploadFile(Document document) { 
    //     return;
    // }

    // public Document retrieveFile(String hash) {
    //     return null;
    // }

    public boolean verifyIntegrity (Document document) {
        return false;
    }

    public void disconnect() {

    }
    
}

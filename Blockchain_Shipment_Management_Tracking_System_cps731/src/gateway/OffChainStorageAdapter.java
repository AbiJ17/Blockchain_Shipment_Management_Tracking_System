package gateway;

import external.OffChainStorage;
import model.Document;

/**
 * OffChainStorageAdapter
 * - Wraps OffChainStorage so the rest of the app talks to a clean API.
 * - Shows the Adapter / Indirection pattern for off-chain storage.
 */
public class OffChainStorageAdapter {

    private boolean connected;
    private int lastTransactionID;
    private final OffChainStorage offChainStorage;

    public OffChainStorageAdapter(OffChainStorage offChainStorage) {
        this.offChainStorage = offChainStorage;
    }

    /**
     * Connect to the underlying off-chain storage.
     * In this simple version we just check availability.
     */
    public boolean connect() {
        this.connected = offChainStorage.checkAvailability();
        return connected;
    }

    /**
     * Upload a document to off-chain storage.
     *
     * @param document Document to store
     * @return the document hash (used as key on-chain), or null if upload failed
     */
    public String uploadFile(Document document) {
        if (document == null) {
            return null;
        }

        if (!connected && !connect()) {
            // storage is not available
            return null;
        }

        // Make sure the document has a hash before storing
        if (document.getHashValue() == null || document.getHashValue().isEmpty()) {
            document.generateHash();
        }

        // Store in the underlying off-chain storage
        offChainStorage.storeFile(document);
        lastTransactionID++;

        // The hash value is the "key" we’ll later store on the blockchain
        return document.getHashValue();
    }

    /**
     * Retrieve a document by its hash.
     */
    public Document retrieveFile(String hash) {
        if (hash == null) {
            return null;
        }

        if (!connected && !connect()) {
            return null;
        }

        return offChainStorage.getFileByHash(hash);
    }

    /**
     * Verifies that the stored document has not been tampered with.
     * Uses Document.verifyHash() to compare content vs stored hash.
     */
    public boolean verifyIntegrity(Document document) {
        if (document == null || document.getHashValue() == null) {
            return false;
        }

        if (!connected && !connect()) {
            return false;
        }

        // Look up the stored version by its hash
        Document stored = offChainStorage.getFileByHash(document.getHashValue());
        if (stored == null) {
            return false;
        }

        // Rely on the Document's own hash verification
        return stored.verifyHash();
    }

    /**
     * "Disconnect" from storage – in our simple case we just flip the flag.
     */
    public void disconnect() {
        connected = false;
    }

    // ───────────── Getters ─────────────

    public boolean isConnected() {
        return connected;
    }

    public int getLastTransactionID() {
        return lastTransactionID;
    }
}

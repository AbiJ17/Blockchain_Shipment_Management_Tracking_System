package external;

import java.util.ArrayList;
import java.util.List;

import model.Document;

/**
 * Simple in-memory off-chain storage service.
 * Acts as the concrete service that OffChainStorageAdapter talks to.
 */
public class OffChainStorage {

    private boolean available = true;
    private final List<Document> documents = new ArrayList<>();

    public OffChainStorage() {
    }

    /** Simulate whether the storage is up. */
    public boolean checkAvailability() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /** Store / overwrite a document in off-chain storage. */
    public void storeFile(Document document) {
        if (document == null)
            return;

        // If a document with same hash already exists, replace it
        Document existing = getFileByHash(document.getHashValue());
        if (existing != null) {
            documents.remove(existing);
        }
        documents.add(document);
    }

    /** Retrieve a document by its hash value. */
    public Document getFileByHash(String hashValue) {
        if (hashValue == null)
            return null;
        for (Document d : documents) {
            if (hashValue.equals(d.getHashValue())) {
                return d;
            }
        }
        return null;
    }

    /** Optional helper: retrieve by name (some controllers / UIs may use this). */
    public Document getFileByName(String name) {
        if (name == null)
            return null;
        for (Document d : documents) {
            if (name.equalsIgnoreCase(d.getName())) {
                return d;
            }
        }
        return null;
    }

    /** Defensive copy of all docs – useful for debugging / audit. */
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documents);
    }
}

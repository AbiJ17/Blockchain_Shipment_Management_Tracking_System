package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Document {

    private int documentID;
    private String name;
    private String hashValue;
    private String filePath;
    private String content;
    private Date timestamp;

    public Document() {
    }

    // ---- basic getters / setters ----

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // ---- hash / integrity helpers ----

    /** Generate a SHA-256 hash from the current content field. */
    public void generateHash() {
        if (content == null) {
            this.hashValue = null;
            return;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(content.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            this.hashValue = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            this.hashValue = null;
        }
    }

    /** Recompute the hash from content and compare with stored hashValue. */
    public boolean verifyHash() {
        if (hashValue == null || content == null) {
            return false;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(content.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String current = sb.toString();
            return hashValue.equals(current);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}

package model;

import java.util.*;

public class Document {

    public int documentID;
    public String name;
    public String hashValue;
    public String filePath; 
    public Date timestamp;
    
    public String generateHash() {
        return "";
    }
    
    public boolean verifyHash() {
        return false;
    }

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
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    } 
    
}

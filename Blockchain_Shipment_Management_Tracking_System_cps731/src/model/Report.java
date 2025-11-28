package model;

import java.util.Date;

public class Report {

    private final String title;
    private final String body;
    private final Date generatedDate; 

    // CONSTRUCTOR
    public Report(String title, String body, Date generatedDate) {
        this.title = title;
        this.body = body;
        this.generatedDate = generatedDate;
    }
    
    // GETTERS
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getGeneratedDate() { 
        return generatedDate; 
    }

    @Override
    public String toString() {
        return title + "\n\n" + body;
    }
}

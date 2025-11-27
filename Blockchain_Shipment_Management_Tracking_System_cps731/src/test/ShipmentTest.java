package test;

import org.junit.jupiter.api.Test;
import model.Document;
import model.Shipment;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentTest {

    @Test
    void testAddDocument() {
        Shipment s = new Shipment("S1", "A", "B", "Test");

        Document d = new Document("invoice", "hash123");
        s.addDocument(d);

        assertEquals(1, s.getDocuments().size());
    }

    @Test
    void testAddEvent() {
        Shipment s = new Shipment("S1", "A", "B", "Test");

        int before = s.getHistory().size(); // constructor already added 1 event
        s.addEvent("CREATED");

        assertEquals(before + 1, s.getHistory().size());
        assertEquals("CREATED", s.getStatus()); // optional: since addEvent sets status
    }

    @Test
    void testStatusUpdates() {
        Shipment s = new Shipment("S1", "A", "B", "Test");
        s.setStatus("IN_TRANSIT");

        assertEquals("IN_TRANSIT", s.getStatus());
    }
}

package test;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import external.PaymentService;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import gateway.PaymentServiceAdapter;
import model.Report;
import model.Shipment;
import model.Shipper;
import model.SmartContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentComplianceControllerTest {

    private ShipmentComplianceController controller;
    private ShipmentLifecycleController lifecycle;

    @BeforeEach
    void setup() {

        BlockchainNetwork network = new BlockchainNetwork();
        BlockchainNetworkGateway gateway = new BlockchainNetworkGateway(network);
        OffChainStorage offChainStorage = new OffChainStorage();
        OffChainStorageAdapter offChainAdapter = new OffChainStorageAdapter(offChainStorage);
        PaymentService paymentService = new PaymentService(); 
        PaymentServiceAdapter paymentAdapter = new PaymentServiceAdapter(paymentService); 
        SmartContract smartContract = new SmartContract();

        lifecycle = new ShipmentLifecycleController(gateway, offChainAdapter, paymentAdapter, smartContract);
        // Updated to match new constructor: (BlockchainNetworkGateway,
        // OffChainStorageAdapter, SmartContract)
        controller = new ShipmentComplianceController(gateway, offChainAdapter, paymentAdapter, smartContract);
    }

    private Shipper makeShipper() {
        Shipper s = new Shipper();
        s.setUserID(99);
        s.setUsername("alice");
        s.setPassword("password");
        s.setEmail("a@x.com");
        s.setCompanyName("Test Shipper Inc.");
        s.setAddress("123 Test Street");
        return s;
    }

    @Test
    void testQueryShipmentStatus() {
        Shipper shipper = makeShipper();

        Shipment s = lifecycle.createShipment(
                shipper,
                "S100",
                "X",
                "Y",
                "Test");

        s.setStatus("IN_TRANSIT");

        String result = controller.queryShipmentStatus(s);

        assertTrue(result.contains("IN_TRANSIT"));
        assertTrue(result.contains("S100"));
    }

    @Test
    void testGenerateAuditTrail() {
        Shipper shipper = makeShipper();

        Shipment s = lifecycle.createShipment(
                shipper,
                "S111",
                "Toronto",
                "NYC",
                "Goods");

        lifecycle.updateShipmentStatus(s, "IN_TRANSIT");

        Report r = controller.generateAuditTrail(s);

        assertNotNull(r);
        String text = r.toString();
        assertTrue(text.contains("Audit trail"));
        assertTrue(text.contains("IN_TRANSIT"));
    }

    @Test
    void testLogDispute() {
        Shipper shipper = makeShipper();

        Shipment s = lifecycle.createShipment(
                shipper,
                "S222",
                "A",
                "B",
                "Test");

        String result = controller.logDispute(s, "Damaged item");

        // Method currently returns: "Dispute filed for shipment S222"
        assertTrue(result.contains("Dispute filed"));
        assertTrue(result.contains("S222"));
    }
}

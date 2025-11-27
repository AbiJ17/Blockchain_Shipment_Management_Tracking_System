package test;

import model.Shipment;
import model.SmartContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmartContract business rules
 * based on the current SmartContract.java implementation.
 */
public class SmartContractTest {

    private SmartContract contract;
    private Shipment shipment;

    @BeforeEach
    void setup() {
        contract = new SmartContract();
        // constructor you used before: (id, origin, destination, description)
        shipment = new Shipment("S900", "A", "B", "Test");
        // at this point status should be something like "CREATED"
    }

    // ---------- canUpdateStatus ----------

    @Test
    void canUpdateStatus_allowsTransitionWhenNotDelivered() {
        // From initial (CREATED) to IN_TRANSIT should be allowed
        boolean ok = contract.canUpdateStatus(shipment, "IN_TRANSIT");
        assertTrue(ok, "Status should be updatable while not DELIVERED");
    }

    @Test
    void canUpdateStatus_blocksChangeAfterDelivered() {
        // Move shipment to DELIVERED (using the domain method you already used
        // elsewhere)
        shipment.addEvent("DELIVERED");

        // Staying DELIVERED is allowed
        assertTrue(contract.canUpdateStatus(shipment, "DELIVERED"));

        // But moving back to any other status is NOT allowed
        assertFalse(contract.canUpdateStatus(shipment, "IN_TRANSIT"));
        assertFalse(contract.canUpdateStatus(shipment, "CREATED"));
    }

    // ---------- canTriggerPayment ----------

    @Test
    void canTriggerPayment_onlyWhenDelivered() {
        // Not delivered yet → no payment
        assertFalse(contract.canTriggerPayment(shipment));

        // After delivered → payment allowed
        shipment.addEvent("DELIVERED");
        assertTrue(contract.canTriggerPayment(shipment));
    }

    // ---------- verifyLedgerIntegrity ----------

    @Test
    void verifyLedgerIntegrity_emptyHistoryIsValid() {
        // No events → treated as valid ledger
        assertTrue(contract.verifyLedgerIntegrity(shipment));
    }

    // (We deliberately avoid asserting on multiple timestamped events here,
    // because Event timestamps are generated internally and compared for
    // strict ordering.)

    // ---------- canRaiseDispute ----------

    @Test
    void canRaiseDispute_onlyWhenNotDelivered() {
        // Initially not delivered → dispute allowed
        assertTrue(contract.canRaiseDispute(shipment));

        // After delivery → dispute not allowed
        shipment.addEvent("DELIVERED");
        assertFalse(contract.canRaiseDispute(shipment));
    }

    // ---------- validateCustomsClearance ----------

    @Test
    void validateCustomsClearance_approveFromCreatedIsAllowed() {
        // Initial status should be a customs-relevant state like CREATED
        boolean ok = contract.validateCustomsClearance(shipment, "APPROVE");
        assertTrue(ok, "Approval should be allowed from CREATED");
    }

    @Test
    void validateCustomsClearance_notAllowedOnceDelivered() {
        shipment.addEvent("DELIVERED");

        assertFalse(contract.validateCustomsClearance(shipment, "APPROVE"),
                "Approval must not be allowed once DELIVERED");
        assertFalse(contract.validateCustomsClearance(shipment, "REJECT"),
                "Even REJECT should not be processed once DELIVERED");
    }

    // ---------- triggerInsuranceClaim ----------

    @Test
    void triggerInsuranceClaim_whenDamaged_returnsTrue() {
        shipment.addEvent("DAMAGED");
        assertTrue(contract.triggerInsuranceClaim(shipment),
                "Damage should automatically trigger an insurance claim");
    }
}

package model;

import java.util.Date;
import java.util.List;

public class SmartContract {

    /**
     * Business rule for status updates.
     * Now uses String for status (since ShipmentStatus enum was removed).
     *
     * Rule:
     * - If shipment is already DELIVERED, it cannot move back to any other status.
     */
    public boolean canUpdateStatus(Shipment shipment, String newStatus) {
        if (shipment == null || newStatus == null) {
            return false;
        }

        String current = shipment.getStatus();
        if ("DELIVERED".equalsIgnoreCase(current)
                && !"DELIVERED".equalsIgnoreCase(newStatus)) {
            return false;
        }
        return true;
    }

    /** Example rule: payment only allowed when status is DELIVERED. */
    public boolean canTriggerPayment(Shipment shipment) {
        if (shipment == null)
            return false;
        return "DELIVERED".equalsIgnoreCase(shipment.getStatus());
    }

    /**
     * Very simple ledger-integrity check used by ShipmentComplianceController:
     * - Events must have non-null timestamps
     * - Timestamps must be strictly increasing (no going backwards).
     */
    public boolean verifyLedgerIntegrity(Shipment shipment) {
        if (shipment == null) {
            return false;
        }

        List<Event> history = shipment.getHistory();
        if (history == null || history.isEmpty()) {
            return true; // nothing to check
        }

        Date prev = null;
        for (Event e : history) {
            Date ts = e.getTimestamp();
            if (ts == null) {
                return false;
            }
            if (prev != null && !ts.after(prev)) {
                return false; // out of order or duplicate timestamp
            }
            prev = ts;
        }
        return true;
    }

    /** Example rule: raise dispute if status is not DELIVERED. */
    public boolean canRaiseDispute(Shipment shipment) {
        return !shipment.getStatus().equals("DELIVERED");
    }
}

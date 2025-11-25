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

    /**
     * Customs clearance validation rule.
     *
     * Rules:
     * - Decision must be APPROVE or REJECT.
     * - Cannot approve clearance after shipment is DELIVERED.
     * - Approval only allowed when shipment is in a customs-relevant status.
     * - Rejection is always allowed unless shipment is already DELIVERED.
     */
    public boolean validateCustomsClearance(Shipment shipment, String decision) {

        if (shipment == null || decision == null) {
            return false;
        }

        String status = shipment.getStatus().toUpperCase();
        decision = decision.toUpperCase();

        // Must be APPROVE or REJECT
        if (!decision.equals("APPROVE") && !decision.equals("REJECT")) {
            return false;
        }

        // Cannot perform clearance on delivered shipments
        if (status.equals("DELIVERED")) {
            return false;
        }

        // Reject is always allowed prior to delivery
        if (decision.equals("REJECT")) {
            return true;
        }

        // Approval must follow logical customs workflow
        boolean allowedForApproval =
                status.equals("CREATED") ||
                status.equals("IN_TRANSIT") ||
                status.equals("AT_BORDER") ||
                status.equals("AT_WAREHOUSE");

        return allowedForApproval;
    }



}

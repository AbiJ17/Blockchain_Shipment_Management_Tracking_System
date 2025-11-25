package model;

public class Warehouse extends User {

    private int warehouseID;
    private int capacity;
    private String address;

    public Warehouse() {
        setRole("WAREHOUSE");
    }

    public Warehouse(int userID,
            String username,
            String password,
            String email,
            int warehouseID,
            int capacity,
            String address) {

        super(userID, username, password, "WAREHOUSE", email);
        this.warehouseID = warehouseID;
        this.capacity = capacity;
        this.address = address;
    }

    // ---- Domain behaviour ----

    public void recordReceipt(Shipment shipment) {
        if (shipment == null)
            return;
        shipment.addHistoryEvent("Shipment received at warehouse " + warehouseID);
        shipment.setStatus("AT_WAREHOUSE");
    }

    public void logStorageDetails(Shipment shipment, String details) {
        if (shipment == null)
            return;
        shipment.addHistoryEvent("Storage details: " + details);
    }

    public void confirmDispatch(Shipment shipment) {
        if (shipment == null)
            return;
        shipment.addHistoryEvent("Shipment dispatched from warehouse " + warehouseID);
        shipment.setStatus("DISPATCHED");
    }

    // ---- Getters / setters ----

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

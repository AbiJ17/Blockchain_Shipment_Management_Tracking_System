package model;

public class LogisticsProvider extends User {

    private int vehicleID;
    private String routeInfo;

    public LogisticsProvider() {
        setRole("LOGISTICS_PROVIDER");
    }

    public LogisticsProvider(int userID,
            String username,
            String password,
            String email,
            int vehicleID,
            String routeInfo) {

        super(userID, username, password, "LOGISTICS_PROVIDER", email);
        this.vehicleID = vehicleID;
        this.routeInfo = routeInfo;
    }

    // ---- Domain behaviour ----

    public void recordPickup(Shipment shipment) {
        if (shipment == null)
            return;
        shipment.setStatus("PICKED_UP");
        shipment.addHistoryEvent("Pickup recorded by logistics provider " + getUsername());
    }

    public void updateTransitStatus(Shipment shipment, String newStatus) {
        if (shipment == null)
            return;
        shipment.setStatus(newStatus);
        shipment.addHistoryEvent("Transit status changed to " + newStatus + " by " + getUsername());
    }

    public void confirmDelivery(Shipment shipment) {
        if (shipment == null)
            return;
        shipment.setStatus("DELIVERED");
        shipment.addHistoryEvent("Delivery confirmed by logistics provider " + getUsername());
    }

    // ---- Getters / setters ----

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }
}

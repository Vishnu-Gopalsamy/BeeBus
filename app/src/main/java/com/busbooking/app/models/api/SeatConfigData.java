package com.busbooking.app.models.api;

public class SeatConfigData {
    private String layout;
    private int seaterSeats;
    private int sleeperLowerSeats;
    private int sleeperUpperSeats;
    private String berthType;

    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }

    public int getSeaterSeats() { return seaterSeats; }
    public void setSeaterSeats(int seaterSeats) { this.seaterSeats = seaterSeats; }

    public int getSleeperLowerSeats() { return sleeperLowerSeats; }
    public void setSleeperLowerSeats(int sleeperLowerSeats) { this.sleeperLowerSeats = sleeperLowerSeats; }

    public int getSleeperUpperSeats() { return sleeperUpperSeats; }
    public void setSleeperUpperSeats(int sleeperUpperSeats) { this.sleeperUpperSeats = sleeperUpperSeats; }

    public String getBerthType() { return berthType; }
    public void setBerthType(String berthType) { this.berthType = berthType; }
}


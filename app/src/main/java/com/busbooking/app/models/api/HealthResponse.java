package com.busbooking.app.models.api;

public class HealthResponse {
    private String status;
    private String timestamp;
    private String version;
    private DatabaseHealth database;
    private double uptime;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public DatabaseHealth getDatabase() { return database; }
    public void setDatabase(DatabaseHealth database) { this.database = database; }

    public double getUptime() { return uptime; }
    public void setUptime(double uptime) { this.uptime = uptime; }

    public static class DatabaseHealth {
        private String status;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

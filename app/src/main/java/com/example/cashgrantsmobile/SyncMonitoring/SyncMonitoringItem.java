package com.example.cashgrantsmobile.SyncMonitoring;

public class SyncMonitoringItem {
    private String username, sync, alias_datetime, update, record;

    public SyncMonitoringItem(String sync, String alias_datetime, String update, String record, String username) {
        this.username = username;
        this.sync = sync;
        this.alias_datetime = alias_datetime;
        this.update = update;
        this.record = record;
    }

    public String getUsername() { return this.username; }
    public String getSync() { return this.sync; }
    public String getDateTime() { return this.alias_datetime; }
    public String getUpdate() { return this.update; }
    public String getRecord() { return this.record; }
}

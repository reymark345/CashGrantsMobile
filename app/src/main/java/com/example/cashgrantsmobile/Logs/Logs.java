package com.example.cashgrantsmobile.Logs;

public class Logs {
    private int id;
    private String username, type, hh_id, description, created_at;

    public Logs(Integer id, String username, String type, String hh_id, String description, String created_at) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.hh_id = hh_id;
        this.description = description;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getType() {
        return this.type;
    }
    public String getHHID() {
        return this.hh_id;
    }
    public String getDescription() {
        return this.description;
    }
    public String getCreated_at() {
        return this.created_at;
    }

    public void setId(int id) {
        this.id = id;
    }
//    public void setUsername(String username) {
//        this.username = username;
//    }
//    public void setLogType(String type) {
//        this.type = type;
//    }
//    public void setHHID(String hh_id) {
//        this.hh_id = hh_id;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//    public void setCreatedAt(String created_at) {
//        this.created_at = created_at;
//    }
}

package com.example.cashgrantsmobile.Unvalidated;

public class UnvalidatedItem {
    private int id;
    private String fullname, address, hh_id;

    public UnvalidatedItem(Integer id, String fullname, String address, String hh_id) {
        this.id = id;
        this.fullname = fullname;
        this.address = address;
        this.hh_id = hh_id;
    }

    public int getId() { return id; }
    public String getFullname() { return this.fullname; }
    public String getAddress() { return this.address; }
    public String getHh_id() { return this.hh_id; }
}

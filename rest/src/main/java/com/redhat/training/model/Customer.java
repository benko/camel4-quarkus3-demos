package com.redhat.training.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = "\t")
public class Customer {
    @DataField(pos = 1)
    private String username;
    @DataField(pos = 2)
    private String firstname;
    @DataField(pos = 3)
    private String lastname;
    @DataField(pos = 4)
    private String department;
    @DataField(pos = 5)
    private String phone;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String toString() {
        return "[cUN:" + this.getUsername() + "," +
                "cFN:" + this.getFirstname() + "," +
                "cLN:" + this.getLastname() + "," +
                "cDP:" + this.getDepartment() + "," +
                "cPH:" + this.getPhone() + "]";
    }
}

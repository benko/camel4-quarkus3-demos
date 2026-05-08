package com.redhat.training.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class ActionItem {
    private String username;
    private String firstname;
    private String lastname;
    private String department;
    private String contactPhone;
    private String assignee;
    private Priority priority;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date submissionDate;
    private String originalText;
    private String actionItem;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date deadline;

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
    public String getContactPhone() {
        return contactPhone;
    }
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    public String getAssignee() {
        return assignee;
    }
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    public Priority getPriority() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public Date getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    public String getOriginalText() {
        return originalText;
    }
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
    public String getActionItem() {
        return actionItem;
    }
    public void setActionItem(String actionItem) {
        this.actionItem = actionItem;
    }
    public Date getDeadline() {
        return deadline;
    }
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    public String toString() {
        return "[cUN:" + this.getUsername() + "," +
                "cFN:" + this.getFirstname() + "," +
                "cLN:" + this.getLastname() + "," +
                "cDP:" + this.getDepartment() + "," +
                "cPH:" + this.getContactPhone() + "," +
                "m:" + this.getOriginalText() + "," +
                "a:" + this.getAssignee() + "]";
    }
}

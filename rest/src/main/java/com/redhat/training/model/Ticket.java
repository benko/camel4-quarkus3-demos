package com.redhat.training.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 0L;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date timestamp;

    @JsonProperty("email")
    private String reporter;

    private Priority priority;

    private String message;

    private boolean processed = false;

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getReporter() {
        return reporter;
    }
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
    public Priority getPriority() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message.trim();
    }
    public boolean isProcessed() {
        return processed;
    }
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    public String toString() {
        return "[" + this.priority + "/" + this.reporter + "/" + this.message + "]";
    }
}

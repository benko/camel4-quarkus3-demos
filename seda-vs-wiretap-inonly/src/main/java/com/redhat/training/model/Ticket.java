package com.redhat.training.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = "\t")
public class Ticket implements Serializable {
    private static final long serialVersionUID = 0L;

    @DataField(pos = 1, required = true, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    @DataField(pos = 2, required = true)
    private String reporter;

    @DataField(pos = 3, required = true)
    private Priority priority;

    @DataField(pos = 4, required = true)
    private String message;

    private boolean processed = false;

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date date) {
        this.timestamp = date;
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

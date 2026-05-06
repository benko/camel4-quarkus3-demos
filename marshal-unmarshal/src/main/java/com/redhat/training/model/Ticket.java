package com.redhat.training.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@CsvRecord(separator = "\t")
public class Ticket implements Serializable {
    private static final long serialVersionUID = 0L;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    @DataField(pos = 1, required = true, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    @JsonProperty("email")
    @DataField(pos = 2, required = true)
    private String reporter;

    @DataField(pos = 3, required = true)
    private Priority priority;

    @DataField(pos = 4, required = true)
    private String message;

    @JsonIgnore
    private boolean processed = false;

    public Date getTimestamp() {
        return timestamp;
    }
    @XmlElement
    public void setTimestamp(Date date) {
        this.timestamp = date;
    }
    public String getReporter() {
        return reporter;
    }
    @XmlElement
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
    public Priority getPriority() {
        return priority;
    }
    @XmlElement
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public String getMessage() {
        return message;
    }
    @XmlElement
    public void setMessage(String message) {
        this.message = message.trim();
    }
    public boolean isProcessed() {
        return processed;
    }
    @XmlTransient
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    public String toString() {
        return "[" + this.priority + "/" + this.reporter + "/" + this.message + "]";
    }
}

package com.redhat.training.services;

import java.util.HashMap;

import org.apache.camel.Exchange;

import com.redhat.training.model.Customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("customer-registry")
public class CustomerRegistry {
    HashMap<String, Customer> customers = new HashMap<>();

    private boolean initialized = false;

    /* Bean processor method - this will be invoked by camel from bean(). */
    public void addCustomer(Exchange e) {
        Customer c = e.getIn().getBody(Customer.class);
        customers.put(c.getUsername(), c);
        if (customers.size() >= ((Integer)e.getProperty("numUsers")).intValue()) {
            this.initialized = true;
        }
    }

    /* This is the lookup method used by the enrichment process. */
    public Customer getCustomer(String username) {
        return customers.get(username);
    }

    /* We wait for this to return true before starting lookups. */
    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean isInitialized) {
        this.initialized = isInitialized;
    }
}

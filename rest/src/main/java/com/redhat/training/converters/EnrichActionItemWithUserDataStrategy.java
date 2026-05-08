package com.redhat.training.converters;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Customer;

public class EnrichActionItemWithUserDataStrategy implements AggregationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(EnrichActionItemWithUserDataStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message m = oldExchange.getIn().copy();
        ActionItem actItem = m.getBody(ActionItem.class);
        Customer customer = newExchange.getIn().getBody(Customer.class);
        LOG.info("Enriching ActionItem: " + actItem);
        LOG.info("Adding customer data: " + customer);

        actItem.setUsername(customer.getUsername());
        actItem.setFirstname(customer.getFirstname());
        actItem.setLastname(customer.getLastname());
        actItem.setDepartment(customer.getDepartment());
        actItem.setContactPhone(customer.getPhone());

        m.setBody(actItem);
        oldExchange.setMessage(m);
        return oldExchange;
    }
}

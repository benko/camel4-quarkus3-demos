package com.redhat.training.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.Ticket;

public class TicketCreatedBean {
    private static final Logger LOG = LoggerFactory.getLogger(TicketCreatedBean.class);

    public void createTicket(Ticket t) {
        LOG.info("Got message body: " + t);
    }
}

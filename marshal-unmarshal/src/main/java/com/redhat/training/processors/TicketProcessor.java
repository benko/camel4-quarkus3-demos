package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.redhat.training.model.Priority;
import com.redhat.training.model.Ticket;

public class TicketProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Ticket t = exchange.getIn().getBody(Ticket.class);
        if (t.getPriority() == Priority.URGENT) {
            // raise alarms
        }
    }
}

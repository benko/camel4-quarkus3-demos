package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.Ticket;

public class DynamicRouterDecision {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicRouterDecision.class);

    public String decideNextStep(Exchange e) {
        Ticket t = e.getIn().getBody(Ticket.class);

        if (t.isProcessed()) {
            LOG.info("Message is already processed. Nothing left to do.");
            return null;
        }

        if (t.getReporter().equals("johndoe@example.com")) {
            LOG.info("Got a ticket from John Doe, this requires special care.");
            return "log:SpecialCare";
        }

        return "log:RegularProcessing";
    }
}

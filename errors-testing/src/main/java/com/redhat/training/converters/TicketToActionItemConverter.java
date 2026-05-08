package com.redhat.training.converters;

import org.apache.camel.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Customer;
import com.redhat.training.model.Ticket;
import com.redhat.training.services.CustomerRegistry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Converter
public class TicketToActionItemConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TicketToActionItemConverter.class);

    @Inject
    CustomerRegistry cr;

    @Converter
    public ActionItem convertTicketToActionItem(Ticket ticket) {
        ActionItem ai = new ActionItem();

        ai.setOriginalText(ticket.getMessage());
        ai.setPriority(ticket.getPriority());
        ai.setSubmissionDate(ticket.getTimestamp());
        ai.setUsername(ticket.getReporter());
        ticket.setProcessed(true);

        // Enrich the message with registry data.
        while (!cr.isInitialized()) {
            LOG.info("Waiting for CustomerRegistry to initialize...");
            try { Thread.sleep(500); } catch (InterruptedException ie) {}
        }
        Customer c = cr.getCustomer(ai.getUsername());
        if (c == null) {
            LOG.error("CustomerRegistry returned null for " + ai.getUsername() + ", skipping enrichment");
            return ai;
        }

        ai.setFirstname(c.getFirstname());
        ai.setLastname(c.getLastname());
        ai.setDepartment(c.getDepartment());
        ai.setContactPhone(c.getPhone());

        return ai;
    }
}

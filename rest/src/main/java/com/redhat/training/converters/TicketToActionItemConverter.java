package com.redhat.training.converters;

import org.apache.camel.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Ticket;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Converter
public class TicketToActionItemConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TicketToActionItemConverter.class);

    @Converter
    public ActionItem convertTicketToActionItem(Ticket ticket) {
        ActionItem ai = new ActionItem();

        LOG.info("Converting ticket to action item: " + ticket);

        /* Things we can easily assign from Ticket. */
        ai.setOriginalText(ticket.getMessage());
        ai.setPriority(ticket.getPriority());
        ai.setSubmissionDate(ticket.getTimestamp());
        ai.setUsername(ticket.getReporter());

        return ai;
    }
}

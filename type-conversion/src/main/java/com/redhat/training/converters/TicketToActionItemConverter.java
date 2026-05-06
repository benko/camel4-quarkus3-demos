package com.redhat.training.converters;

import org.apache.camel.Converter;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Ticket;

@Converter
public class TicketToActionItemConverter {
    @Converter
    public static ActionItem convertTicketToActionItem(Ticket ticket) {
        ActionItem ai = new ActionItem();

        ai.setOriginalText(ticket.getMessage());
        ai.setPriority(ticket.getPriority());
        ai.setSubmissionDate(ticket.getTimestamp());
        ai.setUser(ticket.getReporter());
        ticket.setProcessed(true);

        return ai;
    }
}

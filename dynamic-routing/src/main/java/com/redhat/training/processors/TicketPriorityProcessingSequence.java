package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.redhat.training.model.Priority;
import com.redhat.training.model.Ticket;

public class TicketPriorityProcessingSequence implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message out = exchange.getIn().copy();
        Ticket t = out.getBody(Ticket.class);
        // Map x = https://kubernetes.default/api/v1/namespaces/foo/configmaps/processing-steps
        // if (x.get(t.getPriority()) != null) {
        //     out.setHeader("processing-sequence", x.get(t.getPriority()));
        // } else {
        //     out.setHeader("processing-sequence", "direct:foo");
        // }
        switch (t.getPriority()) {
            case Priority.IDLE:
                out.setHeader("processing-sequence", "direct:idle");
                break;

            case Priority.URGENT:
                out.setHeader("processing-sequence", "direct:urgent,direct:regular,direct:idle");
                break;

            default:
                out.setHeader("processing-sequence", "direct:regular,direct:idle");
        }
        exchange.setMessage(out);
    }

}

package com.redhat.training.routes;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.model.Ticket;

public class GeneralTicketProcessing extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:other-processing?concurrentConsumers=10")
            .log("GENERAL TICKET PROCESSING: ${exchangeId}, XID: ${exchangeProperty.CamelCorrelationId}")
            .process(exchange -> {
                try { Thread.sleep(3000); } catch (InterruptedException ie) {}
                Message out = exchange.getIn().copy();
                out.getBody(Ticket.class).setProcessed(true);
                exchange.setMessage(out);
            })
            .log("TICKET PROCESSING COMPLETE: ${exchangeId}");
    }
}

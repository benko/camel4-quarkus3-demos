package com.redhat.training.routes;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.model.Ticket;

public class WireTapTicketProcessing extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:split")
            .log("WIRETAP TICKET PROCESSING: ${exchangeId}, XID: ${exchangeProperty.CamelCorrelationId}")
            .process(exchange -> {
                try { Thread.sleep(3000); } catch (InterruptedException ie) {}
                Message out = exchange.getIn().copy();
                out.getBody(Ticket.class).setProcessed(true);
                exchange.setMessage(out);
            })
            .log("TICKET PROCESSING COMPLETE: ${exchangeId}");
    }
}

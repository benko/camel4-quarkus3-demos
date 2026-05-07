package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.redhat.training.model.Ticket;
import com.redhat.training.processors.TicketCreatedBean;
import com.redhat.training.processors.TicketCreatedProcessor;

public class CreateTicketRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:createTicket")
            .log("Received request: ${body}")
            // Prior to unmarshaling.
            .process(new TicketCreatedProcessor())
            // Must unmarshal before calling a Ticket method signature.
            .unmarshal().json(Ticket.class)
            .bean(TicketCreatedBean.class)
            .log("Got ticket: ${body}")
            .marshal().json(JsonLibrary.Jackson)
            .to("file:data/tickets")
            // Provide a response to the client.
            .setHeader("CamelHttpResponseCode", constant("201"))
            .setBody(constant("{\"status\": \"created\"}"));
    }
}

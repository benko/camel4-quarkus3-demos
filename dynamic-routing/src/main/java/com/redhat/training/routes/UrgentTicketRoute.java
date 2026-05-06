package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

public class UrgentTicketRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:urgent")
            .log("URGENT TICKET: ${exchangeId}: ${body}");
            // .to("direct:regular");
    }
}

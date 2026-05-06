package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

public class IdleTicketRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:idle")
            .log("IDLE TICKET: ${exchangeId}: ${body}");
    }
}

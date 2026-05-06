package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

public class RegularTicketRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:regular")
            .log("REGULAR TICKET: ${exchangeId}: ${body}");
            // .to("direct:idle");
    }
}

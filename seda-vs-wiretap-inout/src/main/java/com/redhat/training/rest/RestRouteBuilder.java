package com.redhat.training.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .bindingMode(RestBindingMode.off);

        /* Send a POST request to this endpoint using curl:
            curl -v -H "Content-Type: application/json" \
                 -XPOST -d @./data/input/sample-ticket.json \
                 http://localhost:8081/tickets

            NOTE: SEDA will produce a ticket with "processed": true, with a 3s delay.
                  direct will produce a ticket with "processed": false, immediately.
                  direct+processor will act like SEDA, but at the cost of polling.
        */
        rest("/tickets")
            .post()
                .to("direct:createTicket");
    }
}

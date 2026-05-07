package com.redhat.training.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            // Binding mode is for PRODUCER ONLY.
            .bindingMode(RestBindingMode.off)
            .dataFormatProperty("prettyPrint", "true");

        /* Send a POST request to this endpoint using curl:
            curl -v -H "Content-Type: application/json" \
                 -XPOST -d @./data/input/sample-ticket.json \
                 http://localhost:8081/tickets
        */
        rest("/tickets")
            .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:createTicket");
    }
}

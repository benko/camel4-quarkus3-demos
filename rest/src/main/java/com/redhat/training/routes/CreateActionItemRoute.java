package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.converters.EnrichActionItemWithUserDataStrategy;
import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Customer;
import com.redhat.training.model.Ticket;

public class CreateActionItemRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:createActionItem")
            // Raw JSON request.
            .to("log:CreateRequestReceived")
            .convertBodyTo(String.class)
            // Unmarshal to Ticket
            .unmarshal().json(Ticket.class)
            .log("Unmarshaled body: ${body}")
            // Convert Ticket to blank ActionItem.
            .convertBodyTo(ActionItem.class)
            .log("Converted body: ${body}")
            // Use a REST client to enrich the ActionItem
            .setHeader("username", simple("${body.username}"))
            .enrich("direct:enrich", new EnrichActionItemWithUserDataStrategy())
            .to("log:EnrichedActionItem")
            // Marshal it back to JSON.
            .marshal().json()
            .log("Writing body to file: ${body}")
            .to("file:data/out/?fileExist=Override");

        from("direct:enrich")
            .to("rest:get:/api/v1/customers/{username}?host=localhost:8081")
            .unmarshal().json(Customer.class);
    }
}

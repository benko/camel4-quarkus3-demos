package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.redhat.training.model.Ticket;
import com.redhat.training.processors.RepeatAndWaitForChangeProcessor;

public class CreateTicketRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:createTicket")
            .log("Received request: ${body}")
            .unmarshal().json(Ticket.class)
            .log("Got ticket: ${body}")
            /* (Un)comment accordingly. You need the processor with wireTap()
               or you will not see the change in the ticket. Try both with and
               without the processor, of course.
            */
            // .to("seda:processTicket")
            .wireTap("direct:processTicket")
            .process(new RepeatAndWaitForChangeProcessor())
            .marshal().json(JsonLibrary.Jackson)
            .to("file:data/tickets");
    }

}

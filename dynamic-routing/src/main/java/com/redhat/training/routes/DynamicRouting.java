package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.Ticket;

public class DynamicRouting extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat ticketCsv = new BindyCsvDataFormat(Ticket.class);

        /* Uncomment your desired test below. */
        from("file:data/in/?include=ticket.*\\.txt&noop=true")
            .routeId("Ticket Reader and Processor")
            .to("log:incomingMessage")
            .unmarshal(ticketCsv)
            .to("log:processedMessage")
            /* CBR choice */
            // .choice()
            //     .when(simple("${body.priority} == 'IDLE'"))
            //         .to("direct:idle")
            //     .when(simple("${body.priority} == 'URGENT'"))
            //         .to("direct:urgent")
            //     .otherwise()
            //         .to("direct:regular")
            // .end()
            /* Routing Slip */
            // .process(new TicketPriorityProcessingSequence())
            // .routingSlip().header("processing-sequence")
            /* Dynamic Router - NOTE: This deliberately creates an endless loop. */
            // .dynamicRouter().method(DynamicRouterDecision.class)
            /* Dynamic Endpoint - NOTE: This will block unless there is a consumer on direct:high. */
            // .setHeader("foo", simple("direct:${body.priority.toString().toLowerCase()}"))
            // .log("Sending message for processing to ${header.foo}")
            // .toD("${header.foo}")
            .marshal().json()
            .setHeader("CamelFileName").constant("processed-tickets.json")
            .to("file:data/out/?fileExist=Append");
    }
}

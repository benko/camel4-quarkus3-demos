package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.Ticket;
import com.redhat.training.processors.RepeatAndWaitForChangeProcessor;

public class SedaAndWireTapRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat ticketCsv = new BindyCsvDataFormat(Ticket.class);

        from("file:data/in/?include=ticket.*\\.txt&noop=true")
            .routeId("Ticket Reader and Processor")
            .to("log:incomingMessage")
            .unmarshal(ticketCsv)
            .to("log:unmarshaledMessage")
            /* SEDA vs WireTap for polling endpoints (InOnly) */
            // Send the message to SEDA for processing.
            .log("Sending exchange ${exchangeId} for async processing...")
            .to("seda:other-processing")
            .log("Waiting for async processing to complete...")
            .process(new RepeatAndWaitForChangeProcessor())
            // reset processed status for the ticket
            .process(exchange -> { exchange.getIn().getBody(Ticket.class).setProcessed(false); })
            // Send the message through WireTap for processing.
            .log("Tapping into ${exchangeId} for processing...")
            .wireTap("direct:split")
            .process(new RepeatAndWaitForChangeProcessor())
            .log("After tap, in ${exchangeId}...");
    }
}

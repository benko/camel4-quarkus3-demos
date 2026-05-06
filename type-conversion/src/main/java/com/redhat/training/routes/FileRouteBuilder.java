package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Ticket;
import com.redhat.training.processors.RepeatAndWaitForChangeProcessor;

public class FileRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat ticketCsv = new BindyCsvDataFormat(Ticket.class);

        from("file:data/in/?include=ticket.*\\.txt&noop=true")
            .routeId("Ticket Reader and Processor")
            .to("log:incomingMessage")
            .unmarshal(ticketCsv)
            .to("log:processedMessage")
            .filter().simple("${body.priority} == 'URGENT'")
            .log("BEFORE CONVERSION: ${body}")
            .wireTap("direct:ticketUpdate")
            .convertBodyTo(ActionItem.class)
            .log("AFTER CONVERSION: ${body}")
            .marshal().json()
            .to("file:data/out/?fileName=actionItems.json&fileExist=Append");

        from("direct:ticketUpdate")
            .process(new RepeatAndWaitForChangeProcessor())
            .log("TICKET PROCESSING STATE: ${body.processed}")
            .marshal().json()
            .to("file:data/out/?fileName=processed-tickets.json&fileExist=Append");
    }
}

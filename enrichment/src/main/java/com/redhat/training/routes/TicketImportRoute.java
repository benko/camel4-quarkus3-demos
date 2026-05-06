package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Ticket;

public class TicketImportRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DataFormat ticketsCsv = new BindyCsvDataFormat(Ticket.class);

        from("file:data/in/?fileName=tickets.csv&noop=true")
            /* Two other splitting options - in case input is XML, and in case
               input is large and we need to stream it.
            */
            // .split().tokenizeXML("ticket")
            // .split(body().tokenize("\n")).streaming()
            /* For our relatively small CSV, this is the best performance. */
            .unmarshal(ticketsCsv)
            .to("log:LoadedData")
            .split().body()
            // log after split
            .log("SPLIT: ${body}")
            // only process high and urgent priority messages
            .filter().simple("${body.priority} in 'URGENT,HIGH'")
            .log("FILTERED: ${body}")
            /* This is the key - TicketToActionItemConverter performs not just
               the conversion, but also the enrichment from CustomerRegistry.
            */
            .convertBodyTo(ActionItem.class)
            .marshal().json()
            .to("file:data/out/?fileName=action-items.json&fileExist=Append");
    }
}

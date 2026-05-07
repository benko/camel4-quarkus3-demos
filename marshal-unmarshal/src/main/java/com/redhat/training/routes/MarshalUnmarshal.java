package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.Ticket;

public class MarshalUnmarshal extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat ticketCsv = new BindyCsvDataFormat(Ticket.class);
        // Uncomment when testing XML marshaling/unmarshaling.
        // It requires you to import org.apache.camel.converter.jaxb.JaxbDataFormat
        // DataFormat ticketXml = new JaxbDataFormat("com.redhat.training.model");

        /* Several unmarshal-marshal options. Uncomment as desired. */
        from("file:data/in/?include=ticket.*\\.txt&noop=true")
        // from("file:data/xml/?include=ticket.*\\.xml&noop=true")
        // from("file:data/json/?include=ticket.*\\.json&noop=true")
            .routeId("Ticket Reader and Processor")
            .to("log:incomingMessage")
            .unmarshal(ticketCsv)
            // .unmarshal(ticketXml)
            // .unmarshal().json(Ticket.class)
            .to("log:processedMessage")
            /* Filtering: you do JSONPath and XPath before unmarshaling.
               Move these lines above, before unmarshal(), if testing.
            */
            // .filter().jsonpath(".email == 'johndoe@example.com'")
            // .filter().xpath("/ticket/priority/text() = 'URGENT'")
            .filter().simple("${body.priority} == 'URGENT'")
            // .marshal(ticketCsv)
            // .marshal(ticketXml)
            .marshal().json()
            // .setHeader("CamelFileName").constant("processed-tickets.txt")
            // .setHeader("CamelFileName").constant("processed-tickets.xml")
            .setHeader("CamelFileName").constant("processed-tickets.json")
            .to("file:data/out/?fileExist=Append");

        /* Direct XML to JSON conversion. No data formats needed. */
        // from("file:data/xml/?include=ticket.*\\.xml&noop=true")
        //     .to("xj:identity?transformDirection=XML2JSON")
        //     .to("file:data/out/?fileName=converted.json&fileExist=Append");

        /* Direct JSON to XML conversion. No data formats needed. */
        // from("file:data/json/?include=ticket.*\\.json&noop=true")
        //     .to("xj:identity?transformDirection=JSON2XML")
        //     .to("file:data/out/?fileName=converted.xml&fileExist=Append");
    }
}

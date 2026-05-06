package com.redhat.training.routes;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.processors.CustomMessageProcessor;

public class ProcessingRouteBuilder extends RouteBuilder {
    // Pojo foo = new Pojo();
    CustomMessageProcessor cmp = new CustomMessageProcessor();

    @Override
    public void configure() throws Exception {
        from("file:data/in/?include=ticket.*\\.txt&noop=true")
            .routeId("Ticket Reader and Processor")
            .log("Received ticket: ${body}")
            .log("Ticket file name: ${header.camelfilename}")
            .log("Ticket size: ${header.CAMELFILELENGTH}")
            /* The below two are equivalent, but the uncommented version has an
               endpoint prefix that allows you to easily trace it (or exclude
               it from tracing)
            */
            // .bean("pojoBeanProcessor")
            .to("bean:pojoBeanProcessor")
            /* Invoke an implementation of the Processor interface. */
            .process(new CustomMessageProcessor())
            .log("AFTER CustomMessageProcessor: ${body}")
            /* Supply an inline (lambda) implementation of Processor. */
            .process(x -> {
                Message out = x.getIn().copy();
                out.setBody(x.getIn().getBody(String.class).toLowerCase());
                x.setMessage(out);
            })
            .log("AFTER INLINE PROCESSOR: ${body}")
            /* The below two have the same effect: they cause the file name
               to use the exchange ID.
            */
            // .removeHeader("CamelFileName")
            // .setHeader("CamelFileName").simple("${exchangeId}")
            /* The below two options are equivalent. */
            // Option 1: use fileName URI option to File component.
            // .to("file:data/out/?fileName=output.txt&fileExist=Append");
            // Option 2: set the CamelFileName message header.
            .setHeader("CamelFileName").constant("output.txt")
            .to("file:data/out/?fileExist=Append");
    }
}

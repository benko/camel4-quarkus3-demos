package com.redhat.training.routes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListActionItemsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:listActionItems")
            .to("log:ListRequestReceived")
            // Replace the current message with the outcome of the below route.
            // (note - no AggregationStrategy, but body is null at this point anyway)
            .enrich("direct:getActionItems")
            // No need to marshal, because we simply read files.
            .convertBodyTo(String.class)
            .to("log:TicketsRead");

        from("direct:getActionItems")
            // Get the number of files in data/out/ from a processor.
            .process(x -> {
                Logger LOG = LoggerFactory.getLogger(ListActionItemsRoute.class);
                File[] contents = new File("data/out/").listFiles(f -> {
                    // Ignore hidden files.
                    if (f.getName().startsWith(".")) {
                        return false;
                    }
                    return true;
                });
                LOG.info("Processing the following files:");
                for (File f : contents) {
                    LOG.info(f.getName());
                }
                // Store the number of files found in a message header.
                x.getIn().setHeader("nFiles", contents.length);
            })
            .log("Found ${header.nFiles} actionItem(s).")
            // Store the number of files read in a header as well.
            .setHeader("nAdded", constant(0))
            .loopDoWhile().simple("${header.nAdded} < ${header.nFiles}")
                .log("In read loop, got ${header.nAdded}/${header.nFiles} action items.")
                // Read files from data/out/ - pollEnrich always reads one at a time anyway.
                .pollEnrich("file:data/out/?noop=true&idempotent=false", 500, (o, n) -> {
                    Logger LOG = LoggerFactory.getLogger(ListActionItemsRoute.class);
                    int nAdded = o.getMessage().getHeader("nAdded", Integer.class).intValue();
                    if (n == null) {
                        LOG.info("pollEnrich: New exchange is null.");
                        return o;
                    }
                    List<String> contents = new ArrayList<>();
                    Message m = o.getIn().copy();
                    if (m.getBody() instanceof List) {
                        LOG.info("pollEnrich: Old exchange already has results. Retrieving.");
                        contents = m.getBody(List.class);
                    }
                    contents.add(n.getIn().getBody(String.class));
                    nAdded++;
                    m.setBody(contents);
                    m.setHeader("nAdded", nAdded);
                    o.setMessage(m);

                    return o;
                })
                .log("End loop iteration, got ${header.nAdded}/${header.nFiles} action items.");

    }

}

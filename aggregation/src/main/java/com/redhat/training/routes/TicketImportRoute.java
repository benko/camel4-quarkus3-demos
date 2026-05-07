package com.redhat.training.routes;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Ticket;
import com.redhat.training.processors.AssignActionItem;

public class TicketImportRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DataFormat ticketsCsv = new BindyCsvDataFormat(Ticket.class);

        from("file:data/in/?fileName=tickets.csv&noop=true")
            .unmarshal(ticketsCsv)
            .to("log:LoadedData")
            // Uses the consumer thread pool
            .split().body()
            /* Use current thread pool to parallelize processing of split exchanges. */
            // .parallelProcessing()
            /* Use a new thread pool to parallelize processing of split exchanges. */
            // .threads(15).maxQueueSize(100)
            .log("SPLIT: ${body}")
            .filter().simple("${body.priority} in 'URGENT,HIGH'")
            .log("FILTERED: ${body}")
            .convertBodyTo(ActionItem.class)
            .bean(AssignActionItem.class)
            .log("${body.username} assigned to ${body.assignee}...")
            // Correlation key is the assignee of the ActionItem - group
            // ActionItems by whoever is responsible for them.
            .aggregate(simple("${body.assignee}"),
                        // Lambda for AggregationStrategy. Could be a separate class.
                        (oldEx, newEx) -> {
                            List<ActionItem> actionItems;
                            Message m;
                            // Initialize the list if this is the start of a new group.
                            if (oldEx == null) {
                                actionItems = new ArrayList<>();
                                // This is just to get the headers and other meta.
                                // The body will be replaced below.
                                m = newEx.getMessage().copy();
                            } else {
                                actionItems = oldEx.getIn().getBody(List.class);
                                m = oldEx.getMessage().copy();
                            }
                            // Add the current action item to the group (engineer).
                            actionItems.add(newEx.getIn().getBody(ActionItem.class));
                            // Store the list as a new message and return the exchange.
                            m.setBody(actionItems);
                            m.setHeader("CamelFileName",
                                        newEx.getIn().getBody(ActionItem.class).getAssignee()
                                                        .toLowerCase()
                                                        .replace(' ', '-')
                                                        + ".json");
                            newEx.setMessage(m);
                            return newEx;
                        })
                .completionTimeout(5000)
            .log("Aggregation completed for ${header.CamelFileName}...")
            .marshal().json()
            .to("file:data/out/?fileExist=Override");
    }
}

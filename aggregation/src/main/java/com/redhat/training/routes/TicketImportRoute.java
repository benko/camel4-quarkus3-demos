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
            .split().body()
            .log("SPLIT: ${body}")
            .filter().simple("${body.priority} in 'URGENT,HIGH'")
            .log("FILTERED: ${body}")
            .convertBodyTo(ActionItem.class)
            .bean(AssignActionItem.class)
            .log("${body.username} assigned to ${body.assignee}...")
            .aggregate(simple("${body.assignee}"),
                        (oldAI, newAI) -> {
                            List<ActionItem> actionItems;
                            Message m;
                            // Initialize the list if this is the start of a new group.
                            if (oldAI == null) {
                                actionItems = new ArrayList<>();
                                m = newAI.getMessage().copy();
                            } else {
                                actionItems = oldAI.getIn().getBody(List.class);
                                m = oldAI.getMessage().copy();
                            }
                            // Add the current action item to the group (engineer).
                            actionItems.add(newAI.getIn().getBody(ActionItem.class));
                            // Store the list as a new message and return the exchange.
                            m.setBody(actionItems);
                            m.setHeader("CamelFileName",
                                        newAI.getIn().getBody(ActionItem.class).getAssignee()
                                                        .toLowerCase()
                                                        .replace(' ', '-')
                                                        + ".json");
                            newAI.setMessage(m);
                            return newAI;
                        })
                .completionTimeout(5000)
            .log("Aggregation completed for ${header.CamelFileName}...")
            .marshal().json()
            .to("file:data/out/?fileExist=Override");
    }
}

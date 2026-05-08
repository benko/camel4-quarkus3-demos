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

        // Error handler for this entire RouteBuilder.
        // errorHandler(noErrorHandler());
        errorHandler(defaultErrorHandler());

        // Overriding the above for just specific exception type.
        onException(IllegalStateException.class)
            .to("direct:illegalStateExceptions")
            .handled(true)
            .maximumRedeliveries(5)
            .redeliveryDelay(500)
            .onRedeliveryRef("processorName");

        // DLQ handler route for the below error handler.
        from("direct:dead-letter-queue")
            .to("log:ErrorHandler")
            .log("Cause: ${exception}");

        // This route has an error handler overriding the RouteBuilder default.
        from("file:data/in/?fileName=tickets.csv&noop=true")
            // Any exception, unless handled by doTry(), is passed to this handler
            // in the direct:dead-letter-queue route.
            .errorHandler(deadLetterChannel("direct:dead-letter-queue"))
            .routeId("import-ticket-to-action-item-and-aggregate")
            .unmarshal(ticketsCsv)
            .to("log:LoadedData")
            .split().body()
            .log("SPLIT: ${body}")
            .filter().simple("${body.priority} in 'URGENT,HIGH'")
            .log("FILTERED: ${body}")
            .convertBodyTo(ActionItem.class)
            // Whatever exceptions are caught by this doTry() will not be passed
            // to any of the above errorHandler() or onException() as this is as
            // specific as exception handling gets.
            // Any uncaught exception is passed to the upper handlers, if there
            // is a route-specific one (as is the case here), it will handle it,
            // otherwise the RouteBuilder settings (onException if it matches, or
            // errorHandler) will be invoked.
            .doTry()
                .bean(AssignActionItem.class)
            .doCatch(ArrayIndexOutOfBoundsException.class)
                // Note this does not remediate an empty assignee. This will
                // further cause "Invalid correlation key" exceptions below, but
                // that will be handled by the DLQ errorHandler() of this route.
                // The correct way of resolving this issue would be to redeliver
                // to the AssignActionItem bean.
                .to("log:ArrayIndexOutOfBoundsOccurred")
            .doFinally()
                .log("Got through critical section.")
            .end()
            .log("${body.username} assigned to \"${body.assignee}\"...")
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
                .completionTimeout(1500)
            .log("Aggregation completed for ${header.CamelFileName}...")
            .marshal().json()
            .to("file:data/out/?fileExist=Override");
    }
}

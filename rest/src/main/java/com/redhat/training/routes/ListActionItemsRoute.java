package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

public class ListActionItemsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:listActionItems")
            .to("log:ListRequestReceived");
    }

}

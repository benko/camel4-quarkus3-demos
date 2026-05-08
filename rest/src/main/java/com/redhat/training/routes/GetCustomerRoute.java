package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;

public class GetCustomerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:getCustomerData")
            .setBody(simple("${header.username}"))
            .bean("customer-registry", "getCustomer")
            .log("Returning customer: ${body}")
            .marshal().json();
    }
}

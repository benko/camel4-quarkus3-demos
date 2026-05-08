package com.redhat.training.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

import com.redhat.training.model.Customer;

public class CustomerImportRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DataFormat customers = new BindyCsvDataFormat(Customer.class);

        from("file:data/in/?fileName=customers.csv&noop=true")
            .unmarshal(customers)
            .setProperty("numUsers", simple("${body.size()}"))
            .split().body()
            .bean("customer-registry");
    }
}

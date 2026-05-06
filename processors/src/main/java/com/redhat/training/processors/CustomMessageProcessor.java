package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class CustomMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Message out = in.copy();
        out.setBody("Processed message: " + in.getBody(String.class).toUpperCase());
        exchange.setMessage(out);
    }
}

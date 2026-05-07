package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketCreatedProcessor implements Processor {
    public static final Logger LOG = LoggerFactory.getLogger(TicketCreatedProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.info("Got message body: " + exchange.getIn().getBody());
    }
}

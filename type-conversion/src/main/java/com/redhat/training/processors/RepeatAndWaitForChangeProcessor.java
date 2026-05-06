package com.redhat.training.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.model.Ticket;

public class RepeatAndWaitForChangeProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(RepeatAndWaitForChangeProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Ticket t = exchange.getIn().getBody(Ticket.class);
        while (!t.isProcessed()) {
            LOG.info("Waiting for async processing to complete...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
        LOG.info("Async processing complete.");
    }
}

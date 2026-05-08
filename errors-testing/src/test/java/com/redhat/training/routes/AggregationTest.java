package com.redhat.training.routes;

import static org.mockito.ArgumentMatchers.*;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.redhat.training.model.ActionItem;
import com.redhat.training.model.Customer;
import com.redhat.training.processors.AssignActionItem;
import com.redhat.training.services.CustomerRegistry;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AggregationTest extends CamelQuarkusTestSupport {
    // Inject a mock customer registry so we can remove the dependency on
    // the customer import route.
    @InjectMock
    CustomerRegistry cr;

    // Inject a mock AssignActionItem so we do not depend on that bean either.
    // It is "buggy" and that is not this route's fault.
    @InjectMock
    AssignActionItem aai;

    // Any output from this route will be collected by this mock endpoint.
    // See setup() and reconfigureRoute() for the code that ensures this happens.
    @EndpointInject("mock:file:out")
    MockEndpoint producerEndpoint;

    // Instantiates the route we're about to test.
    // Use this alternative when testing multiple routes at a time:
    // protected RoutesBuilder[] createRouteBuilders() throws Exception { ... }
    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new TicketImportRoute();
    }

    @BeforeEach
    public void setup() throws Exception {
        // Ensure CustomerRegistry always reports initialized state.
        Mockito.when(cr.isInitialized()).thenReturn(true);

        // Return the correct data whenever looking up a customer.
        Customer c = new Customer();
        c.setDepartment("Operations");
        c.setFirstname("John");
        c.setLastname("Doe");
        c.setPhone("+123456789");
        Mockito.when(cr.getCustomer("johndoe@example.com")).thenReturn(c);

        // Reconfigure the behaviour of AssignActionItem - note that because the
        // bean method is returning void() and modifying the ActionItem in-memory,
        // the approach to achieve the desired effect is different than above.
        Mockito.doAnswer(
            new Answer<Void>() {
                public Void answer(InvocationOnMock inv) {
                    Object[] args = inv.getArguments();
                    ((ActionItem)args[0]).setAssignee("Batman Superman");
                    return null;
                }
            }
        ).when(aai).assignEngineer(any(ActionItem.class));

        // Reconfigure the route's consumer and producer.
        AdviceWith.adviceWith(context(),
                                "import-ticket-to-action-item-and-aggregate",
                                this::reconfigureRoute);
    }

    private void reconfigureRoute(AdviceWithRouteBuilder route) {
        // Always receive from direct:startTest and do not touch the filesystem.
        route.replaceFromWith("direct:startTest");
        // Always produce to mock:file:out instead of the actual filesystem.
        route.interceptSendToEndpoint("file:data/out/?fileExist=Override")
            .skipSendToOriginalEndpoint()
            .to("mock:file:out");
    }

    @Test
    public void testSingleTicket() throws InterruptedException {
        // The action item expected from the route.
        String expectedResponse = "[{\"username\":\"johndoe@example.com\",\"firstname\":\"John\",\"lastname\":\"Doe\",\"department\":\"Operations\",\"contactPhone\":\"+123456789\",\"assignee\":\"Batman Superman\",\"priority\":\"URGENT\",\"submissionDate\":\"2025-01-04T10:05:00\",\"originalText\":\"What time is it?\",\"actionItem\":null,\"deadline\":null}]";

        // Assert some expectations:
        // - we shall receive exactly one message
        producerEndpoint.expectedMessageCount(1);
        // - the message shall contain the expectedResponse body
        producerEndpoint.expectedBodiesReceived(expectedResponse);
        // - the message should contain a CamelFileName header with the value of batman-superman.json
        producerEndpoint.expectedHeaderReceived("CamelFileName", "batman-superman.json");

        // Kick it off: send an example ticket to direct:startTest
        this.template.sendBody(
            "direct:startTest",
            "2025-01-04 12:05:00\tjohndoe@example.com\tURGENT\tWhat time is it?\n"
        );

        // Ensure assertions are satisfied.
        producerEndpoint.assertIsSatisfied();
    }
}

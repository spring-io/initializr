{{=<% %>=}}
package <%packageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel routes for REST consumer
 * 
 */
@Component
public class RestRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        rest("/{{rest.base.path}}/")
                .get("/{id}")
                    // TODO: route
                    .to("direct:customerDetail")
                .get("/{id}/orders")
                     // TODO: route
                    .to("direct:customerOrders");
    }
}

<%={{ }}=%>
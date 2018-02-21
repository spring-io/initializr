{{=<% %>=}}
package <%packageName%>;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


/**
 * Camel routes for file connectors
 * 
 */
@Component
public class FileRouter extends RouteBuilder {
    
    @Override
    public void configure() {
        // Consume files from file.in.dir directory
        from("file://{{file.in.dir}}?move={{file.done.dir}}&moveFailed={{file.failed.dir}}").id("ReadFileRoute")
                .autoStartup("{{fileReader.enabled}}")
                
                .log("Received file [${headers.CamelFileName}] from [{{file.in.dir}}] directory.")
                
                .log("TODO: Implement your routing");

        // Save file into {{file.out.dir}} using CamelFileName header (Exchange.FILE_NAME) to the desired.
        from("direct:saveFile").id("SaveFileRoute").autoStartup("{{fileWriter.enabled}}")
                .log("Saving file into [{{file.out.dir}}] using name [${headers.CamelFileName}]...")

                .to("file://{{file.out.dir}}?fileName=${headers.CamelFileName}").id("SaveFile")

                .log("\n*** Saved to folder [{{file.out.dir}}] as [${headers.CamelFileNameProduced}]");
    }
}
<%={{ }}=%>

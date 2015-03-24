<% def root = !resolvedDependencies.empty && !resolvedDependencies.collect({it.id}).contains('root_starter') %>
This project was generated from [Spring Initializr](${serviceUrl}) with standard Spring Boot features<% if (root) {%>, plus:<%} else {%>.<%}%> 
<% if (root) { resolvedDependencies.each { %>
* _${it.id}_: ${it.description}<% } %>
<% }%>
You can build and run it with

    \$ <% if (type=='gradle') { %>gradle bootRun<% } else { %>mvn spring-boot:run<% } %>

<% if (dependencies.contains('web')) { %>This will start an embedded container on [port 8080](http://localhost:8080) by default. <% } %>For more information on [Spring Boot](http://projects.spring.io/spring-boot) and Spring please visit the [Spring IO](http://spring.io) website.

> Note: To run the command above you will need Java and <%if(type=='gradle'){%>gradle<%}else{%>maven<%}%> (which you can get using the [standard download location](<%if(type=='gradle'){%>http://gradle.org/downloads<%}else{%>http://maven.apache.org/download.cgi<%}%>), or [gvm](http://gvmtool.net), or `brew` on MacOS).

The project can be re-generated with the equivalent of this command:

    \$ curl ${serviceUrl}/starter.zip -d bootVersion=${bootVersion} \\
      -d javaVersion=${javaVersion} \\
      -d packaging=${packaging} \\
      -d type=${type} \\
      -d language=${language}<% dependencies.each { %> \\
      -d dependencies=${it}<% } %>
      

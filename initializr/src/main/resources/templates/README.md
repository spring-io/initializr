This project was generated from [Spring Initializr](${serviceUrl}) with

    bootVersion=${bootVersion}
    javaVersion=${javaVersion}
    packaging=${packaging}
    type=${type}
    language=${language}<% dependencies.each { %>
    dependencies=${it}<% } %>

You can build and run it with

    \$ <% type=='gradle' ? 'gradle bootRun' : 'mvn spring-boot:run' %>

If the project is a web application this will run it up on [port 8080](http://localhost:8080) by default.

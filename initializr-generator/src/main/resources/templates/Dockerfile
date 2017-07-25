FROM alpine:edge

VOLUME /tmp

{{#mavenBuild}}
ADD target/{{applicationName}}.{{packaging}} /tmp/{{applicationName}}.{{packaging}}
{{/mavenBuild}}
{{^mavenBuild}}
ADD build/libs/{{applicationName}}.{{packaging}} /tmp/{{applicationName}}.{{packaging}}
{{/mavenBuild}}

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/{{applicationName}}.{{packaging}}"]
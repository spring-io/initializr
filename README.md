# Spring Initializr

## Prerequisites

You need Java (1.6 or better) and a bash-like shell.

If you are on a Mac and using [homebrew](http://brew.sh/), all you need to do to install it is:

    $ brew tap pivotal/tap
    $ brew install springboot

It will install `/usr/local/bin/spring`. You can jump right to [running the app](#running_the_app).

An alternative way to install the `spring` command line interface can be installed like this:

    $ curl start.spring.io/install.sh | bash

After running that command you should see a `spring` directory:

    $ ./spring/bin/spring --help

    usage: spring [--help] [--version]
       <command> [<args>]
    ...

You could add that `bin` directory to your `PATH` (the examples below
assume you did that).

If you don't have `curl` or `zip` you can probably get them (for
Windows users we recommend [cygwin](http://cygwin.org)), or you can
download the [zip file](http://start.spring.io/spring.zip) and unpack
it yourself.

## Project structure

Initializr is a library that provides all the default features and a service with a very simple script
that uses the auto-configuration feature of Spring Boot. All you need is _grabbing_ the library and
create a proper configuration file with the following script:

```
package org.acme.myapp

@Grab('io.spring.initalizr:initializr:1.0.0.BUILD-SNAPSHOT')
class InitializerService { }
```

As a reference, `initializr-service` represents the _default_ service that runs at http://start.spring.io

<a name="running_the_app"></a>
## Running the app locally

First make sure that you have built the library:

    $ cd initializr
    $ mvn clean install

Once you have done that, you can easily start the app using the spring command from the `initializr-service`
directory (`cd ../initializr-service`):

    $ spring run app.groovy

## Deploying to Cloud Foundry

If you are on a Mac and using [homebrew](http://brew.sh/), install the Cloud Foundry CLI:

    $ brew install cloudfoundry-cli

Alternatively, download a suitable binary for your platform from [Pivotal Web Services](https://console.run.pivotal.io/tools).

An example Cloud Foundry `manifest.yml` file is provided. You should ensure that
the application name and URL (name and host values) are suitable for your environment
before running `cf push`.

You can jar up the app and make it executable in any environment.

    $ spring jar start.jar app.groovy

To deploy on Cloudfoundry:

    $ cf push start -p start.jar -n start-<space>
    
Where `<space>` is the name of the space. As a failsafe, and a
reminder to be explicit, the deployment will fail in production
without the `-n`. It is needed to select the route because there is a
manifest that defaults it to `start-development`.

If you are deploying the "legacy" service for STS in production:

    $ cf push start-legacy -p start.jar -n start-legacy

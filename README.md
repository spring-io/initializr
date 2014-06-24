# Spring Initializr

## Prerequisites

You need Java (1.6 or better) and a bash-like shell.

If you are on a Mac and using [homebrew](http://brew.sh/), all you need to do to install it is:

    $ brew install spring-boot-cli

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

<a name="running_the_app"></a>
## Running the app locally

Use the spring command:

    $ spring run app.groovy

## Deploying to Cloud Foundry

If you are on a Mac and using [homebrew](http://brew.sh/), install the Cloud Foundry CLI:

    $ brew install cloudfoundry-cli

Alternatively, download a suitable binary for your platform from [Pivotal Web Services](https://console.run.pivotal.io/tools).

To help avoid a timeout on startup you should upload all the dependencies.
You can get those locally by running `spring grab`:

    $ spring jar app.groovy

this will create a local directory `repository/` with all the jar dependencies.
Then when you `cf push` they will be uploaded and used.

An example Cloud Foundry `manifest.yml` file is provided. You should ensure that
the application name and URL (name and host values) are suitable for your environment
before running `cf push`.

Alternatively you can jar up the app and make it executable in any environment. Care is needed with the includes and excludes:

    $ spring jar --include '+spring/**' --exclude '-**/*.jar,start.jar' start.jar app.groovy
    $ cf push -p start.jar -p start.jar -n start

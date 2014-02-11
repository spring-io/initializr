# Spring Initializr

## Prerequisites

You need Java (1.6 or better) and a bash-like shell. 

If you are on a Mac and using [homebrew](http://brew.sh/), all you must do to install it is:

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
Windoze users we recommend [cygwin](http://cygwin.org)), or you can
download the [zip file](http://start.spring.io/spring.zip) and unpack
it yourself.

<a name="running_the_app"></a>
## Running the app

Use the spring command:

    $ spring run app.groovy

## Deploying to Cloud Foundry

To help avoid a timeout on startup you should upload all the
dependencies.  You can get those locally by running the app with
`--local`:

    $ spring run --local app.groovy
    
this will create a local directory `grapes/` with all the jar
dependencies.  Then when you `cf push` they will be uploaded and used
if the app is again launched with `--local`.

#!/bin/bash

if [ -e spring -a ! -d spring ]; then
    echo "You already have a local file called 'spring' and it's not a directory"
    echo "Remove it before trying again"
    exit 1
fi

if [ -d spring ]; then
    echo "You already have a local directory called 'spring'.  Removing..."
    rm -rf spring
fi

echo "Installing Spring in local directory..."

if [ ! -e spring.zip ]; then
    wget ${host}/spring.zip
else
    echo "Using locally cached spring.zip"
fi

echo "Unpacking spring.zip"
unzip -u spring.zip
rm spring.zip

echo "To use the spring CLI:"
echo " export SPRING_HOME="`pwd`/spring
echo " export PATH="`pwd`/spring/bin':\$PATH'
echo "And (e.g.) 'spring run app.groovy'"

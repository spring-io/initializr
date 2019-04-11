FROM ubuntu:bionic-20181018

ADD setup.sh /setup.sh
RUN ./setup.sh

ENV JAVA_HOME /opt/openjdk
ENV PATH $JAVA_HOME/bin:$PATH
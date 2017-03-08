# Undertow demo app

This is a template for a lightweight web service based on [Undertow](http://undertow.io/).
Undertow is an embeddable web server written in Java and supports fully async execution based on NIO.
It is the default web server for RedHat's WildFly application server.

## Overview
While experimenting with Undertow I wanted to have a 'production-ready microservice template' containing
relevant bits of infrastructure all set up and ready so the focus can be on writing application specific code.

- Robust error handling - default error handlers setup
- Logging - using log4j2 with the ability to use graylog appender
- Monitoring - Dropwizard metrics. Various counters and health checks exposed over admin API. Ability to monitor JVM internals.
- Deployment - easy packaging in CI using Gradle. Graceful shutdown to support rolling updates.


## TODO
- Add external configuration
- Add template for integration tests
- Add RPM packaging
- Find a solution similar to sbt-native-packager's application.ini. A simple way to alter JVM configuration.

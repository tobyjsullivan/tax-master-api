# Tax Master API

This project is the API for the Tax Master application.
It provides a single access point for all client applications.

## Architecture

The API is built in Scala. The HTTP server is powered by
Akka Http and Akka actor systems power all request handling
under the hood.

## Running

The application can be run with `sbt run`.
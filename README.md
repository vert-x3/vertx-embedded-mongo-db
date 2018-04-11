# Mongo Embedded DB

[![Build Status](https://vertx.ci.cloudbees.com/buildStatus/icon?job=vert.x3-embedded-mongo-db)](https://vertx.ci.cloudbees.com/view/vert.x-3/job/vert.x3-embedded-mongo-db/)

This enables you to start up an embedded MongoDB instance by deploying a verticle. Useful for tests that
require MongoDB

To specify a specific Mongo `version`, use this configuration in the `DeploymentOptions` of the verticle:

```$java
new DeploymentOptions()
  .setConfig(new JsonObject()
     // Prefer a port that doesn't conflict with background mongod
     .put("port", 27018) 
     // Prefer a version that 100% works on Windows
     .put("version", "3.4.2"))
```

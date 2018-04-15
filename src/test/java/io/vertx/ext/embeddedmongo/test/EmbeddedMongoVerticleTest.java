/*
 * Copyright 2014 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.embeddedmongo.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.embeddedmongo.EmbeddedMongoVerticle;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class EmbeddedMongoVerticleTest extends VertxTestBase {

  public static final long MAX_WORKER_EXECUTE_TIME = 30 * 60 * 1000L;
  public static final int TEST_PORT = 7533;

  @Override
  public VertxOptions getOptions() {
    // It can take some time to download the first time!
    return new VertxOptions().setMaxWorkerExecuteTime(MAX_WORKER_EXECUTE_TIME);
  }

  @Test
  public void testEmbeddedMongo() {
    // Not really sure what to test here apart from start and stop
    vertx.deployVerticle("service:io.vertx.vertx-mongo-embedded-db", onSuccess(deploymentID -> {
      assertNotNull(deploymentID);
      undeploy(deploymentID);
    }));
    await();
  }

  @Test
  public void testConfiguredPort() {
    EmbeddedMongoVerticle mongo = new EmbeddedMongoVerticle();
    vertx.deployVerticle(mongo, createOptions(TEST_PORT), onSuccess(deploymentID -> {
      assertNotNull(deploymentID);
      assertEquals(TEST_PORT, mongo.actualPort());
      undeploy(deploymentID);
    }));
    await();
  }

  @Test
  public void testDeploysSpecificVersionWithoutErrors() {
    EmbeddedMongoVerticle mongo = new EmbeddedMongoVerticle();
    vertx.deployVerticle(mongo, createOptions(TEST_PORT, "3.0.0"), onSuccess(deploymentID -> {
      assertNotNull(deploymentID);
      assertEquals(TEST_PORT, mongo.actualPort());
      undeploy(deploymentID);
    }));
    await();
  }

  @Test
  public void testNonexistentVersionFails() {
    EmbeddedMongoVerticle mongo = new EmbeddedMongoVerticle();
    vertx.deployVerticle(mongo, createOptions(TEST_PORT, "ninethousand"), onFailure(throwable -> {
      testComplete();
    }));
    await();
  }


  @Test
  public void testRandomPort() {
    EmbeddedMongoVerticle mongo = new EmbeddedMongoVerticle();
    vertx.deployVerticle(mongo, createOptions(0), onSuccess(deploymentID -> {
      assertNotNull(deploymentID);
      assertNotSame(0, mongo.actualPort());
      undeploy(deploymentID);
    }));
    await();
  }

  @Test
  public void testRandomPortNoConfig() {
    EmbeddedMongoVerticle mongo = new EmbeddedMongoVerticle();
    vertx.deployVerticle(mongo, createEmptyOptions(), onSuccess(deploymentID -> {
      assertNotNull(deploymentID);
      assertNotSame(0, mongo.actualPort());
      undeploy(deploymentID);
    }));
    await();
  }

  private DeploymentOptions createOptions(int port) {
    return createOptions(port, "3.4.3");
  }

  private DeploymentOptions createOptions(int port, String version) {
    return createEmptyOptions().setConfig(new JsonObject().put("port", port).put("version", version));
  }

  private DeploymentOptions createEmptyOptions() {
    return new DeploymentOptions().setWorker(true);
  }

  private void undeploy(String deploymentID) {
    vertx.undeploy(deploymentID, onSuccess(v -> {
      assertNull(v);
      testComplete();
    }));
  }
}

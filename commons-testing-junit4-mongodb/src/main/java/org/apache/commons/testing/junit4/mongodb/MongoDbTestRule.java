/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.testing.junit4.mongodb;

import java.util.Objects;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Timeout;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Version.Main;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * A JUnit test rule to manage a MongoDB embedded instance.
 *
 * @author <a href="mailto:ggregory@rocketsoftware.com">Gary Gregory</a>
 */
public class MongoDbTestRule implements TestRule {

    private static final Main DEFAULT_MONGODB_VERSION = Version.Main.V3_4;

    private static final int BUILDER_TIMEOUT_MILLIS = 30000;

    /**
     * Store MongodStarter (or RuntimeConfig) in a static final field if you want to use artifact store caching (or else disable caching)
     */
    protected static final MongodStarter starter = MongodStarter.getDefaultInstance();

    public static int getBuilderTimeoutMillis() {
        return BUILDER_TIMEOUT_MILLIS;
    }

    public static MongodStarter getStarter() {
        return starter;
    }

    protected final String portSystemPropertyName;
    protected final Main mongoDbVersion;

    protected MongoClient mongoClient;
    protected MongodExecutable mongodExecutable;
    protected MongodProcess mongodProcess;

    public MongoDbTestRule(final String portSystemPropertyName) {
        this(portSystemPropertyName, DEFAULT_MONGODB_VERSION);
    }

    public MongoDbTestRule(final String portSystemPropertyName, Main mongoDbVersion) {
        this.portSystemPropertyName = Objects.requireNonNull(portSystemPropertyName, "portSystemPropertyName");
        this.mongoDbVersion = mongoDbVersion;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                final String value = Objects.requireNonNull(System.getProperty(portSystemPropertyName), "System property '" + portSystemPropertyName + "' is null");
                final int port = Integer.parseInt(value);
                mongodExecutable = starter.prepare(
                // @formatter:off
                        new MongodConfigBuilder()
                            .version(mongoDbVersion)
                            .timeout(new Timeout(BUILDER_TIMEOUT_MILLIS))
                            .net(
                                    new Net("localhost", port, Network.localhostIsIPv6()))
                            .build());
                // @formatter:on
                mongodProcess = mongodExecutable.start();
                mongoClient = new MongoClient("localhost", port);
                try {
                    base.evaluate();
                } finally {
                    if (mongodProcess != null) {
                        mongodProcess.stop();
                        mongodProcess = null;
                    }
                    if (mongodExecutable != null) {
                        mongodExecutable.stop();
                        mongodExecutable = null;
                    }
                }
            }
        };
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongodExecutable getMongodExecutable() {
        return mongodExecutable;
    }

    public MongodProcess getMongodProcess() {
        return mongodProcess;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("MongoDbTestRule [portSystemPropertyName=");
        builder.append(portSystemPropertyName);
        builder.append(", mongoClient=");
        builder.append(mongoClient);
        builder.append(", mongodExecutable=");
        builder.append(mongodExecutable);
        builder.append(", mongodProcess=");
        builder.append(mongodProcess);
        builder.append("]");
        return builder.toString();
    }

}
package com.wellnr.schooltrip.util;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

/**
 * In some cases we cannot use the default {@link org.testcontainers.containers.MongoDBContainer}
 * provided from testcontainers, E.g., when we require password authentication and proper
 * database setup. That's why we have our own implementation here.
 */
public final class MongoContainer extends GenericContainer<MongoContainer> {

    public static final int MONGO_PORT = 27017;

    public MongoContainer(String tag) {
        super("mongo:" + tag);
    }

    public static MongoContainer apply() {
        return apply("4.0.10");
    }

    public static MongoContainer apply(String tag) {
        return new MongoContainer(tag)
            .withExposedPorts(MONGO_PORT)
            .withEnv("MONGO_INITDB_DATABASE", "app")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "app")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
            .withCopyFileToContainer(MountableFile.forHostPath("./src/test/resources/init-mongo.js"), "/docker-entrypoint-initdb.d/001_users.js");
            // .withCreateContainerCmdModifier(cmd -> cmd.withName("wellnr--mongo"));
    }

    public int getPort() {
        return this.getMappedPort(MONGO_PORT);
    }

}

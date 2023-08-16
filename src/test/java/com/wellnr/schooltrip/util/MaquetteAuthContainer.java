package com.wellnr.schooltrip.util;

import org.testcontainers.containers.GenericContainer;

public final class MaquetteAuthContainer extends GenericContainer<MaquetteAuthContainer> {

    public static final int PORT = 4200;

    public MaquetteAuthContainer(String tag) {
        super("spacereg.azurecr.io/mars-auth:" + tag); // TODO: Replace with Maquette Tag.
    }

    public static MaquetteAuthContainer apply(int uiPort) {
        return apply("latest", uiPort);
    }

    public static MaquetteAuthContainer apply(String tag, int uiPort) {
        return new MaquetteAuthContainer(tag)
            .withEnv("MQ_PROXY_MODE", "dev")
            .withEnv("MQ_PROXY_URL", String.format("http://host.docker.internal:%s", uiPort))
            .withExposedPorts(PORT)
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> cmd.withName("mrs--auth"));
    }

    public String getUrl() {
        return String.format("http://%s:%s", this.getHost(), this.getMappedPort(PORT));
    }

}

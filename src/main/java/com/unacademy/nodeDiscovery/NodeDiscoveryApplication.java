package com.unacademy.nodeDiscovery;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import com.unacademy.nodeDiscovery.resources.NodeDiscoveryResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NodeDiscoveryApplication extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new NodeDiscoveryApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        JerseyGuiceUtils.install((s, serviceLocator) -> null);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        final NodeDiscoveryResource nodeDiscoveryResource = new NodeDiscoveryResource();
        environment.jersey().register(nodeDiscoveryResource);
        System.out.println("NodeDiscoveryApplication Server started");
    }
}

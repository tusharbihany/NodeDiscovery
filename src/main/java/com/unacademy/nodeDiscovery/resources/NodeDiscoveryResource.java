package com.unacademy.nodeDiscovery.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unacademy.nodeDiscovery.clients.HTTPCommandExecutor;
import com.unacademy.nodeDiscovery.models.Response;
import com.unacademy.nodeDiscovery.models.Server;
import com.unacademy.nodeDiscovery.utils.NodeDiscoveryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import scheduler.BackgroundScheduler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.Callable;

import static com.unacademy.nodeDiscovery.constants.NodeDiscoveryConstants.*;

@Path("/nodeDiscovery")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class NodeDiscoveryResource {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Timer timer;

    private volatile Set<Server> hosts;

    public NodeDiscoveryResource() {
        hosts = new HashSet<>();
        timer = new Timer();
        BackgroundScheduler.scheduleTTLCleanUp(timer, getHealthCheckerCallable());
    }

    @POST
    @Timed
    @Path("/register")
    public Response register(Server server) {
        Response response = new Response(server.toString(), SUCCESS);
        // Bootstrap if this is not the first and the only node
        if (!hosts.isEmpty()) {
            //Randomly assign a bootstrapper for this server
            Server bootstrapper = hosts.stream().skip(new Random().nextInt(hosts.size())).findFirst().orElse(null);
            try {
                response = NodeDiscoveryUtils.onboard(server, bootstrapper);
            } catch (Exception e) {
                return new Response(server.toString(), e.getMessage());
            }
            hosts.add(server);
            // Parallelly inform the other cluster nodes to update this new server
            NodeDiscoveryUtils.updateCluster(hosts);
        } else
            hosts.add(server);
        return response;
    }

    private Callable<Void> getHealthCheckerCallable() {
        Callable<Void> requiredCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final URIBuilder uriBuilder = new URIBuilder();
                // Healthcheck the current nodes
                uriBuilder.setPath(HEALTHCHECK);
                Set<Server> unhealthyNodes = new HashSet<>();
                hosts.parallelStream().forEach(node -> {
                    try {
                        Response response = HTTPCommandExecutor.executeGetRequest(uriBuilder, node);
                        if (response == null || !response.getValue().equals(SUCCESS))
                            unhealthyNodes.add(node);
                    } catch (Exception e) {
                        unhealthyNodes.add(node);
                        log.error(e.getMessage());
                    }
                });
                // Remove and deregister all the unhealthy nodes
                hosts.removeAll(unhealthyNodes);
                uriBuilder.setPath(REPLICA_API + DEREGISTER_API);
                hosts.parallelStream().forEach(node -> {
                    try {
                        unhealthyNodes.stream().forEach(unhealthyNode -> {
                            try {
                                HTTPCommandExecutor.executePostRequest(uriBuilder, node, objectMapper.writeValueAsString(unhealthyNode));
                            } catch (Exception e) {
                                // TODO : Handle exception
                                log.error("Couldn't update " + e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        // TODO : Handle exception
                        log.error("Couldn't update " + e.getMessage());
                    }
                });
                return null;
            }
        };
        return requiredCallable;
    }

}
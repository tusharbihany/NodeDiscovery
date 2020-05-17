package com.unacademy.nodeDiscovery.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unacademy.nodeDiscovery.clients.HTTPCommandExecutor;
import com.unacademy.nodeDiscovery.models.Response;
import com.unacademy.nodeDiscovery.models.Server;
import org.apache.http.client.utils.URIBuilder;

import java.util.Set;

import static com.unacademy.nodeDiscovery.constants.NodeDiscoveryConstants.*;

public class NodeDiscoveryUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Response onboard(Server server, Server bootstrapper) throws Exception {
        Response response = null;
        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(REPLICA_API + ONBOARD_API);
        try {
            response = HTTPCommandExecutor.executePostRequest(uriBuilder, bootstrapper, objectMapper.writeValueAsString(server));
        } catch (Exception e) {
            return new Response(server.toString(), e.getMessage());
        }
        if(response == null || !response.getValue().equals(SUCCESS))
            throw new Exception("Couldn't onboard " +response.getValue());
        return response;
    }

    public static Response updateCluster(Set<Server> hosts) {
        final Response[] response = {null};
        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(REPLICA_API + UPDATE_API);
        hosts.stream().forEach(node -> {
            try {
                Response httpResponse = HTTPCommandExecutor.executePostRequest(uriBuilder, node, objectMapper.writeValueAsString(hosts));
                if (httpResponse == null || !httpResponse.getValue().equals(SUCCESS))
                    response[0] = new Response(node.toString(), httpResponse.getValue());
            } catch (Exception e) {
                response[0] = new Response(node.toString(), e.getMessage());
            }
        });
        return response[0];
    }
}

package com.unacademy.nodeDiscovery.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unacademy.nodeDiscovery.constants.NodeDiscoveryConstants;
import com.unacademy.nodeDiscovery.models.Response;
import com.unacademy.nodeDiscovery.models.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static com.unacademy.nodeDiscovery.constants.NodeDiscoveryConstants.HTTP;

/*
  Executes HTTP calls
 */
@Slf4j
public class HTTPCommandExecutor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Response executePostRequest(URIBuilder uriBuilder, Server server, String postBody) throws Exception {

        uriBuilder.setScheme(HTTP).setHost(server.getIp()).setPort(server.getPort());
        HttpPost post = new HttpPost(uriBuilder.build());
        post.setEntity(new StringEntity(postBody));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        return executeHttpRequest(post);
    }

    public static Response executeGetRequest(URIBuilder uriBuilder, Server server) throws Exception {

        uriBuilder.setScheme(HTTP).setHost(server.getIp()).setPort(server.getPort());
        HttpGet get = new HttpGet(uriBuilder.build());
        return executeHttpRequest(get);
    }

    private static Response executeHttpRequest(HttpUriRequest httpUriRequest) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error(response.toString());
                return null;
            }
            Response httpResponse = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<Response>() {
            });
            log.info(httpResponse.toString());
            return httpResponse;
        }
    }

}

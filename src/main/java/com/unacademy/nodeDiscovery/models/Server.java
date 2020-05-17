package com.unacademy.nodeDiscovery.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@ToString
/*
   Umbrella over Address details
 */
public class Server {
    private String ip;
    private int port;
}

package com.unacademy.nodeDiscovery.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
/*
   Key-Value based HTTP Response placeholder.
 */
public class Response {

    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private String value;
}

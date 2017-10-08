package com.company.vote;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {
    @JsonProperty("name")
    public String name;
    @JsonProperty("value")
    public int value;
    @JsonProperty("round")
    public String round;
    @JsonProperty("voteTime")
    public Date voteTime;

    public Vote() {
    }
}

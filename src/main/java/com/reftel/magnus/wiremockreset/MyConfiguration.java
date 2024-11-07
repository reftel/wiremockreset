package com.reftel.magnus.wiremockreset;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;

public class MyConfiguration extends Configuration {
    @Valid
    @JsonProperty
    public URI uri;
}

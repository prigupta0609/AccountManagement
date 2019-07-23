package com.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class ApplicationConfiguration extends Configuration {

    @NotEmpty
    private String dummyDataLocation;

    @JsonProperty
    public String getDummyDataLocation() {
        return dummyDataLocation;
    }

}

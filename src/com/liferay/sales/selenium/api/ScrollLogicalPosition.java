package com.liferay.sales.selenium.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ScrollLogicalPosition {
    @JsonProperty("center" )
    CENTER,
    @JsonProperty("end" )
    END,
    @JsonProperty("nearest" )
    NEAREST,
    @JsonProperty("start" )
    START
}

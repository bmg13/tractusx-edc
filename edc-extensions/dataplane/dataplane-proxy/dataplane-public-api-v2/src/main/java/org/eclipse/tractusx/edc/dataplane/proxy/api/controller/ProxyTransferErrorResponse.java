/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - Initial implementation
 *
 */

package org.eclipse.tractusx.edc.dataplane.proxy.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * Error response returned when transfer request failed.
 */
public class ProxyTransferErrorResponse {
    private final List<String> errors;
    private final Object content;

    public ProxyTransferErrorResponse(
            @JsonProperty("errors") List<String> errors,
            @JsonProperty("content") Object content) {
        this.errors = errors;
        this.content = content;
    }

    @JsonProperty("errors")
    public List<String> getErrors() {
        return errors;
    }

    @JsonProperty("content")
    public Object getContent() {
        return content;
    }
}
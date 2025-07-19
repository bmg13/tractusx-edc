/********************************************************************************
 * Copyright (c) 2025 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.dataplane.proxy.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Error response returned when transfer request failed.
 */
public class ProxyTransferErrorResponse { // TODO: maybe remove class
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
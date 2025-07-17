/**
 * Copyright (c) 2025 Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
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
 **/

package org.eclipse.edc.connector.dataplane.http.params.decorators;

import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.connector.dataplane.http.spi.HttpParamsDecorator;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParams;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;

import java.util.Optional;

public class BaseSinkHttpParamsDecorator implements HttpParamsDecorator {
    private static final String DEFAULT_METHOD = "POST";

    @Override
    public HttpRequestParams.Builder decorate(DataFlowStartMessage request, HttpDataAddress address, HttpRequestParams.Builder params) {
        var method = Optional.ofNullable(address.getMethod()).orElse(DEFAULT_METHOD);
        params.method(method);
        params.path(address.getPath());
        params.queryParams(null);
        params.proxyOriginalResponse(Boolean.parseBoolean(address.getproxyOriginalResponse()));
        Optional.ofNullable(address.getContentType())
                .ifPresent(contentType -> {
                    params.contentType(contentType);
                    params.body(null);
                });
        params.nonChunkedTransfer(address.getNonChunkedTransfer());
        return params;
    }

}

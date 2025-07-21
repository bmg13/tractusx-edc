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

package org.eclipse.edc.connector.dataplane.http.params;

import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.BODY;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.MEDIA_TYPE;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.METHOD;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.PATH;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.QUERY_PARAMS;
import static org.eclipse.edc.spi.types.domain.transfer.FlowType.PULL;
import static org.mockito.Mockito.mock;

class HttpRequestParamsProviderImplSourceTest {

    private final HttpRequestParamsProvider provider = new HttpRequestParamsProviderImpl(mock(), mock());

    @Test
    void shouldMapDataFlowRequestToHttpRequest() {
        var source = HttpDataAddress.Builder.newInstance()
                .baseUrl("http://source")
                .method("test-method")
                .path("test-path")
                .queryParams("foo=bar")
                .contentType("test/content-type")
                .nonChunkedTransfer(true)
                .build();
        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .destinationDataAddress(dummyHttpDataAddress())
                .build();

        var params = provider.provideSourceParams(dataFlowRequest);

        assertThat(params.getMethod()).isEqualTo("test-method");
        assertThat(params.getBaseUrl()).isEqualTo("http://source");
        assertThat(params.getPath()).isEqualTo("test-path");
        assertThat(params.getQueryParams()).isEqualTo("foo=bar");
        assertThat(params.getBody()).isEqualTo(null);
        assertThat(params.getContentType()).isEqualTo("test/content-type");
        assertThat(params.isNonChunkedTransfer()).isFalse(); // always false for source
    }

    @Test
    void shouldMapDataFlowRequestToHttpRequest_proxyDataFlowRequest() {
        var source = HttpDataAddress.Builder.newInstance()
                .baseUrl("http://source")
                .proxyMethod("true")
                .proxyPath("true")
                .proxyQueryParams("true")
                .proxyBody("true")
                .queryParams("foo=bar")
                .contentType("test/content-type")
                .nonChunkedTransfer(true)
                .build();
        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .flowType(PULL)
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .transferType(new TransferType("HttpData", PULL))
                .properties(Map.of(
                        METHOD, "proxy-method",
                        PATH, "proxy-path",
                        QUERY_PARAMS, "bar=baz",
                        MEDIA_TYPE, "proxy/content-type",
                        BODY, "test body"
                ))
                .build();

        var params = provider.provideSourceParams(dataFlowRequest);

        assertThat(params.getMethod()).isEqualTo("proxy-method");
        assertThat(params.getBaseUrl()).isEqualTo("http://source");
        assertThat(params.getPath()).isEqualTo("proxy-path");
        assertThat(params.getQueryParams()).isEqualTo("foo=bar&bar=baz");
        assertThat(params.getBody()).isEqualTo("test body");
        assertThat(params.getContentType()).isEqualTo("proxy/content-type");
        assertThat(params.isNonChunkedTransfer()).isFalse(); // always false for source
    }

    @Test
    void shouldMapDataFlowRequestToHttpRequest_withDefaultValues() {
        var source = HttpDataAddress.Builder.newInstance()
                .baseUrl("http://source")
                .build();
        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .destinationDataAddress(dummyHttpDataAddress())
                .build();

        var params = provider.provideSourceParams(dataFlowRequest);

        assertThat(params.getMethod()).isEqualTo("GET");
        assertThat(params.getBaseUrl()).isEqualTo("http://source");
        assertThat(params.getPath()).isEqualTo(null);
        assertThat(params.getQueryParams()).isEqualTo(null);
        assertThat(params.getBody()).isEqualTo(null);
        assertThat(params.getContentType()).isEqualTo("application/octet-stream");
    }

    @Test
    void shouldThrowException_whenProxyMethodIsMissingAndTransferTypeIsPull() {
        var source = HttpDataAddress.Builder.newInstance()
                .baseUrl("http://source")
                .proxyMethod("true")
                .path("test-path")
                .queryParams("foo=bar")
                .contentType("test/content-type")
                .nonChunkedTransfer(true)
                .build();
        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .flowType(PULL)
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .transferType(new TransferType("HttpData", PULL))
                .build();

        assertThatExceptionOfType(EdcException.class).isThrownBy(() -> provider.provideSourceParams(dataFlowRequest));
    }

    @Test
    void shouldUseSourceMethod_whenProxyMethodIsMissingAndTransferTypeIsNotPull() {
        var source = HttpDataAddress.Builder.newInstance()
                .baseUrl("http://source")
                .proxyMethod("true")
                .path("test-path")
                .queryParams("foo=bar")
                .contentType("test/content-type")
                .nonChunkedTransfer(true)
                .method("POST")
                .build();
        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .destinationDataAddress(dummyHttpDataAddress())
                .build();

        var params = provider.provideSourceParams(dataFlowRequest);

        assertThat(params.getMethod()).isEqualTo("POST");
    }

    private HttpDataAddress dummyHttpDataAddress() {
        return HttpDataAddress.Builder.newInstance().baseUrl("http://dummy").build();
    }
}

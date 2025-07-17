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

package org.eclipse.tractusx.edc;

import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamFailure;
import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult;
import org.eclipse.edc.spi.result.Result;

/**
 * Specialized {@link Result} class to indicate the success or failure opening a source stream.
 *
 * @param <T> The type of content
 */
public class ProxyStreamResult<T> extends StreamResult<T> {

    private final boolean isProxyResponse;
    private final String mediaType;
    private final String statusCode;

    // TODO: maybe builder
    private ProxyStreamResult(T content, String mediaType, String statusCode, StreamFailure failure, boolean isProxyResponse) {
        super(content, failure);
        this.mediaType = mediaType;
        this.statusCode = statusCode;
        this.isProxyResponse = isProxyResponse;
    }

    private ProxyStreamResult(T content, String mediaType, String statusCode, boolean isProxyResponse) {
        super(content, null);
        this.mediaType = mediaType;
        this.statusCode = statusCode;
        this.isProxyResponse = isProxyResponse;
    }

    public static <T> StreamResult<T> success(
            T content, String mediaType, String statusCode, boolean isProxyResponse) {
        return new ProxyStreamResult<>(content, mediaType, statusCode, isProxyResponse);
    }

    public static <T> StreamResult<T> failure(
            T content, String mediaType, String statusCode, StreamFailure failure, boolean isProxyResponse) {
        return new ProxyStreamResult<>(content, mediaType, statusCode, failure, isProxyResponse);
    }

    public boolean isProxyResponse() {
        return isProxyResponse;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getStatusCode() {
        return statusCode;
    }
}

/*
 *  Copyright (c) 2025 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.edc;

import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamFailure;

import java.io.InputStream;
import java.util.List;

public class ProxyStreamFailure extends StreamFailure {
    private final InputStream content;
    private final String mediaType;
    private final String statusCode;
    // TODO: private final boolean isProxyResponse;

    public ProxyStreamFailure(
            List<String> messages,
            Reason reason,
            InputStream content,
            String mediaType,
            String statusCode) {
        super(messages, reason);
        this.content = content;
        this.statusCode = statusCode;
        this.mediaType = mediaType;
    }

    public InputStream getContent() {
        return content;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getStatusCode() {
        return statusCode;
    }
}

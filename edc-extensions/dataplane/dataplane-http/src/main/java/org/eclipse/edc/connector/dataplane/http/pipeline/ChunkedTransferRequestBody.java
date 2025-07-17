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

package org.eclipse.edc.connector.dataplane.http.pipeline;

import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Streams content into an OK HTTP buffered sink in chunks.
 * <p>
 * Due to OkHttp implementation an extra header will be created (no-overridable) Transfer-Encoding with value chunked
 *
 * @see <a href="https://github.com/square/okhttp/blob/master/docs/features/calls.md">OkHttp Dcoumentation</a>
 */
public class ChunkedTransferRequestBody extends AbstractTransferRequestBody {

    private final Supplier<InputStream> bodySupplier;

    public ChunkedTransferRequestBody(Supplier<InputStream> bodySupplier, String contentType) {
        super(contentType);
        this.bodySupplier = bodySupplier;
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        try (var os = sink.outputStream(); var is = bodySupplier.get()) {
            is.transferTo(os);
        }
    }
}

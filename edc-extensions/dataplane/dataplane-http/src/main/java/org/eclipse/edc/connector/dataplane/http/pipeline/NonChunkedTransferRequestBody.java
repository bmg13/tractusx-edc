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
 * Writes content into an OK HTTP buffered sink.
 * <p>
 * The extra Transfer-Encoding is not created because the Content-Length is provided upfront.
 * Note that means that the all content is loaded into memory, so this method can be used for small files (up to 50MB) for e.g.
 *
 * @see <a href="https://github.com/square/okhttp/blob/master/docs/features/calls.md">OkHttp Dcoumentation</a>
 */
public class NonChunkedTransferRequestBody extends AbstractTransferRequestBody {

    private byte[] bytes;

    public NonChunkedTransferRequestBody(Supplier<InputStream> contentSupplier, String contentType) {
        super(contentType);
        try (var is = contentSupplier.get()) {
            this.bytes = is.readAllBytes();
        } catch (IOException e) {
            //do nothing
        }
    }

    @Override
    public long contentLength() {
        return bytes == null ? 0 : bytes.length;
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        if (bytes == null) {
            return;
        }

        try (var os = sink.outputStream()) {
            os.write(bytes);
        }
    }
}


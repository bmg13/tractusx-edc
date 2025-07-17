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

package org.eclipse.edc.connector.dataplane.http.spi;

import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;

/**
 * Permit to register {@link HttpRequestParams} decorators, that are used to enrich the HTTP request with
 * information taken from {@link DataFlowStartMessage}
 */
public interface HttpRequestParamsProvider {

    /**
     * Register source decorator
     */
    void registerSourceDecorator(HttpParamsDecorator decorator);

    /**
     * Register sink decorator
     */
    void registerSinkDecorator(HttpParamsDecorator decorator);

    /**
     * Provide HTTP request params for HttpDataSource
     */
    HttpRequestParams provideSourceParams(DataFlowStartMessage request);

    /**
     * Provide HTTP request params for HttpDataSink
     */
    HttpRequestParams provideSinkParams(DataFlowStartMessage request);
}

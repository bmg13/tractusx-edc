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

package org.eclipse.edc.connector.dataplane.http;

import org.eclipse.edc.connector.dataplane.http.params.HttpRequestFactory;
import org.eclipse.edc.connector.dataplane.http.params.HttpRequestParamsProviderImpl;
import org.eclipse.edc.connector.dataplane.http.pipeline.HttpDataSinkFactory;
import org.eclipse.edc.connector.dataplane.http.pipeline.HttpDataSourceFactory;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataTransferExecutorServiceContainer;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

/**
 * Provides support for reading data from an HTTP endpoint and sending data to an HTTP endpoint.
 */
@Provides(HttpRequestParamsProvider.class)
@Extension(value = DataPlaneHttpExtension.NAME)
public class DataPlaneHttpExtension implements ServiceExtension {
    public static final String NAME = "Data Plane HTTP";
    private static final int DEFAULT_PARTITION_SIZE = 5;

    @Setting(description = "Number of partitions for parallel message push in the HttpDataSink", defaultValue = DEFAULT_PARTITION_SIZE + "", key = "edc.dataplane.http.sink.partition.size")
    private int partitionSize;

    @Inject
    private EdcHttpClient httpClient;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private DataTransferExecutorServiceContainer executorContainer;

    @Inject
    private Vault vault;

    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        var paramsProvider = new HttpRequestParamsProviderImpl(vault, typeManager);
        context.registerService(HttpRequestParamsProvider.class, paramsProvider);

        var httpRequestFactory = new HttpRequestFactory();

        var sourceFactory = new HttpDataSourceFactory(httpClient, paramsProvider, monitor, httpRequestFactory);
        pipelineService.registerFactory(sourceFactory);

        var sinkFactory = new HttpDataSinkFactory(httpClient, executorContainer.getExecutorService(), partitionSize, monitor, paramsProvider, httpRequestFactory);
        pipelineService.registerFactory(sinkFactory);
    }

}

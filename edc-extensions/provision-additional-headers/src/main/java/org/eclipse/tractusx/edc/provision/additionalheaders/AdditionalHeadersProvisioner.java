/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.edc.provision.additionalheaders;

import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.connector.dataplane.spi.provision.ProvisionResource;
import org.eclipse.edc.connector.dataplane.spi.provision.ProvisionedResource;
import org.eclipse.edc.connector.dataplane.spi.provision.Provisioner;
import org.eclipse.edc.spi.response.StatusResult;

import java.util.concurrent.CompletableFuture;

import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.BPN_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.CONTRACT_AGREEMENT_ID_HEADER;

public class AdditionalHeadersProvisioner implements Provisioner {

    @Override
    public String supportedType() {
        return AdditionalHeadersSchema.TYPE;
    }

    @Override
    public CompletableFuture<StatusResult<ProvisionedResource>> provision(ProvisionResource provisionResource) {
        var address =
                HttpDataAddress.Builder.newInstance()
                        .copyFrom(provisionResource.getDataAddress())
                        .type(provisionResource.getType())
                        .addAdditionalHeader(CONTRACT_AGREEMENT_ID_HEADER,
                                provisionResource.getProperties().get(CONTRACT_AGREEMENT_ID_HEADER).toString())
                        .addAdditionalHeader(BPN_HEADER,
                                provisionResource.getProperties().get(BPN_HEADER).toString())
                        .build();

        var provisionedResource = ProvisionedResource.Builder.from(provisionResource)
                .id(provisionResource.getId())
                .flowId(provisionResource.getFlowId())
                .dataAddress(address)
                //.properties(provisionResource.getProperties())
                .properties(provisionResource.getDataAddress().getProperties())
                .build();
        var result = StatusResult.success(provisionedResource);
        return CompletableFuture.completedFuture(result);
    }
}

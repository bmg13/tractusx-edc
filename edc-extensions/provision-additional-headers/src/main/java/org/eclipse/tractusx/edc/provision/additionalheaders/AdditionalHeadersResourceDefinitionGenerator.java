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

import org.eclipse.edc.connector.dataplane.spi.DataFlow;
import org.eclipse.edc.connector.dataplane.spi.provision.ProvisionResource;
import org.eclipse.edc.connector.dataplane.spi.provision.ResourceDefinitionGenerator;
import org.eclipse.tractusx.edc.spi.identity.mapper.BdrsClient;

import java.util.UUID;

import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.BPN_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.CONTRACT_AGREEMENT_ID_HEADER;
import static org.eclipse.tractusx.edc.spi.identity.mapper.BdrsConstants.DID_PREFIX;

class AdditionalHeadersResourceDefinitionGenerator implements ResourceDefinitionGenerator {

    private final BdrsClient bdrsClient;

    AdditionalHeadersResourceDefinitionGenerator(BdrsClient bdrsClient) {
        this.bdrsClient = bdrsClient;
    }

    @Override
    public String supportedType() {
        return AdditionalHeadersSchema.TYPE;
    }

    @Override
    public ProvisionResource generate(DataFlow dataFlow) {
        var identity = dataFlow.getParticipantId();
        if (identity != null && identity.startsWith(DID_PREFIX)) {
            identity = bdrsClient.resolveBpn(identity);
        }
        return ProvisionResource.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .flowId(dataFlow.getId())
                .type(AdditionalHeadersSchema.TYPE)
                .dataAddress(dataFlow.getSource())
                .property(BPN_HEADER, identity)
                .property(CONTRACT_AGREEMENT_ID_HEADER, dataFlow.getAgreementId())
                .build();
    }
}

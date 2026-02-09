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

import java.util.HashMap;

import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.BPN_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.CONTRACT_AGREEMENT_ID_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.TYPE;

class AdditionalHeadersResourceDefinitionGenerator implements ResourceDefinitionGenerator {

    @Override
    public String supportedType() {
        return TYPE;
    }

    @Override
    public ProvisionResource generate(DataFlow dataFlow) {
        var properties = new HashMap<String, Object>();
        properties.put(BPN_HEADER, dataFlow.getParticipantId());
        properties.put(CONTRACT_AGREEMENT_ID_HEADER, dataFlow.getAgreementId());
        return ProvisionResource.Builder.newInstance()
                .flowId(dataFlow.getId())
                .type(TYPE)
                .dataAddress(dataFlow.getSource())
                .properties(properties)
                .build();
    }
}

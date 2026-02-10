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
import org.eclipse.edc.connector.dataplane.spi.DataFlow;
import org.eclipse.edc.connector.dataplane.spi.provision.ProvisionResource;
import org.eclipse.edc.connector.dataplane.spi.provision.ResourceDefinitionGenerator;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.eclipse.tractusx.edc.spi.identity.mapper.BdrsClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.eclipse.edc.connector.dataplane.spi.DataFlowStates.STARTED;
import static org.eclipse.edc.spi.types.domain.transfer.FlowType.PULL;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.BPN_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.CONTRACT_AGREEMENT_ID_HEADER;
import static org.mockito.Mockito.mock;

class AdditionalHeadersResourceDefinitionGeneratorTest {

    private final BdrsClient bdrsClient = mock();
    private final ResourceDefinitionGenerator generator = new AdditionalHeadersResourceDefinitionGenerator(bdrsClient);

    @Test
    void supportType_shouldReturnFalseForNotHttpDataAddresses() {
        var dataAddress = DataAddress.Builder.newInstance().type("any").build();
        assertThat(generator.supportedType()).isNotEqualTo(dataAddress.getType());
    }

    @Test
    void supportType_shouldReturnTrueForNotHttpDataAddresses() {
        var dataAddress = DataAddress.Builder.newInstance().type("HttpData").build();
        assertThat(generator.supportedType()).isEqualTo(dataAddress.getType());
    }

    @Test
    void shouldCreateProvisionResourceWithDataAddress() {
        var dataAddress = HttpDataAddress.Builder.newInstance().baseUrl("http://any").build();
        var dataFlow = DataFlow.Builder.newInstance()
                .state(STARTED.code())
                .participantId("bpn")
                .agreementId("contractId")
                .source(dataAddress)
                .transferType(new TransferType("destination", PULL))
                .build();

        var result = generator.generate(dataFlow);

        assertThat(result)
                .asInstanceOf(type(ProvisionResource.class))
                .satisfies(resourceDefinition -> {
                    assertThat(resourceDefinition.getDataAddress())
                            .extracting(address -> HttpDataAddress.Builder.newInstance().copyFrom(address).build())
                            .extracting(HttpDataAddress::getBaseUrl)
                            .isEqualTo("http://any");
                    assertThat(resourceDefinition.getProperties().containsKey(CONTRACT_AGREEMENT_ID_HEADER)).isTrue();
                    assertThat(resourceDefinition.getProperties().get(CONTRACT_AGREEMENT_ID_HEADER)).isEqualTo("contractId");
                    assertThat(resourceDefinition.getProperties().containsKey(BPN_HEADER)).isTrue();
                    assertThat(resourceDefinition.getProperties().get(BPN_HEADER)).isEqualTo("bpn");
                });
    }
}

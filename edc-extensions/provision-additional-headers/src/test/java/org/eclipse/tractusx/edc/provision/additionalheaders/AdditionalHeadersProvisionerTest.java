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
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.BPN_HEADER;
import static org.eclipse.tractusx.edc.provision.additionalheaders.AdditionalHeadersSchema.CONTRACT_AGREEMENT_ID_HEADER;

class AdditionalHeadersProvisionerTest {

    private final AdditionalHeadersProvisioner provisioner = new AdditionalHeadersProvisioner();

    @Test
    void supportType_shouldReturnFalseForNotHttpDataAddresses() {
        var dataAddress = DataAddress.Builder.newInstance().type("any").build();
        assertThat(provisioner.supportedType()).isNotEqualTo(dataAddress.getType());
    }

    @Test
    void supportType_shouldReturnTrueForNotHttpDataAddresses() {
        var dataAddress = DataAddress.Builder.newInstance().type("HttpData").build();
        assertThat(provisioner.supportedType()).isEqualTo(dataAddress.getType());
    }

    @Test
    void shouldAddAdditionalHeaders() {
        var address = HttpDataAddress.Builder.newInstance().baseUrl("http://any").build();
        var bpn = "bpn";
        var contractId = "contractId";
        var properties = new HashMap<String, Object>();
        properties.put(BPN_HEADER, bpn);
        properties.put(CONTRACT_AGREEMENT_ID_HEADER, contractId);
        var provisionResource = ProvisionResource.Builder.newInstance()
                .flowId(UUID.randomUUID().toString())
                .type(AdditionalHeadersSchema.TYPE)
                .dataAddress(address)
                .properties(properties)
                .build();

        var result = provisioner.provision(provisionResource);
        assertThat(result)
                .succeedsWithin(5, SECONDS)
                .matches(StatusResult::succeeded)
                .extracting(StatusResult::getContent)
                .asInstanceOf(type(ProvisionedResource.class))
                .extracting(ProvisionedResource::getDataAddress)
                .extracting(a -> HttpDataAddress.Builder.newInstance().copyFrom(a).build())
                .extracting(HttpDataAddress::getAdditionalHeaders)
                .asInstanceOf(map(String.class, String.class))
                .containsEntry(CONTRACT_AGREEMENT_ID_HEADER, contractId)
                .containsEntry(BPN_HEADER, bpn);
    }
}

/*******************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2026 Cofinity-X GmbH
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
 ******************************************************************************/

package org.eclipse.tractusx.edc.compatibility.tests.fixtures;

public class LegacyRemoteParticipant extends RemoteParticipant {

    public static class Builder extends RemoteParticipant.Builder {

        protected Builder() {
            super(new LegacyRemoteParticipant());
        }

        public static Builder newInstance() {
            return new Builder();
        }


        // Override parent methods to return the correct builder type
        @Override
        public Builder name(String name) {
            super.name(name);
            return this;
        }

        @Override
        public Builder id(String id) {
            super.id(id);
            return this;
        }

        @Override
        public Builder stsUri(org.eclipse.edc.junit.utils.LazySupplier<java.net.URI> stsUri) {
            super.stsUri(stsUri);
            return this;
        }

        @Override
        public Builder did(String did) {
            super.did(did);
            return this;
        }

        @Override
        public Builder bpn(String bpn) {
            super.bpn(bpn);
            return this;
        }

        @Override
        public Builder trustedIssuer(String trustedIssuer) {
            super.trustedIssuer(trustedIssuer);
            return this;
        }

        /**
         * Sets the protocol version and path for the remote participant.
         * This is required for compatibility testing with specific DSP versions.
         *
         * @param protocol the protocol name (e.g., "dataspace-protocol-http:2025-1")
         * @param path     the protocol path (e.g., "/api/v1/dsp/2025-1")
         * @return this builder
         */
        @Override
        public Builder protocol(String protocol, String path) {
            super.protocol(protocol, path);
            return this;
        }

        @Override
        public LegacyRemoteParticipant build() {
            super.build();
            return (LegacyRemoteParticipant) participant;
        }
    }
}

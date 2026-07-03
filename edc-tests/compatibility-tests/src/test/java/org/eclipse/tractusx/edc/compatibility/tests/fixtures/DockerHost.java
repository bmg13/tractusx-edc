
/*******************************************************************************
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

import org.eclipse.tractusx.edc.tests.participant.TractusxParticipantBase;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Helper to make the compatibility tests work on both Linux CI and macOS.
 * <p>
 * On Linux the containerized {@code remote-connector} is started with {@code --network=host}, so it
 * shares the host's loopback: everything can talk to everything via {@code localhost}. On macOS,
 * Docker Desktop runs containers inside a Linux VM, so {@code --network=host} does <em>not</em> give
 * the container access to the Mac host's {@code localhost} (that is where the in-JVM connector,
 * identity hub, WireMock and the PostgreSQL test container's published port live). The container must
 * instead use {@code host.docker.internal}, which Docker Desktop resolves to the Mac host.
 * <p>
 * Because a {@code did:web} identifier and the advertised connector/callback addresses are resolved
 * by <em>both</em> the container and the in-JVM runtimes, a single host name has to work on both
 * sides. We therefore use {@code host.docker.internal} everywhere on macOS and require it to resolve
 * to the loopback address on the host as well (see {@link #requireResolvable()}).
 */
public final class DockerHost {

    public static final String HOST_DOCKER_INTERNAL = "host.docker.internal";
    public static final boolean IS_MAC_OS = System.getProperty("os.name", "").toLowerCase().contains("mac");

    private DockerHost() {
    }

    /**
     * Enables docker-host address substitution for the whole test by setting
     * {@link TractusxParticipantBase#DOCKER_HOST_PROPERTY} when running on macOS. No-op otherwise.
     */
    public static void enable() {
        if (IS_MAC_OS) {
            System.setProperty(TractusxParticipantBase.DOCKER_HOST_PROPERTY, HOST_DOCKER_INTERNAL);
        }
    }

    /**
     * The host name that both the container and the host must use to reach host-bound services:
     * {@code host.docker.internal} on macOS, {@code localhost} elsewhere.
     */
    public static String host() {
        return IS_MAC_OS ? HOST_DOCKER_INTERNAL : "localhost";
    }

    /**
     * Rewrites a {@code localhost} URL to use {@link #host()}. No-op on non-macOS.
     */
    public static String adapt(String url) {
        if (!IS_MAC_OS || url == null) {
            return url;
        }
        return url.replace("//localhost:", "//" + HOST_DOCKER_INTERNAL + ":");
    }

    /**
     * Fails fast on macOS if {@code host.docker.internal} does not resolve on the host. In that case
     * the in-JVM runtimes and the test client cannot reach the addresses that are shared with the
     * container (e.g. {@code did:web} documents), which would otherwise surface as obscure
     * {@code UnknownHostException}s deep inside the connector.
     */
    public static void requireResolvable() {
        if (!IS_MAC_OS) {
            return;
        }
        try {
            InetAddress.getByName(HOST_DOCKER_INTERNAL);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(
                    "'%s' does not resolve on this host. On macOS the compatibility tests need it to point to the loopback address. Add the following line to /etc/hosts and retry:%n%n    127.0.0.1 %s%n"
                            .formatted(HOST_DOCKER_INTERNAL, HOST_DOCKER_INTERNAL), e);
        }
    }
}

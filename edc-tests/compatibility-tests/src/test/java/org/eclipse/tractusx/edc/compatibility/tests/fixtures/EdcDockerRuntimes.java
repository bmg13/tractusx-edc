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

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EdcDockerRuntimes {

    STABLE_CONNECTOR("connector-stable:latest");

    /**
     * Upper bound for the container to log its "ready" message. Only a cap: the wait returns as soon
     * as the line appears, so Linux CI (a few seconds) is unaffected, while slower Docker Desktop for
     * Mac boots get enough headroom.
     */
    private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(5);

    private final String image;

    EdcDockerRuntimes(String image) {
        this.image = image;
    }

    public GenericContainer<?> create(String name, Map<String, String> env) {
        var container = new GenericContainer<>(image)
                .withCreateContainerCmdModifier(cmd -> cmd.withName(name))
                .withLogConsumer(it -> System.out.printf("[%s] %s%n", name, it.getUtf8StringWithoutLineEnding()))
                .waitingFor(Wait.forLogMessage(".*Runtime .* ready.*", 1).withStartupTimeout(STARTUP_TIMEOUT))
                .withEnv(env)
                .withEnv("JAVA_TOOL_OPTIONS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
                .withLogConsumer(it -> System.out.printf("[%s] %s%n", name, it.getUtf8StringWithoutLineEnding()));

        if (DockerHost.IS_MAC_OS) {
            container.withNetworkMode("bridge")
                    .withExtraHost(DockerHost.HOST_DOCKER_INTERNAL, "host-gateway")
                    .withCreateContainerCmdModifier(cmd -> {
                        var ports = fixedPorts(env);
                        var bindings = ports.stream()
                                .map(port -> new PortBinding(Ports.Binding.bindPort(port), ExposedPort.tcp(port)))
                                .toArray(PortBinding[]::new);
                        cmd.withExposedPorts(ports.stream().map(ExposedPort::tcp).toList());
                        Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(bindings);
                    });
        } else {
            container.withNetworkMode("host");
        }

        return container;
    }

    /**
     * The connector's HTTP ports (protocol, management, public) that must be reachable from the host.
     * They are published on the same port number on the host so advertised addresses resolve
     * identically on the container and the host.
     */
    private static List<Integer> fixedPorts(Map<String, String> env) {
        var ports = Stream.of("web.http.protocol.port", "web.http.management.port", "web.http.public.port")
                .map(env::get)
                .filter(Objects::nonNull)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        ports.add(5005); // Add debug port
        return ports;
    }
}

/**
 * Copyright (c) 2022 Microsoft Corporation - initial API and implementation
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - improvements
 * Copyright (c) 2024 Mercedes-Benz Tech Innovation GmbH - publish public api context into dedicated swagger hub page
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

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.edc.spi.http)
    api(libs.edc.spi.dataplane.dataplane)
    api(libs.edc.spi.dataplane.http)
    //api(project(":edc-extensions:dataplane:dataplane-http-spi"))
    implementation(libs.edc.lib.util )
    implementation(project(":spi:core-spi"))
    implementation(libs.edc.dpf.http)
    //implementation(libs.edc.dpf.core)
    //implementation(libs.edc.cp.api.client )
    implementation(project(":edc-extensions:non-finite-provider-push:non-finite-provider-push-core"))

    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.core.runtime)
    //testImplementation(libs.edc.dpf.core)
    testImplementation(libs.edc.ext.jsonld)
    testImplementation(libs.restAssured)
    testImplementation(libs.netty.mockserver)

    testImplementation(testFixtures(libs.edc.lib.http))
    testImplementation(testFixtures(libs.edc.spi.dataplane.dataplane))
}



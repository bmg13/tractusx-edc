/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

plugins {
    `java-library`
    id("application")
}

dependencies {

    runtimeOnly(project(":edc-dataplane:edc-dataplane-base")) {
        exclude("org.eclipse.edc", "api-observability")
        exclude("org.eclipse.edc", "data-plane-selector-client")
        //exclude("org.eclipse.edc", "data-plane-http")
    }

    // use basic (all in-mem) control plane
    implementation(project(":edc-controlplane:edc-controlplane-base"))
    implementation(project(":core:json-ld-core"))

    implementation(libs.edc.iam.mock)
    implementation(libs.edc.spi.keys)
    // for the controller
    implementation(libs.jakarta.rsApi)
    implementation(libs.bundles.edc.sts)

    implementation(libs.edc.lib.token)

    implementation(libs.edc.ih.common.core)
    implementation(libs.edc.ih.core)
    implementation(libs.edc.ih.keypairs)
    implementation(libs.edc.ih.participants)
    implementation(libs.edc.spi.http)
    implementation(libs.edc.spi.dataplane.dataplane)
    implementation(libs.edc.spi.dataplane.http)
    //api(project(":edc-extensions:dataplane:dataplane-http-spi"))
    implementation(libs.edc.lib.util )
    implementation(project(":spi:core-spi"))
    implementation(libs.edc.dpf.http)
    implementation(libs.edc.dpf.core)
    implementation(project(":edc-extensions:dataplane:dataplane-proxy:dataplane-proxy-http"))
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

edcBuild {
    publish.set(false)
}

/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

plugins {
    `java-library`
}

dependencies {
    api(libs.edc.spi.dataplane.dataplane)

    implementation(libs.edc.lib.util)

    implementation(libs.opentelemetry.instrumentation.annotations)

    //implementation(project(":edc-extensions:dataplane:dataplane-http"))
    implementation(libs.edc.dpf.http)
    implementation(project(":edc-extensions:dataplane:dataplane-smt-http"))

    implementation(project(":spi:core-spi"))
    //runtimeOnly(project(":edc-extensions:dataplane:dataplane-proxy:dataplane-public-api-v2")) // todo: change to proper module

    //testImplementation(project(":tests:junit-base"));
    testImplementation(libs.edc.junit)

}



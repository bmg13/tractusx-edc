package org.eclipse.tractusx.edc.compatibility.tests.fixtures;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.eclipse.tractusx.edc.tests.participant.TractusxDcpParticipantBase;

import java.io.StringReader;

import static jakarta.json.Json.createObjectBuilder;

public class LegacyRemoteParticipant extends RemoteParticipant {

    public JsonObject getDatasetForAssetWithDid(String assetId, TractusxDcpParticipantBase provider) {
        String counterPartyAddress = provider.getProtocolUrl();
        if (!counterPartyAddress.endsWith("/2025-1")) {
            counterPartyAddress = counterPartyAddress + "/2025-1";
        }

        var catalogRequest = createObjectBuilder()
                .add("@context", createObjectBuilder()
                        .add("@vocab", "https://w3id.org/edc/v0.0.1/ns/")
                        .build())
                .add("@type", "CatalogRequest")
                .add("protocol", "dataspace-protocol-http:2025-1")
                .add("counterPartyAddress", counterPartyAddress)
                .add("counterPartyId", provider.getDid())
                .add("querySpec", createObjectBuilder()
                        .add("@type", "QuerySpec")
                        .add("offset", 0)
                        .add("limit", 50)
                        .build())
                .build();

        var catalogResponse = baseManagementRequest()
                .contentType(ContentType.JSON)
                .body(catalogRequest)
                .when()
                .post("/catalog/request")  // Remove /v3 or /v4 prefix
                .then()
                .log()
                .ifError()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        return Json.createReader(new StringReader(catalogResponse))
                .readObject()
                .getJsonArray("dcat:dataset")
                .stream()
                .map(JsonValue::asJsonObject)
                .filter(dataset -> assetId.equals(dataset.getString("@id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Asset " + assetId + " not found in catalog"));
    }

    @Override
    public RequestSpecification baseManagementRequest() {
        return super.baseManagementRequest().basePath("/v3");
    }


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

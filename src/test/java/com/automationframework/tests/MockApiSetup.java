package com.automationframework.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockApiSetup {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void startMockServer() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost", 8080);

        // Positive scenario: both title & price present
        stubFor(post(urlEqualTo("/products"))
                .withRequestBody(matchingJsonPath("$.title"))
                .withRequestBody(matchingJsonPath("$.price"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"title\":\"Valid Product\",\"price\":29.99}")));

        // Negative scenario: missing price (title present, price absent)
        stubFor(post(urlEqualTo("/products"))
                .withRequestBody(matchingJsonPath("$.title"))
                .withRequestBody(notMatching(".*\"price\".*"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Missing price\"}")));

        // Negative scenario: missing title (price present, title absent)
        stubFor(post(urlEqualTo("/products"))
                .withRequestBody(matchingJsonPath("$.price"))
                .withRequestBody(notMatching(".*\"title\".*"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Missing title\"}")));

        // Negative scenario: empty payload
        stubFor(post(urlEqualTo("/products"))
                .withRequestBody(equalToJson("{}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Empty payload\"}")));

        // Catch-all for malformed or unexpected JSON
        stubFor(post(urlEqualTo("/products"))
                .atPriority(10) // lower priority than specific stubs
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Invalid request\"}")));
    }

    @AfterClass
    public void stopMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
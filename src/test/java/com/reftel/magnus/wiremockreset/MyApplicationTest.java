package com.reftel.magnus.wiremockreset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.junit5.DropwizardAppExtension;

class MyApplicationTest {
    @RegisterExtension
    public static WireMockExtension other = WireMockExtension.newInstance().failOnUnmatchedRequests(true).build();

    @RegisterExtension
    @Order(Integer.MAX_VALUE)
    public static DropwizardAppExtension<MyConfiguration> app = new DropwizardAppExtension<>(
        new DropwizardTestSupport<>(MyApplication.class, "src/test/resources/test.yaml") {
            @Override
            public void before() throws Exception {
                addMappings();
                ConfigOverride.config("uri", other.baseUrl()).addToSystemProperties();
//                ConfigOverride.config("uri", "http://localhost:8080").addToSystemProperties();
                super.before();
            }
        }
    );

    @BeforeEach
    public void configureWiremock() {
        addMappings();
    }

    private static void addMappings() {
        other.stubFor(WireMock.any(WireMock.anyUrl()).withName("any").willReturn(WireMock.ok()));
    }

    @BeforeAll
    public static void waitUntilStarted() throws InterruptedException {
        final MyApplication application = app.getApplication();
        application.waitUntilStarted();
    }

    @Test
    public void shouldNeverGetFailuresFromRemote() throws InterruptedException {
        final MyApplication application = app.getApplication();
        Thread.sleep(1000);
        Assumptions.assumeTrue(application.getPass() > 0);
        Assertions.assertEquals(application.getFail(), 0);
    }
}
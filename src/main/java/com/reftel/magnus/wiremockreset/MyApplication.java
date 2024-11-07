package com.reftel.magnus.wiremockreset;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

public class MyApplication extends Application<MyConfiguration> {
    private int pass = 0;
    private int fail = 0;
    private Semaphore started = new Semaphore(0);

    @Override
    public void run(MyConfiguration configuration, Environment environment) throws Exception {
        final HttpClient httpClient = HttpClient.newHttpClient();
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
            () -> {
                final HttpResponse<Void> response;
                try {
                    response = httpClient.send(
                        HttpRequest.newBuilder()
                            .GET()
                            .uri(configuration.uri)
                            .build(),
                        HttpResponse.BodyHandlers.discarding()
                    );
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (response.statusCode() == 200) {
                    pass++;
                } else {
;                    fail++;
                }
                started.release();
            },
            1, 1, TimeUnit.NANOSECONDS
        );
    }

    public int getPass() {
        return pass;
    }

    public int getFail() {
        return fail;
    }

    public void waitUntilStarted() throws InterruptedException {
        started.acquire();
    }
}

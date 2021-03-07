package com.btmatthews.testccontainers.mountebank;

import com.btmatthews.testcontainers.mountebank.MountebankContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestStartWithDefaultResponse {

    @Container
    MountebankContainer mountebankContainer = new MountebankContainer(DockerImageName.parse("bbyars/mountebank:2.4.0"))
            .withImposter("api", "http", 8080, "")
            .withDefaultResponse(8080, 404, "Item not found", Collections.emptyMap());

    @Test
    void startup() throws ExecutionException, InterruptedException, IOException {
        assertThat(mountebankContainer.getMappedPort(2525)).isNotZero();
        assertThat(mountebankContainer.getMappedPort(8080)).isNotZero();

        assertThat(mountebankContainer.getConfiguration())
                .satisfies(configuration ->
                                assertThat(configuration.getImposters())
                                        .singleElement()
                                        .satisfies(imposter -> {
                                                    assertThat(imposter.getName()).isEqualTo("api");
                                                    assertThat(imposter.getPort()).isEqualTo(8080);
                                                    assertThat(imposter.getProtocol()).isEqualTo("http");
//                                            assertThat(imposter.getDefaultResponse())
//                                                    .satisfies(defaultResponse -> {
//                                                                assertThat(defaultResponse.getStatusCode()).isEqualTo(400);
//                                                                assertThat(defaultResponse.getBody()).isEqualTo("Item not found");
//                                                                assertThat(defaultResponse.getHeaders()).isEmpty();
//                                                            }
//                                                    );
                                                }
                                        )
                );

        assertThat(mountebankContainer.getConfiguration(8080))
                .satisfies(imposter -> {
                            assertThat(imposter.getName()).isEqualTo("api");
                            assertThat(imposter.getPort()).isEqualTo(8080);
                            assertThat(imposter.getProtocol()).isEqualTo("http");
//                            assertThat(imposter.getDefaultResponse())
//                                    .satisfies(defaultResponse -> {
//                                                assertThat(defaultResponse.getStatusCode()).isEqualTo(400);
//                                                assertThat(defaultResponse.getBody()).isEqualTo("Item not found");
//                                                assertThat(defaultResponse.getHeaders()).isEmpty();
//                                            }
//                                    );
                        }
                );

        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(mountebankContainer.getHttpUri(8080))).GET().build();
        assertThat(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()))
                .satisfies(response -> {
                    assertThat(response.statusCode()).isEqualTo(404);
                    assertThat(response.body()).isEqualTo("Item not found");
                });
    }
}

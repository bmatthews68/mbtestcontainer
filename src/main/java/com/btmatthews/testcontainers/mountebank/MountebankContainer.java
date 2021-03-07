package com.btmatthews.testcontainers.mountebank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MountebankContainer extends GenericContainer<MountebankContainer> {

    private static final String IMPOSTERS_FILE = "/app/imposters.ejs";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("bbyars/mountebank");

    private static final int MOUNTEBANK_PORT = 2525;
    private final MountebankConfiguration configuration = new MountebankConfiguration(new ArrayList<>());
    private final Map<Integer, ImposterDefinition> imposters = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    private int port;

    public MountebankContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);

        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        withExposedPorts(MOUNTEBANK_PORT);
    }

    public MountebankConfiguration getConfiguration() throws InterruptedException, ExecutionException {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/imposters")).build();
        return client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(x -> {
                    logger().info(x);
                    return x;
                })
                .thenApply(this::fromJson)
                .get();
    }

    public ImposterDefinition getConfiguration(final int imposterPort) throws InterruptedException, ExecutionException {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/imposters/" + imposterPort + "?replayable=false&removeProxies=false")).build();
        return client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(x -> {
                    logger().info(x);
                    return x;
                })
                .thenApply(this::imposterFromJson)
                .get();
    }

    public String getHttpUri(final int imposterPort) {
        return "http://localhost:" + getMappedPort(imposterPort);
    }

    public MountebankContainer withImposter(final String name,
                                            final String protocol,
                                            final int port,
                                            final String stubs) {
        final ImposterDefinition imposter = new ImposterDefinition(name, protocol, port, null);
        imposters.put(port, imposter);
        configuration.getImposters().add(imposter);
        addExposedPort(port);
        return self();
    }

    public MountebankContainer withDefaultResponse(final int port,
                                                   final int statusCode,
                                                   final String body,
                                                   final Map<String, List<String>> headers) {
        final ImposterDefinition imposter = imposters.get(port);
        imposter.setDefaultResponse(new ImposterDefinition.DefaultResponse(statusCode, body, headers));
        return self();
    }

    @Override
    protected void doStart() {
        if (!imposters.isEmpty()) {
            withCommand("start", "--configfile", IMPOSTERS_FILE);
        }
        super.doStart();
    }

    @Override
    protected void containerIsStarting(final InspectContainerResponse containerInfo,
                                       final boolean reused) {
        super.containerIsStarting(containerInfo, reused);

        port = getMappedPort(MOUNTEBANK_PORT);

        if (!(reused || imposters.isEmpty())) {
            try {
                final String json = objectMapper.writeValueAsString(configuration);
                logger().info(json);
                copyFileToContainer(
                        Transferable.of(json.getBytes(StandardCharsets.UTF_8), 0444),
                        IMPOSTERS_FILE);
            } catch (final JsonProcessingException e) {
            }
        }
    }


    private MountebankConfiguration fromJson(final String json) {
        try {
            return objectMapper.readValue(json, MountebankConfiguration.class);
        } catch (final IOException e) {
            logger().error("Cannot parse JSON", e);
            throw new MountebankContainerException("Cannot parse JSON", e);
        }
    }

    private ImposterDefinition imposterFromJson(final String json) {
        try {
            return objectMapper.readValue(json, ImposterDefinition.class);
        } catch (final IOException e) {
            logger().error("Cannot parse JSON", e);
            throw new MountebankContainerException("Cannot parse JSON", e);
        }
    }
}

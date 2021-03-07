package com.btmatthews.testccontainers.mountebank;


import com.btmatthews.testcontainers.mountebank.MountebankContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestStartWithoutImposters {

    @Container
    MountebankContainer mountebankContainer = new MountebankContainer(DockerImageName.parse("bbyars/mountebank:2.4.0"));

    @Test
    void startup() throws InterruptedException, ExecutionException {
        assertThat(mountebankContainer.getMappedPort(2525)).isNotZero();

        assertThat(mountebankContainer.getConfiguration())
                .satisfies(configuration -> {
                    assertThat(configuration.getImposters()).isEmpty();
                });
    }
}

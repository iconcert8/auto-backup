package com.gyeom.infrastructure;

import com.gyeom.domain.AppConfiguration;
import com.gyeom.domain.ConfigRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalConfigRepositoryTest {

    private static ConfigRepository repository;

    @BeforeAll
    static void beforeAll() {
        repository = new LocalConfigRepository();
    }

    @Test
    void write() {
        assertTrue(repository.write(AppConfiguration.getInstance()));
    }

    @Test
    void read() {
        assertTrue(repository.read(AppConfiguration.getInstance()));
        System.out.println(AppConfiguration.getInstance().toJson());
    }

}

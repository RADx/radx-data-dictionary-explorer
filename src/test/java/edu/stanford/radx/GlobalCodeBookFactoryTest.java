package edu.stanford.radx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureJsonTesters
class GlobalCodeBookFactoryTest {

    @Autowired
    private GlobalCodeBookFactory factory;

    private GlobalCodeBook gcb;

    @BeforeEach
    void setUp() {
        gcb = factory.getGlobalCodeBook();
    }

    @Test
    public void shouldLoadGlobalCodeBook() {

        assertThat(gcb).isNotNull();
    }

    @Test
    public void shouldContainRace() {
        assertThat(gcb.isVariable("Race")).isTrue();
    }
}
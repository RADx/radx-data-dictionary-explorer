package edu.stanford.radx;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SheetNameStopList_Test {

    @Test
    void shouldExclude_ReadMe() {
        assertThat(SheetNameStopList.isStopped("ReadMe")).isTrue();
    }

    @Test
    void shouldExclude_Read__Me() {
        assertThat(SheetNameStopList.isStopped("Read_Me")).isTrue();
    }


    @Test
    void shouldExclude_Read_Period_Me() {
        assertThat(SheetNameStopList.isStopped("Read.Me")).isTrue();
    }

    @Test
    void shouldExclude_readme() {
        assertThat(SheetNameStopList.isStopped("readme")).isTrue();
    }

    @Test
    void shouldExclude_Read_Me() {
        assertThat(SheetNameStopList.isStopped("Read Me")).isTrue();
    }

    @Test
    void shouldExclude_Change_Log() {
        assertThat(SheetNameStopList.isStopped("Change Log")).isTrue();
    }
}
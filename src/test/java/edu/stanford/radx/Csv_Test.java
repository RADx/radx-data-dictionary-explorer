package edu.stanford.radx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Csv_Test {

    private Csv csv;

    @Mock
    private CsvCoordinates coordinates;

    private List<String> header;

    private List<List<String>> data;

    @BeforeEach
    void setUp() {
        header = new ArrayList<>(List.of("ColA","ColB"));
        data = new ArrayList<>(List.of(new ArrayList<>(List.of("data")), new ArrayList<>(List.of("")), new ArrayList<>(List.of("other"))));
        csv = new Csv(coordinates, header, data);
    }

    @Test
    void shouldNotModifyHeader() {
        assertThrows(UnsupportedOperationException.class, () -> {
            csv.header().clear();
        });

    }

    @Test
    void shouldNotModifyContent() {
        assertThrows(UnsupportedOperationException.class, () -> {
            csv.content().clear();
        });
    }

    @Test
    void shouldGetIndex() {
        int index = csv.getIndex("ColB");
        assertThat(index).isEqualTo(1);
    }

    @Test
    void shouldNotGetIndex() {
        int index = csv.getIndex("ColC");
        assertThat(index).isEqualTo(-1);
    }

    @Test
    void shouldRemoveEmptyColumns() {
        var csvWithout = csv.withoutEmptyColumns();
        assertThat(csvWithout.header()).hasSize(1);
    }

    @Test
    void shouldRemoveEmptyRows() {
        assertThat(csv.content()).hasSize(3);
        var withoutEmptyRows = csv.withoutEmptyRows();
        assertThat(withoutEmptyRows.content()).hasSize(2);
    }

    @Test
    void shouldPadShortRowsToHeader() {
        assertThat(csv.padShortRowsToHeader()
                .content().get(0)).hasSize(2);
        assertThat(csv.padShortRowsToHeader()
                .content().get(1)).hasSize(2);
        assertThat(csv.padShortRowsToHeader()
                .content().get(2)).hasSize(2);
    }
}
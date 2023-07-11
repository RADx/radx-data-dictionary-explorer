package edu.stanford.radx;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
@Component
public class GlobalCodeBookFactory {

    private final GlobalCodeBookParser parser;

    public GlobalCodeBookFactory(GlobalCodeBookParser parser) {
        this.parser = parser;
    }

    public GlobalCodeBook getGlobalCodeBook() {
        try {
            var inputStream = GlobalCodeBookFactory.class.getResourceAsStream("/globalcodebook.csv");
            var records = parser.parse(inputStream);

            var upRecords = GlobalCodeBookDccParser.get(GlobalCodeBookUpRecord.class)
                    .parse(GlobalCodeBookFactory.class.getResourceAsStream("/globalcodebook-RADx-UP.csv"));


            var techRecords = GlobalCodeBookDccParser.get(GlobalCodeBookTechRecord.class)
                    .parse(GlobalCodeBookFactory.class.getResourceAsStream("/globalcodebook-RADx-Tech.csv"));

            var radRecords = GlobalCodeBookDccParser.get(GlobalCodeBookRadRecord.class)
                    .parse(GlobalCodeBookFactory.class.getResourceAsStream("/globalcodebook-RADx-rad.csv"));

            var dhtRecords = GlobalCodeBookDccParser.get(GlobalCodeBookDhtRecord.class)
                    .parse(GlobalCodeBookFactory.class.getResourceAsStream("/globalcodebook-RADx-DHT.csv"));




            return GlobalCodeBook.get(records, upRecords, techRecords, radRecords, dhtRecords);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

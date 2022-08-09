package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public class DataDictionaryFieldsSummarizer implements DataDictionariesProcessor {

    private static final String FIELD_NAME = "Field Name";

    private static final String SOURCE = "Source";

    private static final String SAMPLED_VALUES = "Sampled values";

    private static final String DIRECTORY = "directory";

    private static final String FIELDS_SUMMARY_WITH_VALUES_CSV = "fields-summary-with-values.csv";

    private static final String FIELDS_SUMMARY_CSV = "fields-summary.csv";

    private static final String CANONICAL_FIELD_NAME = "Canonical Field Name";

    private static final String RED_CAP_FIELD_NAME = "RED Cap Field Name";

    private final CsvProcessor csvProcessor;

    private final List<CsvFieldDescriptor> allDescriptors = new ArrayList<>();

    private final FieldNameSynonyms fieldNameSynonyms;

    private static final Logger logger = LoggerFactory.getLogger(DataDictionaryFieldsSummarizer.class);

    private final long sampleValuesLimit;

    public DataDictionaryFieldsSummarizer(CsvProcessor csvProcessor,
                                          FieldNameSynonyms fieldNameSynonyms,
                                          long sampleValuesLimit) {
        this.csvProcessor = csvProcessor;
        this.fieldNameSynonyms = fieldNameSynonyms;
        this.sampleValuesLimit = sampleValuesLimit;
    }

    @Override
    public void beginProcessing() {
        allDescriptors.clear();
    }

    @Override
    public void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory) {
        var descriptors = csvProcessor.processCsvFile(dataDictionary);
        allDescriptors.addAll(descriptors);
    }

    @Override
    public void endProcessing(Path outputPath) {
        try {
            writeFieldSummaryWithSampleValues(allDescriptors, outputPath);
            writeFieldsSummary(allDescriptors, outputPath);
        } catch (IOException e) {
            logger.error("Error when writing CSV", e);
        }
    }

    private void writeFieldsSummary(List<CsvFieldDescriptor> fieldDescriptors,
                                    Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);
        var path = outputDirectory.resolve(FIELDS_SUMMARY_CSV);
        var csvPrinter = new CSVPrinter(Files.newBufferedWriter(path, StandardCharsets.UTF_8), CSVFormat.DEFAULT);
        csvPrinter.printRecord("Directory", "Dictionary", FIELD_NAME, RED_CAP_FIELD_NAME, CANONICAL_FIELD_NAME, "Number of Data Dictionaries", "Dictionaries");

        var fieldNames2Sources = fieldDescriptors.stream()
                .map(d -> List.of(d.fieldName(), d.source()))
                .distinct()
                .collect(Collectors.groupingBy(d -> d.get(0)));


        fieldDescriptors.stream()
                        .sorted()
                        .map(desc -> List.of(
                                desc.source().getParentDirectory().getFileName().toString(),
                                desc.source().getDescription(),
                                desc.fieldName(),
                                RedCapFieldNames.isRedCapFieldName(desc.fieldName()) ? "yes" : "" ,
                                fieldNameSynonyms.getCanonicalFieldName(desc.fieldName()),
                                fieldNames2Sources.get(desc.fieldName()).size(),
                                fieldNames2Sources.get(desc.fieldName()).stream().map(d -> d.get(1))
                                                     .map(s -> (CsvCoordinates) s)
                                                     .map(c -> c.getDescription())
                                                               .collect(Collectors.joining("  |"))))
                        .distinct()
                        .forEach(values -> {
                            try {
                                csvPrinter.printRecord(values);
                            } catch (IOException e) {
                                logger.error("Error while writing CSV", e);
                            }
                        });
        csvPrinter.close();
    }

    private void writeFieldSummaryWithSampleValues(List<CsvFieldDescriptor> fieldDescriptors,
                                                   Path outputDirectory) throws IOException {
        var path = outputDirectory.resolve(FIELDS_SUMMARY_WITH_VALUES_CSV);
        var csvPrinter = new CSVPrinter(Files.newBufferedWriter(path, StandardCharsets.UTF_8), CSVFormat.DEFAULT);
        csvPrinter.printRecord(FIELD_NAME, CANONICAL_FIELD_NAME, SAMPLED_VALUES, DIRECTORY, SOURCE);
        fieldDescriptors.stream()
                        .sorted()
                        .flatMap(descriptor -> descriptor.exampleContent()
                                                         .stream()
                                                         .filter(val -> !val.isBlank())
                                                         .distinct()
                                                         .limit(sampleValuesLimit)
                                                         .map(ex -> List.of(descriptor.fieldName(),
                                                                            fieldNameSynonyms.getCanonicalFieldName(
                                                                                    descriptor.fieldName()),
                                                                            ex,
                                                                            descriptor.source()
                                                                                      .getParentDirectory()
                                                                                      .getFileName(),
                                                                            descriptor.source().getDescription())))
                        .forEach(values -> {
                            try {
                                csvPrinter.printRecord(values);
                            } catch (IOException e) {
                                logger.error("Error while writing CSV", e);
                            }
                        });
        csvPrinter.close();
    }
}

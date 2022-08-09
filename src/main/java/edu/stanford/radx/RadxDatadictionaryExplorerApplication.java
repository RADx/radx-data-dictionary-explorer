package edu.stanford.radx;

import edu.stanford.radx.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class RadxDatadictionaryExplorerApplication implements CommandLineRunner, ExitCodeGenerator {

	private int exitCode;

	@Autowired
	private picocli.CommandLine.IFactory factory;

	public static void main(String[] args) {
		SpringApplication.run(RadxDatadictionaryExplorerApplication.class, args);
	}

	@Bean
	DataDictionariesSummaryGenerator dataDictionariesSummaryGenerator(
											 InputFilesProcessor inputFilesProcessor,
											 List<DataDictionariesProcessor> dataDictionariesProcessors) {
		return new DataDictionariesSummaryGenerator(inputFilesProcessor,
													dataDictionariesProcessors);
	}

	@Bean
	CliCsvTransformerProcessor csvTransformer(CliInputFilesProcessor processor, CliCsvWriter writer) {
		return new CliCsvTransformerProcessor(processor, writer);
	}

	@Bean
	CliCsvWriter writer() {
		return new CliCsvWriter();
	}


	@Bean
	CsvFilesProcessor csvFilesProcessor(InputFilesProcessor inputFilesProcessor) {
		return new CsvFilesProcessor(inputFilesProcessor);
	}

	@Bean
	CsvConsolePrinter csvConsolePrinter() {
		return new CsvConsolePrinter(System.out);
	}

	@Bean
	CsvProcessor csvProcessor() {
		return new CsvProcessor();
	}

	@Bean
	InputFilesProcessor inputFilesProcessor(Excel2Csv excel2Csv) {
		return new InputFilesProcessor(excel2Csv);
	}

	@Bean
	Excel2Csv excel2Csv(ExcelWorkbookParser workbookParser) {
		return new Excel2Csv(workbookParser);
	}

	@Bean
	ExcelWorkbookParser workbookParser() {
		return new ExcelWorkbookParser();
	}

	@Bean
	FieldsSourcesValuesSummaryWriter fieldsSourcesValuesSummaryWriter() {
		return new FieldsSourcesValuesSummaryWriter();
	}

	@Bean
	DataDictionaryFieldsSummarizer dataDictionaryFieldsSummarizer(CsvProcessor csvProcessor,
																  FieldNameSynonyms fieldNameSynonyms,
																  @Value("${sampled-values:10}") long sampleValuesLimit) {
		return new DataDictionaryFieldsSummarizer(csvProcessor, fieldNameSynonyms, sampleValuesLimit);
	}

	@Bean
	DataDictionariesMergingProcessor mergingSummarizer(FieldNameSynonyms fieldNameSynonyms) {
		return new DataDictionariesMergingProcessor(fieldNameSynonyms);
	}

	@Bean
	DataDictionaryDescriptiveStatsSummarizer dataDictionaryDescriptiveStatsSummarizer() {
		return new DataDictionaryDescriptiveStatsSummarizer();
	}

	@Bean
	DataDictionariesCsvOutProcessor dataDictionariesCsvOutProcessor() {
		return new DataDictionariesCsvOutProcessor();
	}

	@Bean
	FieldNameSynonymsLoader canonicalFieldNamesLoader(FieldNameSynonyms fieldNameSynonyms) {
		return new FieldNameSynonymsLoader(fieldNameSynonyms);
	}

	@Bean
	FieldNameNormalizer fieldNameNormalizer() {
		return new FieldNameNormalizer();
	}



	@Bean
	FieldNameSynonyms canonicalFieldNames(FieldNameNormalizer fieldNameNormalizer) {
		return new FieldNameSynonyms(fieldNameNormalizer);
	}

	@Bean
	CliInputFilesProcessor cliInputFilesProcessor(CsvFilesProcessor processor) {
		return new CliInputFilesProcessor(processor);
	}

	private InputStream getFieldNameMapInputStream(Path fieldNamesMap) throws IOException {
		if(fieldNamesMap == null) {
			return FieldNameSynonymsLoader.class.getResourceAsStream("/field-name-synonyms.csv");
		}
		else {
			return Files.newInputStream(fieldNamesMap);
		}
	}

	@Bean
	protected MappedFieldsProcessor mappedFieldsProcessor(FieldNameSynonyms fieldNameSynonyms) {
		return new MappedFieldsProcessor(fieldNameSynonyms);
	}

	public RadxDatadictionaryExplorerApplication(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	private final ApplicationContext ctx;

	@Override
	public void run(String... args) throws Exception {

		var cliRunner = ctx.getBean(CliRunner.class);
		exitCode = cliRunner.execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}

	@Bean
	DataDictionaryFilter dataDictionaryFilter(FieldNameSynonyms fieldNameSynonyms) {
		return new DataDictionaryFilter(fieldNameSynonyms);
	}
}

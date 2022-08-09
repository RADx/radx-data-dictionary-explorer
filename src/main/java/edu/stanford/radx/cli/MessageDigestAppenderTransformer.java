package edu.stanford.radx.cli;

import com.google.common.io.BaseEncoding;
import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvTransformer;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-08-09
 */
public class MessageDigestAppenderTransformer implements CsvTransformer {

    private final List<String> fieldNames;

    public MessageDigestAppenderTransformer(List<String> fieldNames) {
        this.fieldNames = new ArrayList<>(fieldNames);
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var rowsWithDigest = csv.content()
                .stream()
                .map(row -> getRowWithDigest(csv, row))
                .toList();
        var replacementHeader = new ArrayList<>(csv.header());
        replacementHeader.add("Digest (" + String.join("|", fieldNames) + ")");
        return List.of(new Csv(csv.coordinates(), replacementHeader, rowsWithDigest));
    }

    private List<String> getRowWithDigest(Csv csv, List<String> row) {
        try {
            var digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_512);
            digest.reset();
            fieldNames.stream()
                    .map(csv::getIndex)
                    .map(index -> {
                        if(index == -1) {
                            return "";
                        }
                        else {
                            return row.get(index);
                        }
                    })
                    .map(s -> s.getBytes(StandardCharsets.UTF_8))
                    .forEach(digest::update);
            var hexDigest = BaseEncoding.base16().lowerCase().encode(digest.digest());
            var rowWithDigest = new ArrayList<>(row);
            rowWithDigest.add(hexDigest);
            return rowWithDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

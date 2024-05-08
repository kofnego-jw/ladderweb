package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.ModifierDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.SubactDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.LadderRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorpusDataCSVService {

    private static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;

    private static String listToString(List<String> list) {
        if (list == null) {
            return "";
        }
        return list.stream().map(x -> x.replaceAll(",", "&#x2C;")).collect(Collectors.joining(","));
    }

    private static String listLongsToString(List<Long> list) {
        if (list == null) {
            return "";
        }
        return list.stream().map(x -> Long.toString(x)).collect(Collectors.joining(","));
    }

    public void writeAsCSV(CorpusData data, OutputStream outputStream) throws IOException {
        List<String> headers = new ArrayList<>();
        headers.add("id");
        headers.add("altId");
        headers.add("textdata");
        headers.add("languageCode");
        headers.add("creationTask");
        headers.add("lastModified");
        headers.add("gender");
        headers.add("ageAtCreation");
        headers.add("l1Language");
        headers.add("l2Languages");
        headers.add("location");
        headers.add("tokens");
        for (ModifierDTO modifier : data.modifierList) {
            headers.add(modifier.modifierCode);
        }
        for (SubactDTO subact : data.subactList) {
            headers.add(subact.getFullName());
        }

        try (OutputStreamWriter osWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            CSVPrinter printer = new CSVPrinter(osWriter, CSV_FORMAT);
            printer.printRecord(headers);

            for (LadderRecord record : data.records) {
                List<Object> row = new ArrayList<>();
                row.add(record.textMetadata.id);
                row.add(record.textMetadata.altId);
                row.add(record.textMetadata.textdata);
                row.add(record.textMetadata.languageCode);
                row.add(record.textMetadata.getCreationTaskName());
                row.add(record.textMetadata.lastModified);
                row.add(record.textMetadata.gender);
                row.add(record.textMetadata.ageAtCreation);
                row.add(record.textMetadata.l1Language);
                row.add(record.textMetadata.l2Languages);
                row.add(record.textMetadata.location);
                row.add(listToString(record.textMetadata.tokens));
                for (ModifierDTO modifier : data.modifierList) {
                    List<Long> tokenNrs = record.modifierAnnotations.stream().filter(x -> x.modifierId == modifier.id)
                            .map(x -> x.startTn).collect(Collectors.toList());
                    String tokenNrCoded = listLongsToString(tokenNrs);
                    row.add(tokenNrCoded);
                }
                for (SubactDTO subact : data.subactList) {
                    List<Long> tokenNrs = record.subactAnnotations.stream().filter(x -> x.subactId == subact.id)
                            .map(x -> x.startTn).collect(Collectors.toList());
                    String tokenNrCoded = listLongsToString(tokenNrs);
                    row.add(tokenNrCoded);
                }
                printer.printRecord(row);
            }
        }
    }
}

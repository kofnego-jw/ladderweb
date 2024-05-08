package at.ac.uibk.fiba.ladder3.service;

import at.ac.uibk.fiba.ladder3.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Ladder3RepositoryToCsvService {

    public String toCsv(Ladder3Repository repository) throws Exception {
        StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormat.RFC4180.withFirstRecordAsHeader();
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
            List<Modifier> modifierList = repository.getModifierList();
            List<Subact> subactList = repository.getSubactList();
            List<String> header = new ArrayList<>();

            header.add("Pool");
            header.add("ID");
            header.add("Text");
            modifierList.forEach(m -> header.add("Modifier: " + m.modifierName));
            subactList.forEach(s -> header.add("Subact: " + s.getFullname()));

            printer.printRecord(header);

            List<Evidence> evidences = repository.getEvidenceList();
            for (Evidence ev : evidences) {
                List<String> rec = new ArrayList<>();
                rec.add(ev.pool.name);
                rec.add(ev.evidenceName);
                rec.add(ev.originalAssertions);
                List<ModifierMarking> modifierMarkings = repository.getModifierMarkings(ev);
                List<SubactMarking> subactMarkings = repository.getSubactMarkings(ev);
                for (Modifier m : modifierList) {
                    List<ModifierMarking> markings = modifierMarkings.stream().filter(marking -> marking.modifier.equals(m)).collect(Collectors.toList());
                    if (markings.isEmpty()) {
                        rec.add("");
                    } else {
                        rec.add(markings.stream().map(marking -> marking.text).collect(Collectors.joining("; ")));
                    }
                }
                for (Subact s : subactList) {
                    List<SubactMarking> markings = subactMarkings.stream().filter(marking -> marking.subact.equals(s)).collect(Collectors.toList());
                    if (markings.isEmpty()) {
                        rec.add("");
                    } else {
                        rec.add(markings.stream().map(marking -> marking.text).collect(Collectors.joining("; ")));
                    }
                }
                printer.printRecord(rec);
            }
        }
        return sw.toString();
    }
}

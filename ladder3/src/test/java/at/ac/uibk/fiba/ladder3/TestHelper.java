package at.ac.uibk.fiba.ladder3;

import at.ac.uibk.fiba.ladder3.model.*;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestHelper {

    private static final Lorem LOREM = new LoremIpsum();
    private static final Random R = new Random();

    private static final DateTimeFormatter  DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static LocalDateTime randomTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.minusMonths(R.nextInt(12))
                .minusDays(R.nextInt(30))
                .minusHours(R.nextInt(24))
                .minusMinutes(R.nextInt(60));
    }

    private static String randomTimeAsString() {
        return DATE_TIME_FORMATTER.format(randomTime());
    }

    private static <T>T randomMember(Collection<T> members) {
        if (members == null || members.isEmpty()) {
            return null;
        }
        int count = R.nextInt(members.size());
        int i = 0;
        Iterator<T> iterator = members.iterator();
        while (i < count) {
            iterator.next();
            i++;
        }
        return iterator.next();
    }

    public Modifier randomModifier() {
        return new Modifier(LOREM.getWords(1));
    }

    public List<Modifier> randomModifiers(int minCount, int maxCount) {
        List<Modifier> modifiers = new ArrayList<>();
        int count = minCount == maxCount ? minCount : minCount + R.nextInt(maxCount - minCount + 1) ;
        for (int i=0; i<count; i++) {
            modifiers.add(randomModifier());
        }
        return modifiers;
    }

    public Subact randomSubact(List<Subact> possibleParents) {
        Subact parent = randomMember(possibleParents);
        return new Subact(LOREM.getWords(1,2), parent);
    }

    public List<Subact> randomSubacts(int minParentCount, int maxParentCount, int minSubCount, int maxSubCount) {
        List<Subact> parents = new ArrayList<>();
        int parentCount = minParentCount == maxParentCount ? minParentCount : minParentCount + R.nextInt(maxParentCount - minParentCount + 1);
        for (int i=0; i<parentCount; i++) {
            parents.add(randomSubact(null));
        }
        List<Subact> children = new ArrayList<>();
        for (Subact parent: parents) {
            int subCount = minSubCount == maxSubCount ? minSubCount : minSubCount + R.nextInt(maxSubCount - minSubCount + 1);
            for (int i=0; i< subCount; i++)  {
                children.add(randomSubact(List.of(parent)));
            }
        }
        return Stream.concat(parents.stream(), children.stream()).collect(Collectors.toList());
    }

    public Pool randomPool() {
        String name = LOREM.getWords(2, 3);
        String created = randomTimeAsString();
        String modified = created;
        return new Pool(name, created, modified);
    }

    public List<Pool> randomPools(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> this.randomPool())
                .collect(Collectors.toList());
    }

    public Evidence randomEvidence(int counter, List<Pool> pools) {
        Pool pool = randomMember(pools);
        if (pool == null) {
            pool = randomPool();
        }
        String name = pool.name + "_" + String.format("%03d", counter);
        String assertions = LOREM.getParagraphs(1,1);
        return new Evidence(pool, name, assertions);
    }

    public List<Evidence> randomEvidences(int count, List<Pool> pools) {
        List<Evidence> evidences = new ArrayList<>();
        for (int i=0; i<count; i++) {
            evidences.add(randomEvidence(i + 1, pools));
        }
        return evidences;
    }

    public List<ModifierMarking> randomModifierMarking(Modifier modifier, Evidence evidence, int minPlaceCount, int maxPlaceCount) {
        List<ModifierMarking> markings = new ArrayList<>();
        int markingsCount = minPlaceCount == maxPlaceCount ? minPlaceCount : minPlaceCount + R.nextInt(maxPlaceCount - minPlaceCount + 1);
        int size = evidence.originalAssertions.length();
        for (int i=0; i<markingsCount; i++) {
            int one = R.nextInt(size);
            int two = R.nextInt(size);
            while (two == one) {
                two = R.nextInt(size);
            }
            int from = Math.min(one, two);
            int to = Math.max(one, two);
            String text = evidence.originalAssertions.substring(from, to);
            markings.add(new ModifierMarking(modifier, evidence, text, from, to));
        }
        return markings;
    }

    public List<ModifierMarking> randomModifierMarking(Modifier modifier, List<Evidence> evidences, int minEvidenceCount, int maxEvidenceCount, int minModifierCount, int maxModifierCount) {
        int chosenCount = minEvidenceCount == maxEvidenceCount ? minEvidenceCount : minModifierCount + R.nextInt(maxEvidenceCount - minEvidenceCount + 1);
        List<Evidence> chosen = new ArrayList<>();
        for (int i=0; i<chosenCount; i++) {
            Evidence chose = randomMember(evidences);
            while (chosen.contains(chose)) {
                chose = randomMember(evidences);
            }
            chosen.add(chose);
        }
        List<ModifierMarking> markings = new ArrayList<>();
        for (Evidence evidence: chosen) {
            markings.addAll(randomModifierMarking(modifier, evidence, minModifierCount, maxModifierCount));
        }
        return markings;
    }

    public List<ModifierMarking> randomModifierMarkings(List<Modifier> modifiers, List<Evidence> evidences, int minEvidenceCount, int maxEvidenceCount,
                                                        int minModifierCount, int maxModifierCount) {
        List<ModifierMarking> modifierMarkings = new ArrayList<>();
        for (Modifier modifier: modifiers) {
            modifierMarkings.addAll(randomModifierMarking(modifier, evidences, minEvidenceCount, maxEvidenceCount, minModifierCount, maxModifierCount));
        }
        return modifierMarkings;
    }

    public List<SubactMarking> randomSubactMarking(Subact subact, Evidence evidence, int minPlaceCount, int maxPlaceCount) {
        List<SubactMarking> markings = new ArrayList<>();
        int markingsCount = minPlaceCount == maxPlaceCount ? minPlaceCount : minPlaceCount + R.nextInt(maxPlaceCount - minPlaceCount + 1);
        int size = evidence.originalAssertions.length();
        for (int i=0; i<markingsCount; i++) {
            int one = R.nextInt(size);
            int two = R.nextInt(size);
            while (two == one) {
                two = R.nextInt(size);
            }
            int from = Math.min(one, two);
            int to = Math.max(one, two);
            String text = evidence.originalAssertions.substring(from, to);
            markings.add(new SubactMarking(subact, evidence, text, from, to));
        }
        return markings;
    }

    public List<SubactMarking> randomSubactMarking(Subact subact, List<Evidence> evidences, int minEvidenceCount, int maxEvidenceCount, int minPlaceCount, int maxPlaceCount) {
        int chosenCount = minEvidenceCount == maxEvidenceCount ? minEvidenceCount : minPlaceCount + R.nextInt(maxEvidenceCount - minEvidenceCount + 1);
        List<Evidence> chosen = new ArrayList<>();
        for (int i=0; i<chosenCount; i++) {
            Evidence chose = randomMember(evidences);
            while (chosen.contains(chose)) {
                chose = randomMember(evidences);
            }
            chosen.add(chose);
        }
        List<SubactMarking> markings = new ArrayList<>();
        for (Evidence evidence: chosen) {
            markings.addAll(randomSubactMarking(subact, evidence, minPlaceCount, maxPlaceCount));
        }
        return markings;
    }

    public List<SubactMarking> randomSubactMarkings(List<Subact> subacts, List<Evidence> evidences, int minEvidenceCount, int maxEvidenceCount,
                                                        int minPlaceCount, int maxPlaceCount) {
        List<SubactMarking> modifierMarkings = new ArrayList<>();
        for (Subact subact: subacts) {
            modifierMarkings.addAll(randomSubactMarking(subact, evidences, minEvidenceCount, maxEvidenceCount, minPlaceCount, maxPlaceCount));
        }
        return modifierMarkings;
    }


}

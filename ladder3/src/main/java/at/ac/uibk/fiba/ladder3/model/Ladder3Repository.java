package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

public class Ladder3Repository {

    public final List<Modifier> modifiers = new ArrayList<>();

    public final List<Subact> subacts = new ArrayList<>();

    public final List<Pool> pools = new ArrayList<>();

    public final List<Evidence> evidences = new ArrayList<>();

    public final List<ModifierMarking> modifierMarkings = new ArrayList<>();

    public final List<SubactMarking> subactMarkings = new ArrayList<>();

    public Ladder3Repository() {
    }

    @JsonCreator
    public Ladder3Repository(
            @JsonProperty("modifiers") List<Modifier> modifiers,
            @JsonProperty("subacts") List<Subact> subacts,
            @JsonProperty("pools") List<Pool> pools,
            @JsonProperty("evidences") List<Evidence> evidences,
            @JsonProperty("modifierMarkings") List<ModifierMarking> modifierMarkings,
            @JsonProperty("subactMarkings") List<SubactMarking> subactMarkings) {
        if (modifiers != null) {
            this.modifiers.addAll(modifiers);
        }
        if (subacts != null) {
            this.subacts.addAll(subacts);
        }
        if (pools != null) {
            this.pools.addAll(pools);
        }
        if (evidences != null) {
            this.evidences.addAll(evidences);
        }
        if (modifierMarkings != null) {
            this.modifierMarkings.addAll(modifierMarkings);
        }
        if (subactMarkings != null) {
            this.subactMarkings.addAll(subactMarkings);
        }
    }

    public Pool getPool(String id) {
        return this.pools.stream().filter(x -> x.name.equals(id))
                .findAny().orElse(null);
    }

    public Optional<Modifier> getModifier(String name) {
        return this.modifiers.stream().filter(x -> x.modifierName.equals(name)).findAny();
    }

    public Optional<Subact> getSubact(String name) {
        return this.subacts.stream()
                .filter(x -> x.name.equals(name))
                .findAny();
    }

    public Optional<Subact> getSubact(String parentName, String name) {
        Optional<Subact> parentOpt = getSubact(parentName);
        Subact parent;
        if (parentOpt.isEmpty()) {
            parent = new Subact(parentName, null);
            this.subacts.add(parent);
        } else {
            parent = parentOpt.get();
        }
        return this.subacts.stream().filter(x -> x.parentAct != null && x.parentAct.equals(parent) && x.name.equals(name))
                .findAny();
    }

    public Optional<Evidence> getEvidence(Pool pool, String id) {
        return this.evidences.stream()
                .filter(e -> e.pool.equals(pool) && e.evidenceName.equals(id))
                .findAny();
    }

    public void addMarkings(MarkingDTO dto, Evidence ev) {
        if (dto.type.equals("modifier")) {
            Optional<Modifier> modOpt = getModifier(dto.subtype);
            Modifier mod;
            if (modOpt.isEmpty()) {
                mod = new Modifier(dto.subtype);
                this.modifiers.add(mod);
            } else {
                mod = modOpt.get();
            }
            ModifierMarking marking = new ModifierMarking(mod, ev, dto.markedText, dto.from, dto.to);
            this.modifierMarkings.add(marking);
        } else {
            String[] parts = dto.subtype.contains("/") ? dto.subtype.split("\\s*/\\s*") : new String[]{dto.subtype};
            Optional<Subact> subactOpt = parts.length == 2 ? getSubact(parts[0], parts[1]) : getSubact(parts[0]);
            Subact subact;
            if (subactOpt.isEmpty()) {
                Subact parent;
                if (parts.length == 2) {
                    Optional<Subact> parentOpt = getSubact(parts[0]);
                    if (parentOpt.isEmpty()) {
                        parent = new Subact(parts[0], null);
                        this.subacts.add(parent);
                    } else {
                        parent = parentOpt.get();
                    }
                } else {
                    parent = null;
                }
                subact = new Subact(parts.length == 2 ? parts[1] : parts[0], parent);
                this.subacts.add(subact);
            } else {
                subact = subactOpt.get();
            }
            SubactMarking marking = new SubactMarking(subact, ev, dto.markedText, dto.from, dto.to);
            this.subactMarkings.add(marking);
        }
    }

    public void addTeiData(TeiDataDTO data, Pool pool) {
        Optional<Evidence> evOpt = getEvidence(pool, data.id);
        Evidence ev;
        if (evOpt.isEmpty()) {
            ev = new Evidence(pool, data.id, data.text);
            this.evidences.add(ev);
        } else {
            ev = evOpt.get();
        }
        if (data.markedPlaces != null) {
            data.markedPlaces.forEach(p -> addMarkings(p, ev));
        }
    }

    public void addDoc(TeiDocDTO docDTO) {
        Pool pool = getPool(docDTO.poolId);
        if (pool == null) {
            pool = new Pool(docDTO.poolId, docDTO.createdOn, docDTO.modifiedOn);
            this.pools.add(pool);
        }
        Pool myPool = pool;
        docDTO.data
                .forEach(data -> addTeiData(data, myPool));

    }

    public Map<Evidence, List<ModifierMarking>> getModifierMarkings(Modifier modifier) {
        return this.modifierMarkings
                .stream()
                .filter(m -> m.modifier.equals(modifier))
                .collect(Collectors.groupingBy(m -> m.evidence));
    }

    public Map<Evidence, List<SubactMarking>> getSubactMarkings(Subact subact) {
        return this.subactMarkings
                .stream()
                .filter(m -> m.subact.equals(subact))
                .collect(Collectors.groupingBy(m -> m.evidence));
    }

    public List<ModifierMarking> getModifierMarkings(Evidence evidence) {
        return this.modifierMarkings.stream().filter(m -> m.evidence.equals(evidence))
                .sorted(Comparator.comparing(m -> m.modifier.modifierName))
                .collect(Collectors.toList());
    }

    public List<SubactMarking> getSubactMarkings(Evidence evidence) {
        return this.subactMarkings.stream().filter(m -> m.evidence.equals(evidence))
                .sorted(Comparator.comparing(m -> m.subact.getFullname()))
                .collect(Collectors.toList());
    }

    public List<Modifier> getModifierList() {
        return this.modifiers.stream().sorted(Comparator.comparing(m -> m.modifierName)).collect(Collectors.toList());
    }

    public List<Subact> getSubactList() {
        return this.subacts.stream().sorted(Comparator.comparing(s -> s.getFullname())).collect(Collectors.toList());
    }

    public List<Evidence> getEvidenceList() {
        return this.evidences.stream().sorted(Comparator.comparing(e -> e.evidenceName)).collect(Collectors.toList());
    }
}

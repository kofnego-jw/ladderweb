package at.ac.uibk.fiba.ladder3ca.business.model;

public class AnnotationFW {
    public final Long modifierId;
    public final Long subactId;
    public final Long startTn;
    public final Long endTn;

    public AnnotationFW(Long modifierId, Long subactId, Long startTn, Long endTn) {
        this.modifierId = modifierId;
        this.subactId = subactId;
        this.startTn = startTn;
        this.endTn = endTn;
    }

    public static AnnotationFW fromModifierAnnotation(Long modifierId, Long startTn, Long endTn) {
        return new AnnotationFW(modifierId, null, startTn, endTn);
    }

    public static AnnotationFW fromSubactAnnotation(Long subactId, Long startTn, Long endTn) {
        return new AnnotationFW(null, subactId, startTn, endTn);
    }
}

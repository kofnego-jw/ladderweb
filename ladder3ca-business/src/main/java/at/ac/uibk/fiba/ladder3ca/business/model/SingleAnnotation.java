package at.ac.uibk.fiba.ladder3ca.business.model;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.ModifierAnnotation;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.SubactAnnotation;

public class SingleAnnotation {
    public final String textId;
    public final AnnotationModifier modifier;
    public final AnnotationSubact subact;
    public final Long startTn;
    public final Long endTn;

    public SingleAnnotation(String textId, AnnotationModifier modifier, AnnotationSubact subact, Long startTn, Long endTn) {
        this.textId = textId;
        this.modifier = modifier;
        this.subact = subact;
        this.startTn = startTn;
        this.endTn = endTn;
    }

    public static SingleAnnotation fromAnnotation(ModifierAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        return new SingleAnnotation(annotation.getAnnotatedText().getId(), annotation.getModifier(), null, annotation.getStartTn(), annotation.getEndTn());
    }

    public static SingleAnnotation fromAnnotation(SubactAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        return new SingleAnnotation(annotation.getAnnotatedText().getId(), null, annotation.getSubact(), annotation.getStartTn(), annotation.getEndTn());
    }

}

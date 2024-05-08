package at.ac.uibk.fiba.ladder3ca.business.model;

import at.ac.uibk.fiba.ladder3ca.business.index.FieldType;

public enum LadderField {

    ID("_id", FieldType.KEYWORD, false),
    ALT_ID("altId", FieldType.KEYWORD, false),
    LANGUAGE("language", FieldType.KEYWORD, false),
    TEXT_CONTENT("content", FieldType.TEXT, false),
    CREATION_TASK("creationTaskId", FieldType.NUMBER, false),
    LAST_MODIFIED("lastModified", FieldType.NUMBER, false),
    SPEAKER_GENDER("speakerGender", FieldType.KEYWORD, false),
    AGE_AT_CREATION("ageAtCreation", FieldType.NUMBER, false),
    L1_LANGUAGE("l1Language", FieldType.KEYWORD, true),
    L2_LANGUAGE("l2Language", FieldType.KEYWORD, true),
    LOCATION("location", FieldType.KEYWORD, false),
    MODIFIERS("modifiers", FieldType.NUMBER, true),
    SUBACTS("subacts", FieldType.NUMBER, true);

    public final String fieldname;
    public final FieldType fieldType;

    public final boolean multiValued;

    LadderField(String fieldname, FieldType fieldType, boolean multiValued) {
        this.fieldname = fieldname;
        this.fieldType = fieldType;
        this.multiValued = multiValued;
    }
}

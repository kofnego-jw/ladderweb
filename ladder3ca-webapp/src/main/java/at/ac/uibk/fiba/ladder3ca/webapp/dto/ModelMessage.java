package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import java.util.List;

public class ModelMessage {

    public final ModelType type;
    public final String modelName;
    public final String language;
    public final List<String> messages;

    public ModelMessage(ModelType type, String modelName, String language, List<String> messages) {
        this.type = type;
        this.modelName = modelName;
        this.language = language;
        this.messages = messages;
    }
}

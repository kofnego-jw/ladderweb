package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import java.util.List;

public class RetrainModelMessagesDTO {
    public final List<ModelMessage> modelMessageList;

    public RetrainModelMessagesDTO(List<ModelMessage> modelMessageList) {
        this.modelMessageList = modelMessageList;
    }
}

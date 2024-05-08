package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

public enum SpeakerGender {
    F("female"), M("male"), D("diverse");

    public final String readableName;

    SpeakerGender(String readableName) {
        this.readableName = readableName;
    }

}

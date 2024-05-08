package at.ac.uibk.fiba.ladder3ca.business.model;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;

import java.util.List;

public class TextHit {
    public final TextMetadata textMetadata;
    public final List<String> snippets;

    public TextHit(TextMetadata textMetadata, List<String> snippets) {
        this.textMetadata = textMetadata;
        this.snippets = snippets;
    }
}

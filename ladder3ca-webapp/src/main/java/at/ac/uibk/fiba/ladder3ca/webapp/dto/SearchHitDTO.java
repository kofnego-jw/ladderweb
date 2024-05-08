package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.business.model.TextHit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchHitDTO {
    public final TextWithMetadataDTO textMetadata;
    public final List<String> snippets;

    public SearchHitDTO(TextWithMetadataDTO textMetadata, List<String> snippets) {
        this.textMetadata = textMetadata;
        this.snippets = snippets;
    }

    public static SearchHitDTO fromTextHit(TextHit hit) {
        if (hit == null) {
            return null;
        }
        return new SearchHitDTO(TextWithMetadataDTO.fromTextMetadata(hit.textMetadata, Collections.emptyList()), hit.snippets);
    }

    public static List<SearchHitDTO> fromTextHits(List<TextHit> hits) {
        if (hits == null) {
            return Collections.emptyList();
        }
        return hits.stream().map(SearchHitDTO::fromTextHit).collect(Collectors.toList());
    }
}

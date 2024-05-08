package at.ac.uibk.fiba.ladder3ca.business.model;

import java.util.List;

public class SearchResult {

    public final SearchRequest searchRequest;
    public final int totalHits;
    public final List<TextHit> hits;

    public SearchResult(SearchRequest searchRequest, int totalHits, List<TextHit> hits) {
        this.searchRequest = searchRequest;
        this.totalHits = totalHits;
        this.hits = hits;
    }
}

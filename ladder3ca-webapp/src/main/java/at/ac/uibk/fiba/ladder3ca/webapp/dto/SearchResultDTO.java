package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.business.model.SearchRequest;
import at.ac.uibk.fiba.ladder3ca.business.model.SearchResult;

import java.util.List;

public class SearchResultDTO {

    public final SearchRequest searchRequest;
    public final int totalHits;
    public final List<SearchHitDTO> hits;

    public SearchResultDTO(SearchRequest searchRequest, int totalHits, List<SearchHitDTO> hits) {
        this.searchRequest = searchRequest;
        this.totalHits = totalHits;
        this.hits = hits;
    }

    public static SearchResultDTO fromSearchResult(SearchResult sr) {
        if (sr == null) {
            return null;
        }
        return new SearchResultDTO(sr.searchRequest, sr.totalHits, SearchHitDTO.fromTextHits(sr.hits));
    }
}

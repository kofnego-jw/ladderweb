package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.index.SearchService;
import at.ac.uibk.fiba.ladder3ca.business.model.SearchRequest;
import at.ac.uibk.fiba.ladder3ca.business.model.SearchResult;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.LadderRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSearchService {
    private final SearchService searchService;
    private final CrudService crudService;

    public UserSearchService(SearchService searchService, CrudService crudService) {
        this.searchService = searchService;
        this.crudService = crudService;
    }

    public SearchResultDTO search(SearchRequest request) {
        try {
            SearchResult search = searchService.search(request);
            return SearchResultDTO.fromSearchResult(search);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public CorpusData createCorpusBySearch(SearchRequest request) {
        try {
            SearchResult result = searchService.search(request);
            List<SubactDTO> subactList = crudService.listAllSubacts();
            List<ModifierDTO> modifierList = crudService.listAllModifiers();
            List<LadderRecord> collect = result.hits.stream()
                    .map(x -> this.createRecord(TextDTO.fromAnnotatedText(x.textMetadata.getText())))
                    .collect(Collectors.toList());
            return new CorpusData(modifierList, subactList, collect);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private LadderRecord createRecord(TextDTO text) {
        TextWithMetadataDTO textWithMetadataDTO = crudService.readOneTextWithMetadata(text.id);
        List<SubactAnnotationDTO> subactAnnotations = crudService.getSubactAnnotationsByText(text.id);
        List<ModifierAnnotationDTO> modifierAnnotations = crudService.getModifierAnnotationsByText(text.id);
        return new LadderRecord(textWithMetadataDTO, modifierAnnotations, subactAnnotations);
    }

    public CorpusData downloadAll() {
        List<SubactDTO> subactList = crudService.listAllSubacts();
        List<ModifierDTO> modifierList = crudService.listAllModifiers();
        List<LadderRecord> collect = crudService.listAllTexts()
                .stream().map(this::createRecord)
                .collect(Collectors.toList());
        return new CorpusData(modifierList, subactList, collect);
    }
}

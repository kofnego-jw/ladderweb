package at.ac.uibk.fiba.ladder3ca.webapp.web;

import at.ac.uibk.fiba.ladder3ca.business.model.SearchRequest;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import at.ac.uibk.fiba.ladder3ca.webapp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @Autowired
    private CrudService crudService;

    @Autowired
    private CorpusDataCSVService corpusDataCSVService;

    @Autowired
    private CorpusDataTeiService corpusDataTeiService;

    @Autowired
    private NlpService nlpService;

    @Autowired
    private UserSearchService userSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/creation_task/")
    public List<CreationTaskDTO> listAllCreationTasks() {
        return crudService.listAllCreationTasks();
    }

    @GetMapping("/creation_task/{id}")
    public CreationTaskDTO readCreationTask(@PathVariable("id") Long id) {
        return crudService.findCreationTaskById(id);
    }

    @PutMapping("/creation_task/{id}")
    public CreationTaskDTO updateCreationTask(@PathVariable("id") Long id, @RequestBody CreationTaskDTO dto) {
        return crudService.updateCreationTask(dto);
    }

    @PostMapping("/creation_task/")
    public CreationTaskDTO creationCreationTask(@RequestBody CreationTaskDTO dto) {
        return crudService.addNewCreationTask(dto);
    }

    @DeleteMapping("/creation_task/{id}")
    public List<CreationTaskDTO> deleteCreationTask(@PathVariable("id") Long id) {
        return crudService.deleteCreationTask(id);
    }

    @GetMapping("/text/")
    public List<TextDTO> listAllTexts() {
        return crudService.listAllTexts();
    }

    @GetMapping("/text/{textId}")
    public TextWithMetadataDTO getOneText(@PathVariable("textId") String textId) {
        return crudService.readOneTextWithMetadata(textId);
    }

    @GetMapping("/text/latest/{count}")
    public List<TextDTO> getLatestModified(@PathVariable("count") Integer count) {
        return crudService.listLatestTexts(count);
    }

    @PutMapping("/text/{textId}")
    public TextWithMetadataDTO updateOneText(@PathVariable("textId") String textId, @RequestBody TextWithMetadataDTO text) {
        return crudService.updateTextWithMetadata(text);
    }

    @PostMapping("/text/")
    public TextWithMetadataDTO addNewText(@RequestBody TextWithMetadataDTO textAndMetadata) {
        return crudService.addNewText(textAndMetadata);
    }

    @DeleteMapping("/text/{textId}")
    public List<TextDTO> deleteText(@PathVariable("textId") String textId) {
        return crudService.deleteTextById(textId);
    }

    @GetMapping("/modifier/")
    public List<ModifierDTO> listAllModifiers() {
        return crudService.listAllModifiers();
    }

    @GetMapping("/modifier/{modifierId}")
    public ModifierDTO getOneModifier(@PathVariable("modifierId") Long modifierId) {
        return crudService.readModifier(modifierId);
    }

    @PutMapping("/modifier/{modifierId}")
    public ModifierDTO updateOneModifier(@PathVariable("modifierId") Long modifierId, @RequestBody ModifierDTO dto) {
        return crudService.updateModifier(dto);
    }

    @PostMapping("/modifier/")
    public ModifierDTO addNewModifier(@RequestBody ModifierDTO dto) {
        return crudService.addNewModifier(dto);
    }

    @DeleteMapping("/modifier/{modifierId}")
    public List<ModifierDTO> deleteModifier(@PathVariable("modifierId") Long modifierId) {
        return crudService.deleteModifierById(modifierId);
    }

    @GetMapping("/subact/")
    public List<SubactDTO> listAllSubacts() {
        return crudService.listAllSubacts();
    }

    @GetMapping("/subact/{subactId}")
    public SubactDTO getOneSubact(@PathVariable("subactId") Long subactId) {
        return crudService.readSubact(subactId);
    }

    @PutMapping("/subact/{subactId}")
    public SubactDTO updateOneSubact(@PathVariable("subactId") Long subactId, @RequestBody SubactDTO dto) {
        return crudService.updateSubact(dto);
    }

    @PostMapping("/subact/")
    public SubactDTO addNewSubact(@RequestBody SubactDTO dto) {
        return crudService.addNewSubact(dto);
    }

    @DeleteMapping("/subact/{subactId}")
    public List<SubactDTO> deleteSubact(@PathVariable("subactId") Long subactId) {
        return crudService.deleteSubactById(subactId);
    }


    @GetMapping("/annotation/{textId}/modifier/")
    public List<ModifierAnnotationDTO> getModifierAnnotationsByText(@PathVariable("textId") String textId) {
        return crudService.getModifierAnnotationsByText(textId);
    }

    @PutMapping("/annotation/{textId}/modifier/")
    @PostMapping("/annotation/{textId}/modifier/")
    public List<ModifierAnnotationDTO> updateModifierAnnotationsByText(@PathVariable("textId") String textId,
                                                                       @RequestBody List<ModifierAnnotationDTO> annotations) {
        return crudService.updateModifierAnnotationByText(textId, annotations);
    }

    @GetMapping("/annotation/{textId}/subact/")
    public List<SubactAnnotationDTO> getSubactAnnotationsByText(@PathVariable("textId") String textId) {
        return crudService.getSubactAnnotationsByText(textId);
    }

    @PutMapping("/annotation/{textId}/subact/")
    @PostMapping("/annotation/{textId}/subact/")
    public List<SubactAnnotationDTO> updateSubactAnnotationsByText(@PathVariable("textId") String textId,
                                                                   @RequestBody List<SubactAnnotationDTO> annotations) {
        return crudService.updateSubactAnnotationByText(textId, annotations);
    }

    @PostMapping("/model/modifier/{modifierId}/{langCode}")
    public SimpleMessageDTO retrainModifierModelByLanguage(@PathVariable("modifierId") Long modifierId, @PathVariable("langCode") String languageCode) {
        return nlpService.retrainModifierModel(modifierId, languageCode);
    }

    @PostMapping("/model/subact/{subactId}/{langCode}")
    public SimpleMessageDTO retrainSubactModelByLanguage(@PathVariable("subactId") Long subactId, @PathVariable("langCode") String languageCode) {
        return nlpService.retrainSubactModel(subactId, languageCode);
    }

    @PostMapping("/model/retrainAll")
    public SimpleMessageDTO retrainAllModels() {
        try {
            String msg = nlpService.retrainAllModels();
            return new SimpleMessageDTO(200, msg, Collections.emptyList());
        } catch (Exception e) {
            return SimpleMessageDTO.errorMessage(400, e.getMessage(), e);
        }
    }

    @GetMapping("/model/retrainAll")
    public SimpleMessageDTO isTrainingRunning() {
        boolean running = nlpService.isTrainingRunning();
        return running ? new SimpleMessageDTO(200, "Training", Collections.emptyList()) :
                new SimpleMessageDTO(200, "Idle", Collections.emptyList());
    }

    @GetMapping("/model/retrainAll/messages")
    public RetrainModelMessagesDTO getRetrainModelMessages() {
        return new RetrainModelMessagesDTO(nlpService.getModelMessages());
    }


    @PostMapping("/annotate/")
    public AnnotatingResultDTO annotateUseModel(@RequestBody AnnotatingRequestDTO request) {
        return nlpService.annotateText(request);
    }

    @PostMapping("/search")
    public SearchResultDTO search(@RequestBody SearchRequest request) {
        return userSearchService.search(request);
    }

    @GetMapping("/corpus/download")
    public void downloadCorpus(HttpServletResponse response) throws Exception {
        CorpusData corpusData = userSearchService.downloadAll();
        sendData(corpusData, response);
    }

    @GetMapping("/corpus/download/csv")
    public void downloadCorpusAsCSV(HttpServletResponse response) throws Exception {
        CorpusData corpusData = userSearchService.downloadAll();
        sendDataAsCSV(corpusData, response);
    }

    @GetMapping("/corpus/download/xmltei")
    public void downloadCorpusAsTEI(HttpServletResponse response) throws Exception {
        CorpusData corpusData = userSearchService.downloadAll();
        sendDataAsTEI(corpusData, null, response);
    }

    @PostMapping("/corpus/download")
    public void downloadDataBySearch(@RequestBody SearchRequest request, HttpServletResponse response) throws Exception {
        CorpusData data = userSearchService.createCorpusBySearch(request);
        sendData(data, response);
    }

    @PostMapping("/corpus/download/csv")
    public void downloadDataBySearchAsCSV(@RequestBody SearchRequest request, HttpServletResponse response) throws Exception {
        CorpusData data = userSearchService.createCorpusBySearch(request);
        sendDataAsCSV(data, response);
    }

    @PostMapping("/corpus/download/xmltei")
    public void downloadDataBySearchAsTEI(@RequestBody SearchRequest request, HttpServletResponse response) throws Exception {
        CorpusData data = userSearchService.createCorpusBySearch(request);
        sendDataAsTEI(data, request, response);
    }

    private void sendData(CorpusData data, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"ladderdata.json\"");
        try (OutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os, data);
        }
    }

    private void sendDataAsCSV(CorpusData data, HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"ladderdata.csv\"");
        try (OutputStream os = response.getOutputStream()) {
            corpusDataCSVService.writeAsCSV(data, os);
        }
    }

    private void sendDataAsTEI(CorpusData data, SearchRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"ladderdata.xml\"");
        try (OutputStream os = response.getOutputStream()) {
            byte[] xml = corpusDataTeiService.toXML(data, request);
            IOUtils.write(xml, os);
        }
    }
}

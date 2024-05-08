package at.ac.uibk.fiba.ladder3ca.webapp.web;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import at.ac.uibk.fiba.ladder3ca.webapp.service.CrudService;
import at.ac.uibk.fiba.ladder3ca.webapp.service.NlpService;
import at.ac.uibk.fiba.ladder3ca.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/api/v1")
public class PublicController {

    @Autowired
    private CrudService crudService;

    @Autowired
    private NlpService nlpService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AppUserDTO login() {
        return userService.getActiveUser();
    }

    @GetMapping("/login")
    public AppUserDTO activeUser() {
        return userService.getActiveUser();
    }

    @GetMapping("/creation_task/")
    public List<CreationTaskDTO> listAllCreationTasks() {
        return crudService.listAllCreationTasks();
    }

    @GetMapping("/creation_task/{id}")
    public CreationTaskDTO readCreationTask(@PathVariable("id") Long id) {
        return crudService.findCreationTaskById(id);
    }

    @GetMapping("/text/")
    public List<TextDTO> listAllTexts() {
        return crudService.listAllTexts();
    }

    @GetMapping("/text/{textId}")
    public TextWithMetadataDTO getOneText(@PathVariable("textId") String textId) {
        return crudService.readOneTextWithMetadata(textId);
    }

    @GetMapping("/modifier/")
    public List<ModifierDTO> listAllModifiers() {
        return crudService.listAllModifiers();
    }

    @GetMapping("/modifier/{modifierId}")
    public ModifierDTO getOneModifier(@PathVariable("modifierId") Long modifierId) {
        return crudService.readModifier(modifierId);
    }

    @GetMapping("/subact/")
    public List<SubactDTO> listAllSubacts() {
        return crudService.listAllSubacts();
    }

    @GetMapping("/subact/{subactId}")
    public SubactDTO getOneSubact(@PathVariable("subactId") Long subactId) {
        return crudService.readSubact(subactId);
    }

    @GetMapping("/annotation/{textId}/modifier/")
    public List<ModifierAnnotationDTO> getModifierAnnotationsByText(@PathVariable("textId") String textId) {
        return crudService.getModifierAnnotationsByText(textId);
    }

    @GetMapping("/annotation/{textId}/subact/")
    public List<SubactAnnotationDTO> getSubactAnnotationsByText(@PathVariable("textId") String textId) {
        return crudService.getSubactAnnotationsByText(textId);
    }

    @PostMapping("/annotate/")
    public AnnotatingResultDTO annotateUseModel(@RequestBody AnnotatingRequestDTO request) {
        return nlpService.annotateText(request);
    }

}

package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.model.AnnotatingResult;
import at.ac.uibk.fiba.ladder3ca.business.model.TokenInfo;
import at.ac.uibk.fiba.ladder3ca.business.service.POSModelsService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Transactional(readOnly = true)
public class NlpService {

    private final ExecutorService myPool = Executors.newSingleThreadExecutor();
    private final CopyOnWriteArrayList<ModelMessage> messages = new CopyOnWriteArrayList<>();
    private final POSModelsService posModelsService;
    private final CrudService crudService;
    private transient CompletableFuture<Void> combinedTasks = CompletableFuture.allOf();
    private transient int totalModelCount = 0;


    public NlpService(POSModelsService posModelsService, CrudService crudService) {
        this.posModelsService = posModelsService;
        this.crudService = crudService;
    }

    private AppUser getPrincipal() {
        return SecurityUtils.getAppUser();
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void checkRunning() {

        if (this.combinedTasks != null) {
            try {
                this.combinedTasks.get();
            } catch (Exception ignored) {

            }
            this.combinedTasks = null;
        }
    }

    public boolean isTrainingRunning() {
        return this.combinedTasks != null;
    }

    public List<ModelMessage> getModelMessages() {
        return this.messages;
    }

    public String retrainAllModels() throws Exception {
        SecurityUtils.needUserOrAdmin();
        if (this.isTrainingRunning()) {
            // Already running
            throw new Exception("Retraining already running.");
        }
        this.messages.clear();
        List<ModelTrainRunnable> myTrainingTasks = new ArrayList<>();
        List<ModifierDTO> modifiers = crudService.listAllModifiers();
        for (ModifierDTO modifier : modifiers) {
            myTrainingTasks.add(new ModelTrainRunnable(ModelType.MODIFIER, modifier.id, modifier.modifierCode,
                    "it", getPrincipal(), posModelsService));
            myTrainingTasks.add(new ModelTrainRunnable(ModelType.MODIFIER, modifier.id, modifier.modifierCode,
                    "de", getPrincipal(), posModelsService));
        }
        List<SubactDTO> subacts = crudService.listAllSubacts();
        for (SubactDTO subact : subacts) {
            myTrainingTasks.add(new ModelTrainRunnable(ModelType.SUBACT, subact.id, subact.subactName,
                    "it", getPrincipal(), posModelsService));
            myTrainingTasks.add(new ModelTrainRunnable(ModelType.SUBACT, subact.id, subact.subactName,
                    "de", getPrincipal(), posModelsService));
        }
        this.totalModelCount = myTrainingTasks.size();
        if (this.totalModelCount > 0) {
            List<CompletableFuture<ModelMessage>> tasks = new ArrayList<>();
            myTrainingTasks.forEach(t -> {
                try {
                    CompletableFuture<ModelMessage> modelMessageCompletableFuture = CompletableFuture.supplyAsync(t::call, myPool)
                            .thenApply(x -> {
                                this.messages.add(x);
                                return x;
                            });
                    tasks.add(modelMessageCompletableFuture);
                } catch (Exception e) {
                    this.messages.add(new ModelMessage(t.modelType, t.modelName, t.language, List.of("ERROR")));
                }
            });
            this.combinedTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
            return "Total " + this.totalModelCount + " models are being trained.";
        } else {
            this.combinedTasks = null;
            throw new Exception("No model found to be trained.");
        }
    }


    public SimpleMessageDTO retrainModifierModel(Long modifierId, String language) {
        SecurityUtils.needUserOrAdmin();
        try {
            posModelsService.retrainModifierModel(modifierId, language, getPrincipal());
            return SimpleMessageDTO.OKAY;
        } catch (Exception e) {
            return SimpleMessageDTO.errorMessage(500, "Cannot train modifier model.", e);
        }
    }

    public SimpleMessageDTO retrainSubactModel(Long subactId, String language) {
        SecurityUtils.needUserOrAdmin();
        try {
            posModelsService.retrainSubactModel(subactId, language, getPrincipal());
            return SimpleMessageDTO.OKAY;
        } catch (Exception e) {
            return SimpleMessageDTO.errorMessage(500, "Cannot train modifier model.", e);
        }
    }

    public AnnotatingResultDTO annotateText(AnnotatingRequestDTO request) {
        try {
            List<Long> modifierIds = request.modifierIds;
            List<TokenInfo> tokens = new ArrayList<>();
            for (Long id : modifierIds) {
                AnnotatingResult result = posModelsService.annotateModifier(request.text, id, request.language, request.useMax);
                addResultToTokenInfo(tokens, result);
            }
            for (Long id : request.subactIds) {
                AnnotatingResult result = posModelsService.annotateSubact(request.text, id, request.language, request.useMax);
                addResultToTokenInfo(tokens, result);
            }
            AnnotatingResult result = new AnnotatingResult(request.text, tokens);
            return new AnnotatingResultDTO(request, result);
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot annotate text.", e);
        }
    }

    private void addResultToTokenInfo(List<TokenInfo> tokens, AnnotatingResult result) {
        if (tokens.isEmpty()) {
            tokens.addAll(result.tokenInfos);
        } else {
            for (int i = 0; i < result.tokenInfos.size(); i++) {
                TokenInfo info = result.tokenInfos.get(i);
                TokenInfo token = tokens.get(i);
                if (!info.tags.isEmpty()) {
                    for (String tag : info.tags) {
                        token.addTag(tag);
                    }
                }
                if (!info.modifierIds.isEmpty()) {
                    for (Long id : info.modifierIds) {
                        token.addModifierId(id);
                    }
                }
                if (!info.subactIds.isEmpty()) {
                    for (Long id : info.subactIds) {
                        token.addSubactId(id);
                    }
                }
            }
        }
    }

    public static final class ModelTrainRunnable implements Callable<ModelMessage> {
        private final ModelType modelType;
        private final Long id;
        private final String modelName;
        private final String language;
        private final AppUser principal;
        private final POSModelsService posModelsService;

        public ModelTrainRunnable(ModelType modelType, Long id, String modelName, String language, AppUser principal, POSModelsService posModelsService) {
            this.modelType = modelType;
            this.id = id;
            this.modelName = modelName;
            this.language = language;
            this.principal = principal;
            this.posModelsService = posModelsService;
        }

        @Override
        public ModelMessage call() {
            List<String> msgs = new ArrayList<>();
            msgs.add("Training start on: " + LocalDateTime.now());
            try {
                switch (modelType) {
                    case MODIFIER -> posModelsService.retrainModifierModel(id, language, principal);
                    case SUBACT -> posModelsService.retrainSubactModel(id, language, principal);
                }
                msgs.add("Traning finished on " + LocalDateTime.now());
            } catch (Exception e) {
                msgs.add("[ERROR] on " + LocalDateTime.now());
                msgs.add(e.getMessage());
            }
            return new ModelMessage(modelType, modelName, language, msgs);
        }
    }

}

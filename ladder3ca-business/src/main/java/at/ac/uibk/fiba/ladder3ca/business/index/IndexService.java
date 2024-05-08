package at.ac.uibk.fiba.ladder3ca.business.index;

import at.ac.uibk.fiba.ladder3ca.business.model.LadderField;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.ModifierAnnotation;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.SubactAnnotation;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class IndexService implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
    private static final FieldType KEYWORD;

    static {
        KEYWORD = new FieldType();
        KEYWORD.setStored(true);
        KEYWORD.setIndexOptions(IndexOptions.DOCS);
        KEYWORD.setTokenized(false);
        KEYWORD.freeze();
    }

    private final AnnotatedTextService textService;
    private final File luceneDir;
    private transient LocalDateTime lastIndexed;

    public IndexService(AnnotatedTextService textService, File luceneDir) {
        this.luceneDir = luceneDir;
        this.textService = textService;
    }

    private static String joinStringList(List<String> strings) {
        if (strings == null) {
            return "";
        }
        return String.join(" / ", strings);
    }

    private static long parseLong(String content) {
        try {
            return Long.parseLong(content);
        } catch (Throwable e) {
            return 0L;
        }
    }

    private static String stringOf(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedDelay = 1000L * 60 * 2)
    public void indexTexts() {
        LOGGER.debug("Start indexing.");
        purgeDeletedDocuments();
        List<AnnotatedText> texts =
                lastIndexed == null ? textService.listAll() :
                        textService.listSince(lastIndexed);
        lastIndexed = LocalDateTime.now();
        doIndex(texts);
        LOGGER.debug("End indexing.");
    }

    private void doIndex(List<AnnotatedText> texts) {
        for (AnnotatedText text : texts) {
            try {
                TextMetadata metadata = textService.findTextMetadataById(text.getId());
                updateLuceneIndex(metadata);
            } catch (Exception e) {
                LOGGER.error("Cannot index a document.", e);
            }
        }
    }

    private IndexableField createField(LadderField fieldname, String content) {
        return switch (fieldname.fieldType) {
            case TEXT -> new TextField(fieldname.fieldname, content, Field.Store.YES);
            case BOOLEAN -> new StringField(fieldname.fieldname, content, Field.Store.YES);
            case KEYWORD, NUMBER -> new StoredField(fieldname.fieldname, content, KEYWORD);
        };
    }

    private IndexableField createField(LadderField fieldname, Number content) {
        return switch (fieldname.fieldType) {
            case TEXT -> new TextField(fieldname.fieldname, content.toString(), Field.Store.YES);
            case BOOLEAN -> new StringField(fieldname.fieldname, content.toString(), Field.Store.YES);
            case KEYWORD, NUMBER -> new StoredField(fieldname.fieldname, Long.toString(content.longValue()), KEYWORD);
        };
    }

    public Directory getDirectory() throws Exception {
        return FSDirectory.open(luceneDir.toPath());
    }

    private Analyzer getAnalyzer() {
        return LadderAnalyzer.getAnalyzer();
    }

    private IndexWriterConfig getIndexWriterConfig() {
        Analyzer analyzer = getAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCommitOnClose(true);
        return config;
    }

    private IndexWriter getIndexWriter() throws Exception {
        Directory directory = getDirectory();
        IndexWriterConfig config = getIndexWriterConfig();
        return new IndexWriter(directory, config);
    }

    private List<IndexableField> createFields(LadderField field, String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        if (field.multiValued) {
            String[] contents = content.split("(,|;)");
            return Stream.of(contents)
                    .map(x -> createField(field, x.trim()))
                    .collect(Collectors.toList());
        }
        return List.of(createField(field, content.trim()));
    }

    private List<IndexableField> createFields(LadderField field, Number content) {
        if (content == null) {
            return Collections.emptyList();
        }
        return List.of(createField(field, content));
    }

    private List<IndexableField> createFields(LadderField field, List<Number> content) {
        if (content == null) {
            return Collections.emptyList();
        }
        return content.stream().map(x -> createField(field, x)).collect(Collectors.toList());
    }

    private Document createDocument(TextMetadata textMetadata, List<ModifierAnnotation> modifierAnnotations,
                                    List<SubactAnnotation> subactAnnotations) {
        Document document = new Document();
        document.add(createField(LadderField.ID, textMetadata.getId()));
        createFields(LadderField.ALT_ID, textMetadata.getText().getAltId()).forEach(document::add);
        createFields(LadderField.LANGUAGE, textMetadata.getText().getLanguageCode()).forEach(document::add);
        createFields(LadderField.TEXT_CONTENT, textMetadata.getText().getTextdata()).forEach(document::add);
        createFields(LadderField.CREATION_TASK, textMetadata.getText().getCreationTaskId()).forEach(document::add);
        createFields(LadderField.LAST_MODIFIED, textMetadata.getText().getLastModifiedNumber()).forEach(document::add);
        createFields(LadderField.SPEAKER_GENDER, stringOf(textMetadata.getGender())).forEach(document::add);
        createFields(LadderField.L1_LANGUAGE, textMetadata.getL1Language()).forEach(document::add);
        createFields(LadderField.L2_LANGUAGE, textMetadata.getL2Languages()).forEach(document::add);
        createFields(LadderField.LOCATION, textMetadata.getLocation()).forEach(document::add);

        List<Number> modifiers = modifierAnnotations.stream().map(x -> x.getModifier().getId()).distinct().collect(Collectors.toList());
        List<Number> subacts = subactAnnotations.stream().map(x -> x.getSubact().getId()).distinct().collect(Collectors.toList());

        createFields(LadderField.MODIFIERS, modifiers).forEach(document::add);
        createFields(LadderField.SUBACTS, subacts).forEach(document::add);
        return document;
    }

    private void purgeDeletedDocuments() {
        List<String> textIds = textService.listAll().stream().map(AnnotatedText::getId).collect(Collectors.toList());
        try (IndexWriter indexWriter = getIndexWriter()) {
            DirectoryReader reader = DirectoryReader.open(indexWriter);
            List<String> toDelete = new ArrayList<>();
            for (int i = 0; i < reader.numDocs(); i++) {
                Document doc = reader.document(i);
                IndexableField docField = doc.getField(LadderField.ID.fieldname);
                if (!textIds.contains(docField.stringValue())) {
                    toDelete.add(docField.stringValue());
                }
            }
            for (String id : toDelete) {
                Term idTerm = new Term(LadderField.ID.fieldname, id);
                indexWriter.deleteDocuments(idTerm);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while deleting document.", e);
        }
    }

    public synchronized void updateLuceneIndex(TextMetadata text) {
        LOGGER.info("Update annotated text {}.", text.getId());
        try (IndexWriter indexWriter = getIndexWriter()) {
            List<SubactAnnotation> subactAnnotations = textService.listSubactAnnotationsByText(text.getId());
            List<ModifierAnnotation> modifierAnnotations = textService.listModifierAnnotationsByText(text.getId());
            Document document = createDocument(text, modifierAnnotations, subactAnnotations);
            Term idTerm = new Term(LadderField.ID.fieldname, text.getId());
            indexWriter.updateDocument(idTerm, document);
            indexWriter.commit();
        } catch (Exception e) {
            LOGGER.warn("Error during indexing.", e);
        }
    }

    @Override
    public void close() throws Exception {
        getDirectory().close();
    }

    public void clearIndex() throws Exception {
        LOGGER.warn("Clear index.");
        try (IndexWriter indexWriter = getIndexWriter()) {
            indexWriter.deleteAll();
            indexWriter.commit();
        }
    }

}

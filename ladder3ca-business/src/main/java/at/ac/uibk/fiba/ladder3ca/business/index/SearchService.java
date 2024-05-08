package at.ac.uibk.fiba.ladder3ca.business.index;

import at.ac.uibk.fiba.ladder3ca.business.model.*;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchService implements AutoCloseable {

    private final File luceneDir;
    private final AnnotatedTextService textService;

    public SearchService(File luceneDir, AnnotatedTextService textService) {
        this.luceneDir = luceneDir;
        this.textService = textService;
    }

    private Directory getDirectory() throws Exception {
        return FSDirectory.open(luceneDir.toPath());
    }

    private DirectoryReader getIndexReader() throws Exception {
        return DirectoryReader.open(getDirectory());
    }

    private Analyzer getAnalyzer() {
        return LadderAnalyzer.getAnalyzer();
    }

    private List<String> highlight(Query q, IndexableField[] fields) {
        if (fields == null) {
            return Collections.emptyList();
        }
        Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
        QueryScorer scorer = new QueryScorer(q);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        List<String> highlighted = new ArrayList<>();
        for (IndexableField field : fields) {
            try {
                String[] bestFragments = highlighter.getBestFragments(getAnalyzer(), field.name(), field.stringValue(),
                        3);
                if (bestFragments != null) {
                    Collections.addAll(highlighted, bestFragments);
                }
            } catch (Exception e) {
                // ignore this
            }
        }
        return highlighted;
    }

    public SearchResult search(SearchRequest query) throws Exception {
        try (IndexReader indexReader = getIndexReader()) {
            IndexSearcher searcher = new IndexSearcher(indexReader);
            Query q = makeQuery(query);
            TopDocs result = searcher.search(q, indexReader.numDocs(), Sort.RELEVANCE);
            ScoreDoc[] hits = result.scoreDocs;
            List<TextHit> resultHits = new ArrayList<>(hits.length);
            int totalHitCount = (int) result.totalHits.value;
            for (ScoreDoc hit : hits) {
                Document document = indexReader.document(hit.doc);
                String id = document.getField(LadderField.ID.fieldname).stringValue();
                TextMetadata metadata;
                try {
                    metadata = textService.findTextMetadataById(id);
                } catch (Exception e) {
                    continue;
                }
                List<String> previews = new ArrayList<>();
                IndexableField[] fields = document.getFields(LadderField.TEXT_CONTENT.fieldname);
                if (fields != null) {
                    List<String> highlight = highlight(q, fields);
                    previews.addAll(highlight);
                }
                TextHit searchHit = new TextHit(metadata, previews);
                resultHits.add(searchHit);
            }
            return new SearchResult(query, totalHitCount, resultHits);
        }
    }

    public Query makeQuery(SearchRequest query) {
        if (query == null || query.clauses == null || query.clauses.isEmpty()) {
            return new MatchNoDocsQuery();
        }
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (SearchClause clause : query.clauses) {
            try {
                QueryParser parser = new QueryParser(clause.field.fieldname, getAnalyzer());
                Query parsedQuery = parser.parse(clause.queryString);
                BooleanClause.Occur occur = switch (clause.mode) {
                    case AND -> BooleanClause.Occur.MUST;
                    case OR -> BooleanClause.Occur.SHOULD;
                };
                builder.add(new BooleanClause(parsedQuery, occur));
            } catch (Exception ignored) {
            }
        }
        return builder.build();
    }

    @Override
    public void close() throws Exception {
        getDirectory().close();
    }
}

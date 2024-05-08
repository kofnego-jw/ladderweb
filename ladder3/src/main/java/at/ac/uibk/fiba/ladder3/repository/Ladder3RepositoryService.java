package at.ac.uibk.fiba.ladder3.repository;

import at.ac.uibk.fiba.ladder3.Ladder3Exception;
import at.ac.uibk.fiba.ladder3.model.Ladder3Repository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Ladder3RepositoryService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final File repositoryFile;

    public Ladder3RepositoryService(File repositoryFile) {
        this.repositoryFile = repositoryFile;
    }

    public Ladder3Repository readFromFile() throws Ladder3Exception {
        try {
            Ladder3Repository repository = OBJECT_MAPPER.readValue(repositoryFile, Ladder3Repository.class);
            return repository;
        } catch (Exception e) {
            throw new Ladder3Exception("Cannot read from JSON file.", e);
        }
    }

    public Ladder3Repository saveRepository(Ladder3Repository repository) throws Ladder3Exception {
        if (repository == null) {
            throw new Ladder3Exception("Cannot save null object.");
        }
        try (OutputStream os = new FileOutputStream(this.repositoryFile)) {
            OBJECT_MAPPER.writeValue(os, repository);
            return readFromFile();
        } catch (Exception e) {
            throw new Ladder3Exception("Cannot save file.", e);
        }
    }
}

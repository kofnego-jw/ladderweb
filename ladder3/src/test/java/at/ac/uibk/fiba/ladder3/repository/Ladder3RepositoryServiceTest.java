package at.ac.uibk.fiba.ladder3.repository;

import at.ac.uibk.fiba.ladder3.TestHelper;
import at.ac.uibk.fiba.ladder3.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class Ladder3RepositoryServiceTest {

    private static final File REPO_FILE = new File("target/testRepo.json");
    private TestHelper testHelper = new TestHelper();

    private Ladder3RepositoryService repositoryService = new Ladder3RepositoryService(REPO_FILE);

    @Test
    public void test() throws Exception {
        List<Modifier> modifiers = testHelper.randomModifiers(4,5);
        List<Subact> subacts = testHelper.randomSubacts(3,5, 0, 4);
        List<Pool> pools = testHelper.randomPools(2);
        List<Evidence> evidences = testHelper.randomEvidences(100, pools);
        List<ModifierMarking> modifierMarkings = testHelper.randomModifierMarkings(modifiers, evidences, 0, 20, 0, 2);
        List<SubactMarking> subactMarkings = testHelper.randomSubactMarkings(subacts, evidences, 0, 10, 1, 3);
        Ladder3Repository repository = new Ladder3Repository(modifiers, subacts, pools, evidences, modifierMarkings, subactMarkings);
        repositoryService.saveRepository(repository);
        Thread.sleep(500);
        Ladder3Repository repo2 = repositoryService.readFromFile();
        for (SubactMarking m: repo2.subactMarkings) {
            Assertions.assertTrue(repository.subactMarkings.contains(m));
        }
        for (ModifierMarking m: repo2.modifierMarkings) {
            Assertions.assertTrue(repository.modifierMarkings.contains(m));
        }

    }
}

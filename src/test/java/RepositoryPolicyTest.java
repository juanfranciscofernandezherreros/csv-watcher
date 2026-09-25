import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryPolicyTest {
    @Test
    void repositoryKeepsTheSharedCsvContract() throws Exception {
        String pom = Files.readString(Path.of("pom.xml"));
        String readme = Files.readString(Path.of("README.md"));
        String changelog = Files.readString(Path.of("CHANGELOG.md"));
        assertTrue(pom.contains("<java.version>21</java.version>"));
        assertTrue(readme.contains("mvn -B test"));
        assertTrue(Files.exists(Path.of("AGENTS.md")));
        assertTrue(Files.exists(Path.of(".github/workflows/documentation-policy.yml")));
        assertTrue(Files.exists(Path.of(".github/workflows/test.yml")));
    }
}

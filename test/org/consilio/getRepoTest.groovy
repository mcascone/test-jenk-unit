import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*

class GetRepoTest extends BasePipelineTest {
  def getRepo

  @Before
  void setUp() {
    super.setUp()
    // set up mocks
    def reponame = 'myRepoName'
    helper.registerAllowedMethod("pwsh", [Boolean, String, String], { p -> return reponame })

    // load getRepo
    getRepo = loadScript("vars/getRepo.groovy")
  }

  @Test
  void testCall() {
    // call getRepo and check result
    def result = getRepo()
    assert 'myRepoName'.equals(result)
  }
}

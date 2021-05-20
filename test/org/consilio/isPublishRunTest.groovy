import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*

class IsPublishRunTest extends BasePipelineTest {
  def isPublishRun

  @Before
  void setUp() {
    super.setUp()
    // set up mocks
    // load isPublishRun
    isPublishRun = loadScript("vars/isPublishRun.groovy")
  }

  @Test
  void developBranchNotDeployRun() {
    // set up mocks
    def branch = 'develop'
    helper.registerAllowedMethod('isDeployRun', {false})

    // call isPublishRun and check result
    def result = isPublishRun(branch)
    assert result == true
  }

  @Test
  void developBranchDeployRun() {
    // set up mocks
    def branch = 'develop'
    helper.registerAllowedMethod('isDeployRun', {true})

    // call isPublishRun and check result
    def result = isPublishRun(branch)
    assert result == false
  }

  @Test
  void featureBranchDeployRun() {
    // set up mocks
    def branch = 'some-feature'
    helper.registerAllowedMethod('isDeployRun', {true})

    // call isPublishRun and check result
    def result = isPublishRun(branch)
    assert result == false
  }

  @Test
  void featureBranchNotDeployRun() {
    // set up mocks
    def branch = 'some-feature'
    helper.registerAllowedMethod('isDeployRun', {false})

    // call isPublishRun and check result
    def result = isPublishRun(branch)
    assert result == false
  }
}

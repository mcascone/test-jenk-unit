import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*

class ToAlphanumericTest extends BasePipelineTest {
    def toAlphanumeric

    @Before
    void setUp() {
        super.setUp()
        // load toAlphanumeric
        toAlphanumeric = loadScript("vars/toAlphanumeric.groovy")
    }

    @Test
    void testCall() {
        // call toAlphanumeric and check result
        def result = toAlphanumeric(text: "a_B-c.1")
        // assertEquals "result:", "abc1", result // this is old school. Prefer groovy Power Assert:
        assert 'abc1' == result
    }
    
    @Test
    void testInverseCall() {
        def result = toAlphanumeric(text: "a_B-c.1")
        assert 'abc1.' != result
    }
}

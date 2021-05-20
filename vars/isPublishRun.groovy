// We only want to publish to package repository (ProGet) under these conditions.
// Returns true/false
// TRUE if branch is develop, release* or hotfix* AND this is not a deploy run.
// Can also set doNotPublish in the Jenkinsfile to return false, if this doesn't need to be published.
// This will probably have to be revisited for CD.
def call(branch = env.BRANCH_NAME, noPublish = env.doNotPublish) {
  return branch ==~ /develop|release.*|hotfix.*/ && !isDeployRun() && !noPublish?.toBoolean()
}

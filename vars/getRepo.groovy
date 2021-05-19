// This is a bit of intentional overkill for such a simple script,
// left this way as an example of the libraryResource pattern.
def call() {
  def repo = pwsh returnStdout: true, label: 'get repo', script: "${libraryResource 'Get-Repo.ps1'}"

  return repo.trim()
}

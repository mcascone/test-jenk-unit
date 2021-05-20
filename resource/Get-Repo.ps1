## Get-Repo
## Returns the name of the git repo you are located in.
(git remote get-url origin | Split-Path -Leaf).replace('.git', '')

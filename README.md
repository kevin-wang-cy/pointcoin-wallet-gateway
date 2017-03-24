
# GIT CONFIGURATION

```
  git config --global branch.autosetuprebase always
  git config --global branch.master.rebase true
  git config --global push.default simple
```

# GIT FLOW

## Outlines

1. Anything in the master branch is deployable
2. To work on something new, create a descriptively named branch off of master (ie: new-oauth2-scopes)
3. Commit to that branch locally and regularly push your work to the same named branch on the server
4. When you need feedback or help, or you think the branch is ready for merging, open a pull request
5. After someone else has reviewed and signed off on the feature, you can merge it into master
6. Once it is merged and pushed to master, you can and should deploy immediately

## Quick Commands

### 1. create branch off of master
```
git checkout -b dev/feature-1 origin/master
git push -u origin dev/feature-1
```
### 2. download others change and merge into local
```
git checkout dev/feature-1
git pull
```
### 3. commit local change and push change from local to server
```
git add .
git commit -m '#ticket number with descriptive statement'
git push
```
### 4. merge master into feature branch and solve conflict before create pull request
```
git fetch origin
git merge origin/master`ddd


dafdfadadfafsdaafsdafasdfdsasdfdssafdsfdasdaffsdads1111111c0                fddsgsdfgfdgsfdgommit -m '#ticket-number solve conflicts before merge'
git push
```
### 5. create pull request as suggested [here](https://help.github.com/articles/creating-a-pull-request/)
### 6. merge pull request as suggested [here](https://help.github.com/articles/merging-a-pull-request/)

# Reference

1. Please check github recommend [git flow](https://help.github.com/articles/github-flow/)
2. [Here](http://scottchacon.com/2011/08/31/github-flow.html) is another reference


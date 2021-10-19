## git log 打印

```
//最近一次log
git log -p -1 > log.log

//git log --stat 相比git log 可以查看每次提交对应修改的文件列表，修改的行数等
git log --stat -1 最近一次
```

## git拉取

```
//
repo forall -c: 此命令遍历所有的git仓库，并在每个仓库执行-c所指定的命令，被执行的命令不限于git命令，而是任何被系统支持的命令，比如：ls, git log, git status等
repo forall -c git checkout "分支名"
repo forall -c 'git pull origin 9178WF_20200105' 

//拉取代码3步
repo_mtk  init -u  gitaddress/9178WF/manifests.git -b "支线名称"
sudo repo_mtk init -u gitlite:thirdproject/9178WF_20200105/manifests.git -b 9178WF_20200105
repo_mtk sync -c -f
repo_mtk start --all 9178WF_20200105

//在现有代码线上 拉取分支代码
repo_mtk forall -c git fetch origin “分支”:“分支”  
//eg.
repo_mtk forall -c git fetch origin 9178WF_20200227_TO_V9.2:9178WF_20200227_TO_V9.2

切换基线
repo_mtk forall -c git fetch origin xxx:xxx
repo_mtk forall -c git fetch origin 9178WF_20200227_TO_V9.2:9178WF_20200227_TO_V9.2
repo_mtk forall -c git checkout 9178WF_N5003L_20200227_TO_V9.2
```

## git拉取配置

```
host gitlite
user gitolite
hostname 58.215.227.27
IdentityFile /home/nuu/.ssh/id_rsa
IdentitiesOnly yes


```

## git取消commit

1、还没有push，只是在本地commit

```
git的版本管理，及HEAD的理解
使用git的每次提交，Git都会自动把它们串成一条时间线，这条时间线就是一个分支。如果没有新建分支，那么只有一条时间线，即只有一个分支，在Git里，这个分支叫主分支，即master分支。有一个HEAD指针指向当前分支（只有一个分支的情况下会指向master，而master是指向最新提交）。每个版本都会有自己的版本信息，如特有的版本号、版本名等。
```



```
回退（reset）、反做（revert）

git reset --soft|--mixed|--hard <commit_id>
git push develop develop --force  (本地分支和远程分支都是 develop)

这里的<commit_id>就是每次commit的SHA-1，可以在log里查看到
--mixed  会保留源码,只是将git commit和index 信息回退到了某个版本.
--soft   保留源码,只回退到commit信息到某个版本.不涉及index的回退,如果还需要提交,直接commit即可.
--hard   源码也会回退到某个版本,commit和index 都会回退到某个版本.(注意,这种方式是改变本地代码仓库源码)

当然有人在push代码以后,也使用 reset --hard <commit...> 回退代码到某个版本之前,但是这样会有一个问题,你线上的代码没有变,线上commit,index都没有变,当你把本地代码修改完提交的时候你会发现全是冲突.....这时换下一种

git reset --hard HEAD^         回退到上个版本
git reset --hard HEAD~3        回退到前3次提交之前，以此类推，回退到n次提交之前
git reset --hard commit_id     退到/进到 指定commit的sha码
强推到远程：
git push origin HEAD --force
git push -f强制推
```

### git reset

```
git reset的作用是修改HEAD的位置，即将HEAD指向的位置改变为之前存在的某个版本
适用场景： 如果想恢复到之前某个提交的版本，且那个版本之后提交的版本我们都不要了，就可以用这种方法。
```

### **git revert**

```
原理： git revert是用于“反做”某一个版本，以达到撤销该版本的修改的目的。比如，我们commit了三个版本（版本一、版本二、 版本三），突然发现版本二不行（如：有bug），想要撤销版本二，但又不想影响撤销版本三的提交，就可以用 git revert 命令来反做版本二，生成新的版本四，这个版本四里会保留版本三的东西，但撤销了版本二的东西。

适用场景： 如果我们想撤销之前的某一版本，但是又想保留该目标版本后面的版本，记录下这整个版本变动流程，就可以用这种方法。
```

2、commit push 代码已经更新到远程仓库

对于已经把代码push到线上仓库,你回退本地代码其实也想同时回退线上代码,回滚到某个指定的版本,线上,线下代码保持一致.你要用到下面的命令

```
git revert <commit_id>

git revert HEAD                撤销前一次 commit
git revert HEAD^               撤销前前一次 commit
git revert commit （比如：fa042ce57ebbe5bb9c8db709f719cec2c58ee7ff）撤销指定的版本，撤销也会作为一次提交进行保存。

revert 之后你的本地代码会回滚到指定的历史版本,这时你再 git push 既可以把线上的代码更新。

注意：git revert是用一次新的commit来回滚之前的commit，git reset是直接删除指定的commit，看似达到的效果是一样的,其实完全不同。

第一:上面我们说的如果你已经push到线上代码库, reset 删除指定commit以后,你git push可能导致一大堆冲突.但是revert 并不会.
第二:如果在日后现有分支和历史分支需要合并的时候,reset 恢复部分的代码依然会出现在历史分支里.但是revert 方向提交的commit 并不会出现在历史分支里.
第三:reset 是在正常的commit历史中,删除了指定的commit,这时 HEAD 是向后移动了,而 revert 是在正常的commit历史中再commit一次,只不过是反向提交,他的 HEAD 是一直向前的.
```

## git修改最后一次注释

```
git commit --amend

git commit --amend  # 会通过 core.editor 指定的编辑器进行编辑
git commit --amend --no-edit   # 不会进入编辑器，直接进行提交
```


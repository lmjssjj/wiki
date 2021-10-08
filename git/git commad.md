## log

```
//最近一次log
git log -p -1 > log.log

//git log --stat 相比git log 可以查看每次提交对应修改的文件列表，修改的行数等
git log --stat -1 最近一次
```

## 拉取

```
//
repo forall -c: 此命令遍历所有的git仓库，并在每个仓库执行-c所指定的命令，被执行的命令不限于git命令，而是任何被系统支持的命令，比如：ls, git log, git status等

//拉取代码
repo_mtk  init -u  gitaddress/9178WF/manifests.git -b 支线名称
sudo repo_mtk init -u gitlite:thirdproject/9178WF_20200105/manifests.git -b 9178WF_20200105

//在现有代码线上 拉取分支代码
repo_mtk forall -c git fetch origin “分支”:“分支”  
//eg.
repo_mtk forall -c git fetch origin 9178WF_20200227_TO_V9.2:9178WF_20200227_TO_V9.2


```


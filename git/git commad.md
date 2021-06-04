```
//最近一次log
git log -p -1 > log.log

//拉取代码
repo_mtk  init -u  gitaddress/9178WF/manifests.git -b 支线名称
sudo repo_mtk init -u gitlite:thirdproject/9178WF_20200105/manifests.git -b 9178WF_20200105

//在现有代码线上 拉取分支代码
repo_mtk forall -c git fetch origin “分支”:“分支”  
//eg.
repo_mtk forall -c git fetch origin 9178WF_20200227_TO_V9.2:9178WF_20200227_TO_V9.2


```


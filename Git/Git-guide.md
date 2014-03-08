Git使用指南
------------
[TOC]

--------------
###1. 配置
    git config --global user.name "your name"
    git config --global user.email "test@126.com"
    git config --global core.editor vim
    git config --global merge.tool vimdiff 
>- git config --list   查看配置的变量
>- git help `verb`     查看帮助

###2. 克隆仓库
1）本地建立目录code:
&emsp;    git init &emsp;在此目录下添加.git文件夹
2）执行`git clone git@github.com:youname/gitrep.git .`克隆远端仓库
3) 在本地目录做更改后执行`git push origin master` 将本地分支推送的服务器
> **remote**相关的命令

> - git remote -v   &emsp;   列出此拷贝对应的URL信息
> - git remote add [shotname] [url]     &emsp;  使用一个简单名字来引用url对应的仓库
> - git fetch [remote-name]     &emsp;   抓取从你上次克隆到现在别人上传的代码到本地
> - git pull [remote-name]  &emsp;  获取更新
> - git push [remote-name] [branch] &emsp;  将本地的分支推送到远程仓库中
> - git remote show [remote-name]  &emsp;  查看远程仓库的信息

###3. 提交更改
文件有3个状态：已修改，已暂存，已提交
可以通过命令`git status`来查看本地仓库内文件的状态
> - git add file/dir  &emsp; 跟踪文件或目录；若文件已跟踪且被更改，则暂存文件
> - git commit  会弹出默认编辑器，让用户输入提交说明
> - git commit –m “comment”  在提交的同时，指明提交说明
> - git commit –a –m     跳过暂存区域，直接将已跟踪且修改过的文件提交
> - git commit –-amend –m “modify the latest commit”                 
修改最后一次提交的信息，但是自从上次提交后，中间不能有任何操作；

<!--- 误操作的恢复 --->
1）取消对某文件的暂存
&emsp; `git reset HEAD <file>`
2）撤销对本地文件的修改（慎用，会直接删除本地的修改）
&emsp; `git checkout -- <file>`
###4. 打标签
给某一时间点的文档打上一个标签，便于今后的查看和使用;
git中的标签分为轻量级标签和含附注的标签
>- git tag&emsp;列出现有仓库的标签
>- git tag -l 'v1.4.*'  &emsp; 列出v1.4相关的标签
>- git tag -a v1.2 -m "附注"  &emsp; 打一个v1.2的标签，带附注
>- git tag v1.2   &emsp; 打一个轻量级的标签
>- git show v1.2   &emsp; 查看v1.2标签的描述信息

注：<!---默认情况下git不会将本地的标签推送到远端服务器中的--->，只有通过显示命令才能将标签推送到远端仓库，与别人分享。
git push origin [tagname]
git push origin - -tags   &emsp; 将本地所有标签都推送到远端仓库中

-------------------
###附录
####github管理
1. 如何删除Github中的仓库？
    http://www.oschina.net/question/256591_103418
2. 
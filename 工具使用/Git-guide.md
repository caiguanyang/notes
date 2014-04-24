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
2）执行`git clone git@github.com:youname/gitrep.git .`克隆远端仓库  （可以考虑值克隆最新版本，不克隆历史，如指定参数 --depth）
3) 在本地目录做更改后执行`git push origin master` 将本地分支推送的服务器  

> **remote**相关的命令

> - git remote -v   &emsp;   列出此拷贝对应的URL信息
> - git remote add [shotname] [url]     &emsp;  使用一个简单名字来引用url对应的仓库
> - git fetch [remote-name]     &emsp;   抓取从你上次克隆到现在别人上传的代码到本地，默认为origin/master分支，在检查更新后，可以执行合并分支操作，参考5--分支
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

###5.分支
多人协作开发/分模块开发，可以每人/为每个模块在主干的基础上创建一个分支，然后都在自己的分支上开发功能模块，当模块开发完成，并且测试通 过时可以将此分支合并到主目录中。
>- git branch name &emsp; 创建分支name
>- git checkout bname &emsp; 切换到分支bname;git也会将目录中的文件切换到这个分支的状态，这样就可以在不需要复制文件的情况下直接在目录内开发了
>- git merge bname &emsp; 合并bname分支到当前分支中，遇到冲突可以在本地文件中手动解决冲突
>- git mergetool &emsp; 图形化界面解决冲突

**分支管理**
>- git branch &emsp; 查看所有的分支信息，带星号的标示当前分支
>- git branch --merged &emsp; 查看以合并到当前分支的分支，可以在master上查看
>- git branch --no-merged &emsp; 与上面的命令相反
>- git branch -d branchName &emsp; 删除分支，对于已合并的，我们可以删除
>- git push origin branchName &emsp; 将分支推送到远程仓库中，如果没有则新建分支branchName  
>- git push origin :branchName &emsp; 删除远程服务器中的分支，慎用
>  

附：
git fetch：从远程仓库获得最新版本，但不会merge;
>- git fetch origin master:test
>- git diff test
>- git merge test

以上命令的含义是从远端master获取主干的最新版本到本地分支test
查看本地master和test分支的差异
合并分支test到master
>- git pull origin master：则相当于上面指令的集合，直接获取最新并合并到当前分支

但是git fetch ，git merge更安全一些  
合并后记着这些信息仍在本地，你需要将更新上传到你自己的服务器中（git push）
###6.日志


###7. 概念说明
**fork**  
将感兴趣的开源项目创建一个分支放置到自己的账户中（复制）；  
但是如果源项目有更新，自己的分支不会同步，需要自己手动来更新。  

git clone  your fork  
git remote add src_url  
git fetch src_url  
git merge  


*参考：*http://www.shizuwu.cn/post/669.html



-------------------
###附录
####github管理
1. 如何删除Github中的仓库？
    http://www.oschina.net/question/256591_103418
2. git分支管理
   http://www.51testing.com/html/75/534775-853493.html
3. 如果从仓库中获取一个/部分文件  
   一个文件：在Github页面中，点击想要的文件，然后找到“raw”按钮，将会在网页中显示此文件，手动复制。  
   部分文件：？？？


# dpkg --list|grep mysql

```
在终端中查看MySQL的依赖项：dpkg --list|grep mysql
```



# 自动卸载mysql（包括server和client）

```
sudo apt-get autoremove mysql* --purge
sudo apt-get remove apparmor
```



```
dpkg --list|grep mysql
有时候自动卸载并没有卸载完成，我的就卸载失败了
```



# 手动卸载相关包

```
sudo apt-get remove dbconfig-mysql
sudo apt-get remove mysql-client
sudo apt-get remove mysql-client-5.7
sudo apt-get remove mysql-client-core-5.7

```



```
再次执行自动卸载：sudo apt-get autoremove mysql* --purge
再次执行 dpkg --list|grep mysql
清除残留数据：dpkg -l|grep ^rc|awk '{print$2}'|sudo xargs dpkg -P
再次查看MySQL的剩余依赖项：dpkg --list|grep mysql
```



# 删除相关数据

```
删除mysql的数据文件
sudo rm /var/lib/mysql/ -R

删除mysql的配置文件
sudo rm /etc/mysql/ -R
```



# 安装


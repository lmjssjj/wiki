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

```
sudo apt-get install mysql-server 

service mysql start
service mysql stop
service mysql status
```



# 设置密码

修改root用户的的密码
这里是关键点，由于mysql5.7没有password字段，密码存储在authentication_string字段中，password()方法还能用

在mysql中执行下面语句修改密码

```sql
show databases；
 
use mysql;
  
update user set authentication_string=PASSWORD("yourpassword") where user='root';
  
update user set plugin="mysql_native_password";
  
flush privileges;
  
quit;
```

# 在登录mysql时遇到上面的错误

在/etc/mysql/my.cnf中添加

```
[mysqld]



skip-grant-tables
```

重启mysql

```
sudo service mysql restart
```

重新登录

```
sudo mysql
```

输入以下命令

```
select user, plugin from mysql.user
update mysql.user set authentication_string=PASSWORD('你的密码'), plugin='mysql_native_plugin' where user='root';
或：
show databases；
 
use mysql;
  
update user set authentication_string=PASSWORD("yourpassword") where user='root';
  
update user set plugin="mysql_native_password";
  
flush privileges;
  
quit;
```

# 远程访问

```
/etc/mysql/mysqld.conf.d/mysqld.cnf

bind-address = 127.0.0.1

IP改为0.0.0.0

sudo service mysql restart
```


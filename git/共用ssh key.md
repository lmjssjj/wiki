### 1、先拷贝原始的`ssh key`，没有的话就生成一个

### 2、将拷贝的`ssh key`复制到另一台电脑的用户目录下（linux用户目录：`cd ~`进入；Windows：在`C:\Users\user中；目录名可能会有一点区别）

### 3、重新文件赋予权限

```
cd ~/.ssh
chmod 600 id_rsa
chmod 644 id_rsa.pub
```

###  生成ssh key

```
1、ssh-keygen -t rsa -C “youremail@example.com”，把邮件地址换成你自己的邮件地址，然后一直按回车，使用默认值即可。
2、此时应该可以在用户主目录里找到.ssh目录，里面有id_rsa和id_rsa.pub两个文件，这两个就是SSH Key的秘钥对，id_rsa是私钥，不能泄露出去，id_rsa.pub是公钥，可以告诉任何人。

```



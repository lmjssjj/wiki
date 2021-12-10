https://blog.csdn.net/weixin_44792344/article/details/109674599

https://blog.csdn.net/mafei852213034/article/details/79236800

https://blog.csdn.net/ytuglt/article/details/114198376?utm_term=android%E7%A6%81%E6%AD%A2%E5%BA%94%E7%94%A8%E8%AE%BF%E9%97%AE%E7%BD%91%E7%BB%9C&utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~all~sobaiduweb~default-1-114198376&spm=3001.4430



frameworks/base/services/core/java/com/android/server/NetworkManagementService.java

system/netd/server/binder/android/net/INetd.aidl

# iptables

​		netfilter/iptables（简称为iptables）组成[Linux](https://so.csdn.net/so/search?from=pc_blog_highlight&q=Linux)平台下的包过滤防火墙，与大多数的Linux软件一样，这个包过滤防火墙是免费的，它可以代替昂贵的商业防火墙解决方案，完成封包过滤、封包重定向和网络地址转换（NAT）等功能。

​		iptables是Linux系统的IP信息包过滤工具，实际就是一个Linux命令，通过这个命令，可以对整个系统发出去的包，接收到的包，以及转发的包进行拦截、修改、拒绝等操作。

## iptables和netfilter的关系：

```
	其实iptables只是Linux防火墙的管理工具而已，位于/sbin/iptables。真正实现防火墙功能的是 netfilter，它是Linux内核中实现包过滤的内部结构。
```



```
   规则（rules）其实就是网络管理员预定义的条件，规则一般的定义为“如果数据包头符合这样的条件，就这样处理这个数据包”。规则存储在内核空间的信息 包过滤表中，这些规则分别指定了源地址、目的地址、传输协议（如TCP、UDP、ICMP）和服务类型（如HTTP、FTP和SMTP）等。当数据包与规则匹配时，iptables就根据规则所定义的方法来处理这些数据包，如放行（accept）、拒绝（reject）和丢弃（drop）等。配置防火墙的 主要工作就是添加、修改和删除这些规则。

String cmd = new String[] {
    "iptables", "-P", "OUTPUT", "DROP"
};
try {
    process = Runtime.getRuntime().exec(cmd);
} catch (IOException e1) {
    e1.printStackTrace();        
}
```

```
Android上使用iptables一般做代理和app防火墙功能。
有几个开源项目:
droidwall;
app网络防火墙;
```

## 命令

```

```


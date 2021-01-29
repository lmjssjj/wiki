# 1、jps

打印出java相关进程

# 2、jstat -gc  进程ID

或加参数：-XX:+PrintGCDetails 

打印gc相关信息

# 3、参数

-Xss 设置jvm 栈大小     如  ：-Xss1024  ，-Xss1K，-Xss1k  ，-Xss1M  ，-Xss1m ，-Xss1G -Xss1g

-Xms 设置jvm堆起始大小

-Xmx设置jvm堆最大大小

-Xmn设置新生代最大内存大小

-XX:NewRatio=4， 新生代占1，老年代占4，新生代占整个堆的1/5(默认为：-XX:NewRatio=2)

-XX:SurvivorRatio=8, eden s0 s1 8:1:1

-XX:MaxTenuringThreshold=N   默认15次   设置几次后放到老年区



# 4、
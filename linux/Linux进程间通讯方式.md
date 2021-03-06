# **Linux进程间通讯方式：**

## **管道( pipe )**

​		管道是Linux支持的最初Unix IPC形式之一，具有以下特点：

​		是两个进程之间进行单向通信的机制，因为它的单向性，所以又称为半双工管道。它主要用于进程间的一些简单通信。

​		管道数据只能由一个进程流向另一个进程（一个写管道，一个读管道）；如果要进行全双工通信，需要建立两个管道。
​		管道只能用于父子进程或者兄弟进程之间的通信。
​		管道没有名字，且其缓冲区大小有限。
​		一个进程向管道写数据，数据每次都添加在管道缓冲区的末尾；另一个进程从管道另一端读数据，从缓冲区头部读出数据。

```c++
#include <unistd.h>
int pipe(int fd[2])
// 管道两端分别用描述符fd[0]和fd[1]来描述。其中fd[0]只能用于读，称为管道读端；fd[1]只能用于写，称为管道写端。
```

#### 管道的局限性

管道的主要局限性正体现在它的特点上：

只支持单向数据流；

只能用于具有亲缘关系的进程之间；

没有名字；

管道的缓冲区是有限的（管道制存在于内存中，在管道创建时，为缓冲区分配一个页面大小）；

(管道对于管道两端的进程而言，就是一个文件，但它不是普通的文件，它不属于某种文件系统，而是自立门户，单独构成一种文件系统，并且只存在与内存中。)

管道所传送的是无格式字节流，这就要求管道的读出方和写入方必须事先约定好数据的格式，比如多少字节算作一个消息（或命令、或记录）等等；


### **有名管道 (named pipe)** 

```
 有名管道也是半双工的通信方式，但是它允许无亲缘关系进程间的通信。
```



## 消息队列(message queue)

```
消息队列是消息的链表，存放在内存中，由内核维护。
```



### 消息队列的特点

```
消息队列允许一个或多个进程向它写入或者读取消息，并且每条消息都有类型。
消息队列可以实现消息的随机查询，消息不一定要以先进先出的次序读取，编程时可以按消息的类型 读取。
与无名管道、有名管道一样，从消息队列中读出消息，消息队列中数据会被删除。
同样消息队列中的消息是有格式的。
只有内核重启或人工删除时，该消息才会被删除，若不人工删除消息队列，消息队列会一直存在于内存中。
消息队列标识符，来标识消息队列。消息队列在整个 系统中是唯一的。
在Linux操作系统中消息队列限制值如下: 

      1. 消息队列个数最多为16个 
      2. 消息队列总容量最多为16384字节 
         3.每个消息内容最多为8192字节
         System V提供的IPC通信机制需要一个key值，通过key 值就可在系统内获得一个唯一的消息队列。
         key值可以是人为指定的，也可以通过ftok函数获得。
         
```

```c++
#include <sys/msg.h>
int msgget(key_t key, int msgflg);
//功能：
//  创建一个新的或打开一个已经存在的消息队列。 不同的进程调用此函数，只要用相同的key值就能得到 同一个消息队列的ID。//
//参数： 
//   key：IPC键值 
//   msgflg：标识函数的行为：IPC_CREAT(创建)或 IPC_EXCL(如果已经存在则返回失败)。
//返回值：
//   成功：消息队列的标识符，失败：返回-1。

```

```
信息复制两次，额外的CPU消耗；不合适频繁或信息量大的通信；
```

**使用shell命令操作消息队列:**
   查看消息队列   ipcs -q
   删除消息队列   ipcrm -q msqid



## **共享内存**

```
	共享内存就是允许两个或多个进程共享一定的存储区。就如同 malloc() 函数向不同进程返回了指向同一个物理内存区域的指针。当一个进程改变了这块地址中的内容的时候，其它进程都会察觉到这个更改。因为数据不需要在客户机和服务器端之间复制，数据直接写到内存，不用若干次数据拷贝，所以这是最快的一种IPC。
	
注：共享内存没有任何的同步与互斥机制，所以要使用信号量来实现对共享内存的存取的同步。

无须复制，共享缓冲区直接付附加到进程虚拟地址空间，速度快；但进程间的同步问题操作系统无法实现，必须各进程利用同步工具解决；

```

### **共享内存特点和优势**

​		当中共享内存的大致原理相信我们可以看明白了，就是让两个进程地址通过页表映射到同一片物理地址以便于通信,你可以给一个区域里面写入数据，理所当然你就可以从中拿取数据，这也就构成了进程间的双向通信，而且共享内存是IPC通信当中**传输速度最快的通信方式没有之一**，理由很简单，客户进程和服务进程传递的数据直接从内存里存取、放入，数据不需要在两进程间复制，没有什么操作比这简单了。再者用共享内存进行数据通信，它对数据也没啥限制。

最后就是共享内存的生命周期随内核。即所有访问共享内存区域对象的进程都已经正常结束,共享内存区域对象仍然在内核中存在(除非显式删除共享内存区域对象),在内核重新引导之前,对该共享内存区域对象的任何改写操作都将一直保留;简单地说,共享内存区域对象的生命周期跟系统内核的生命周期是一致的,而且共享内存区域对象的作用域范围就是在整个系统内核的生命周期之内。

 

### **缺陷**

​		但是，共享内存也并不完美，共享内存并未提供同步机制，也就是说，在一个服务进程结束对共享内存的写操作之前，并没有自动机制可以阻止另一个进程（客户进程）开始对它进行读取。这明显还达不到我们想要的，我们不单是在两进程间交互数据，还想实现多个进程对共享内存的同步访问，这也正是使用共享内存的窍门所在。基于此，我们通常会用平时常谈到和用到 **信号量**来实现对共享内存同步访问控制。





## 套接字

```
	socket API原本是为网络通讯设计的，但后来在socket的框架上发展出一种IPC机制，就是UNIX Domain Socket。虽然网络socket也可用于同一台主机的进程间通讯（通过loopback地址127.0.0.1），但是UNIX Domain Socket用于IPC更有效率：不需要经过网络协议栈，不需要打包拆包、计算校验和、维护序号和应答等，只是将应用层数据从一个进程拷贝到另一个进程。这是因为，IPC机制本质上是可靠的通讯，而网络协议是为不可靠的通讯设计的。UNIX Domain Socket也提供面向流和面向数据包两种API接口，类似于TCP和UDP，但是面向消息的UNIX Domain Socket也是可靠的，消息既不会丢失也不会顺序错乱。

	UNIX Domain Socket是全双工的，API接口语义丰富，相比其它IPC机制有明显的优越性，目前已成为使用最广泛的IPC机制，比如X Window服务器和GUI程序之间就是通过UNIXDomain Socket通讯的。

	使用UNIX Domain Socket的过程和网络socket十分相似，也要先调用socket()创建一个socket文件描述符，address family指定为AF_UNIX，type可以选择SOCK_DGRAM或SOCK_STREAM，protocol参数仍然指定为0即可。

	UNIX Domain Socket与网络socket编程最明显的不同在于地址格式不同，用结构体sockaddr_un表示，网络编程的socket地址是IP地址加端口号，而UNIX Domain Socket的地址是一个socket类型的文件在文件系统中的路径，这个socket文件由bind()调用创建，如果调用bind()时该文件已存在，则bind()错误返回。
	
	作为更通用的接口，传输效率低，主要用于不通机器或跨网络的通信；
```





## **信号量**

```
信号量（也叫信号灯）是一种用于提供不同进程间或一个给定进程的不同线程间同步手段的原语。

信号量是进程/线程同步的一种方式，有时候我们需要保护一段代码，使它每次只能被一个执行进程/线程运行，这种工作就需要一个二进制开关；

有时候需要限制一段代码可以被多少个进程/线程执行，这就需要用到关于计数信号量。信号量开关是二进制信号量的一种逻辑扩展，两者实际调用的函数都是一样。

信号量分为以下三种。

1、System V信号量，在内核中维护，可用于进程或线程间的同步，常用于进程的同步。 

2、Posix有名信号量，一种来源于POSIX技术规范的实时扩展方案（POSIX Realtime Extension）,可用于进程或线程间的同步，常用于线程。

3、Posix基于内存的信号量，存放在共享内存区中，可用于进程或线程间的同步。

为了获得共享资源进程需要执行下列操作：

（1）测试控制该资源的信号量。

（2）若信号量的值为正，则进程可以使用该资源。进程信号量值减1，表示它使用了一个资源单位。此进程使用完共享资源后对应的信号量会加1。以便其他进程使用。

（3）若信号量的值为0，则进程进入休息状态，直至信号量值大于0。进程被唤醒，返回第（1）步。

    为了正确地实现信号量，信号量值的测试值的测试及减1操作应当是原子操作（原子操作是不可分割的，在执行完毕不会被任何其它任务或事件中断）。为此信号量通常是在内核中实现的。
    
```

```
 常作为一种锁机制，防止某进程正在访问共享资源时，其他进程也访问该资源。因此，主要作为进程间以及同一进程内不同线程之间的同步手段。
```





## 信号

```
 信号是进程在运行过程中， 由自身产生或由进程外部发过来的消息（ 事件） 。 信号是硬件中断的软

 件模拟(软中断)。 每个信号用一个整型常量宏表示， 以 SIG 开头， 比如 SIGCHLD、 SIGINT 等.信号的生成来自内核， 让内核生成信号的请求来自 3 个地方：
 （1）用户： 用户能够通过输入 CTRL+c Ctrl+\
 （2）内核： 当进程执行出错时， 内核会给进程发送一个信号， 例如非法段存取(内存访问违规)、 浮点数溢出等；
 （3）进程： 一个进程可以通过系统调用 
 		 	kill 给另一个进程发送信号， 一个进程可以通过信号和另外一个进程进行通信。
			由进程的某个操作产生的信号称为同步信号
```

```
不适用于信息交换，更适用于进程中断控制，比如非法内存访问，杀死某个进程等；
```


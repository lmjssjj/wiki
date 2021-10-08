## Binder

https://www.cxyzjd.com/article/yiranfeng/105232709

### binder_init()

```
内核初始化时，会调用到device_initcall()进行初始化，从而启动binder_init。
binder_init()主要负责注册misc设备,通过调用misc_register()来实现。
在Android8.0之后，现在Binder驱动有三个：/dev/binder; /dev/hwbinder; /dev/vndbinder.
```

```c
//kernel-4.9/drivers/android/binder.c
//kernel-4.9/include/linux/debugfs.h

//打开debug_fs
//默认\kernel-4.9\arch\arm64\configs\defconfig 文件下 CONFIG_DEBUG_FS=y
CONFIG_ANDROID_BINDER_DEVICES="binder,hwbinder,vndbinder"
static char *binder_devices_param = CONFIG_ANDROID_BINDER_DEVICES;

device_initcall(binder_init);
static int __init binder_init(void)
{
     //在debugfs文件系统中创建一个目录，返回值是指向dentry的指针
    //在手机对应的目录：/sys/kernel/debug/binder，里面创建了几个文件，用来记录binder操作过程中的信息和日志：
    //failed_transaction_log、state、stats、transaction_log、transactions
    binder_debugfs_dir_entry_root = debugfs_create_dir("binder", NULL);
	if (binder_debugfs_dir_entry_root)
		binder_debugfs_dir_entry_proc = debugfs_create_dir("proc",
						 binder_debugfs_dir_entry_root);
    
    if (binder_debugfs_dir_entry_root) {
        /* 创建/sys/kernel/debug/binder/state记录状态信息，并注册操作函数*/  
		debugfs_create_file("state",
				    0444,
				    binder_debugfs_dir_entry_root,
				    NULL,
				    &binder_state_fops);
        /* 创建/sys/kernel/debug/binder/stats记录统计信息，并注册操作函数*/   
		debugfs_create_file("stats",
				    0444,
				    binder_debugfs_dir_entry_root,
				    NULL,
				    &binder_stats_fops);
        /* 创建/sys/kernel/debug/binder/transactions记录transaction相关信息，并注册操作函数*/
		debugfs_create_file("transactions",
				    0444,
				    binder_debugfs_dir_entry_root,
				    NULL,
				    &binder_transactions_fops);
        /* 创建/sys/kernel/debug/binder/transactions_log记录transaction日志相关信息，并注册操作函数*/      
		debugfs_create_file("transaction_log",
				    0444,
				    binder_debugfs_dir_entry_root,
				    &binder_transaction_log,
				    &binder_transaction_log_fops);
        /* 创建/sys/kernel/debug/binder/failed_transactions_log记录失败的transaction日志相关信息，并注册操作函数*/
		debugfs_create_file("failed_transaction_log",
				    0444,
				    binder_debugfs_dir_entry_root,
				    &binder_transaction_log_failed,
				    &binder_transaction_log_fops);
#ifdef BINDER_WATCHDOG
		debugfs_create_file("timeout_log",
				    0444,
				    binder_debugfs_dir_entry_root,
				    NULL,
				    &binder_timeout_log_fops);
#endif
	}
    
    device_names = kzalloc(strlen(binder_devices_param) + 1, GFP_KERNEL);
    device_tmp = device_names;
    //Android8.0 中引入了hwbinder，vndbinder，所以现在有三个binder，分别需要创建三个binder device:
   // /dev/binder、/dev/hwbinder、/dev/vndbinder
   //循环注册binder 的三个设备：/dev/binder、/dev/hwbinder、/dev/vndbinder
    while ((device_name = strsep(&device_tmp, ","))) {
		ret = init_binder_device(device_name);
		if (ret)
			goto err_init_binder_device_failed;
	}
}


static int __init init_binder_device(const char *name)
{
	int ret;
	struct binder_device *binder_device;
    //申请内存空间，
	binder_device = kzalloc(sizeof(*binder_device), GFP_KERNEL);
	if (!binder_device)
		return -ENOMEM;

	binder_device->miscdev.fops = &binder_fops;
	binder_device->miscdev.minor = MISC_DYNAMIC_MINOR;
	binder_device->miscdev.name = name;

	binder_device->context.binder_context_mgr_uid = INVALID_UID;
	binder_device->context.name = name;
	mutex_init(&binder_device->context.context_mgr_node_lock);
	//系统注册设备节点。该函数成功调用后即会在/device/binder (/device/vndbinder，/device/hwbinder)下生成设备节点
	ret = misc_register(&binder_device->miscdev);
	if (ret < 0) {
		kfree(binder_device);
		return ret;
	}

	hlist_add_head(&binder_device->hlist, &binder_devices);

	return ret;
}
```

### binder_open()

```
binder_open()职责如下：
首先创建了binder_proc结构体实例proc
接着开始初始化一系列成员：tsk, todo, default_priority, pid， delivered_death等。
更新了统计数据：binder_proc的创建个数加1
紧接着将初始化好的proc，存放到文件指针filp->private_data中，以便于在之后的mmap、ioctl中获取。
将binder_proc链入binder_procs哈希链表中；
最后查看是否创建的了/sys/kernel/debug/binde/proc/目录，有的话再创建一个/sys/kernel/debug/binde/proc/pid文件，用来记录binder_proc的状态
```

#### binder_proc

```
binder_proc 与应用层的binder实体一一对应，每个进程调用open()打开binder驱动都会创建该结构体，用于管理IPC所需的各种信息。
	其中有4个红黑树threads、nodes、refs_by_desc、refs_by_node， 在一个进程中，有多少“被其他进程进行跨进程调用的”binder实体，就会在该进程对应的nodes树中生成多少个红黑树节点。另一方面，一个进程要访问多少其他进程的binder实体，则必须在其refs_by_desc树中拥有对应的引用节点。
```

```c
struct binder_proc {
       struct hlist_node proc_node;    //进程节点
       struct rb_root threads;         //记录执行传输动作的线程信息, binder_thread红黑树的根节点
       struct rb_root nodes;           //用于记录binder实体  ,binder_node红黑树的根节点，它是Server在Binder驱动中的体现
       struct rb_root refs_by_desc;    //记录binder引用, 便于快速查找,binder_ref红黑树的根节点(以handle为key)，它是Client在Binder驱动中的体现
       struct rb_root refs_by_node;    //记录binder引用, 便于快速查找,binder_ref红黑树的根节点（以ptr为key），它是Client在Binder驱动中的体现
       struct list_head waiting_threads;
       int pid;    //相应进程id
       struct task_struct *tsk;    //相应进程的task结构体
       struct files_struct *files; //相应进程的文件结构体
       struct mutex files_lock;
       struct hlist_node deferred_work_node;
       int deferred_work;
       bool is_dead;

       struct list_head todo;      //进程将要做的事
       struct binder_stats stats;  //binder统计信息
       struct list_head delivered_death;   //已分发的死亡通知
       int max_threads;        //最大线程数
       int requested_threads;  //请求的线程数
       int requested_threads_started;  //已启动的请求线程数
       atomic_t tmp_ref;
       struct binder_priority default_priority;    //默认优先级
       struct dentry *debugfs_entry;
       struct binder_alloc alloc;
       struct binder_context *context;
       spinlock_t inner_lock;
       spinlock_t outer_lock;
};
```



```c
static int binder_open(struct inode *nodp, struct file *filp)
{
	struct binder_proc *proc;
	struct binder_device *binder_dev;

	binder_debug(BINDER_DEBUG_OPEN_CLOSE, "%s: %d:%d\n", __func__,
		     current->group_leader->pid, current->pid);

    // 为binder_proc结构体在分配kernel内存空间
	proc = kzalloc(sizeof(*proc), GFP_KERNEL);
	if (proc == NULL)
		return -ENOMEM;
	spin_lock_init(&proc->inner_lock);
	spin_lock_init(&proc->outer_lock);
	atomic_set(&proc->tmp_ref, 0);
	get_task_struct(current->group_leader);//增加线程引用计数
	proc->tsk = current->group_leader; //将当前线程的task保存到binder进程的tsk
	mutex_init(&proc->files_lock);
	INIT_LIST_HEAD(&proc->todo);//初始化todo队列，用于存放待处理的请求（server端）
	if (binder_supported_policy(current->policy)) {
		proc->default_priority.sched_policy = current->policy;
		proc->default_priority.prio = current->normal_prio;
	} else {
		proc->default_priority.sched_policy = SCHED_NORMAL;
		proc->default_priority.prio = NICE_TO_PRIO(0);
	}

	binder_dev = container_of(filp->private_data, struct binder_device,
				  miscdev);
	proc->context = &binder_dev->context;//拿到binder device的context，传给binder_proc 
	binder_alloc_init(&proc->alloc);

	binder_stats_created(BINDER_STAT_PROC);
	proc->pid = current->group_leader->pid;//记录当前进程的pid
	INIT_LIST_HEAD(&proc->delivered_death);
	INIT_LIST_HEAD(&proc->waiting_threads);
	filp->private_data = proc;//将binder_proc存放在filp的private_data域，以便于在之后的mmap、ioctl中获取

	mutex_lock(&binder_procs_lock);
	hlist_add_head(&proc->proc_node, &binder_procs); //将proc_node节点添加到binder_procs为表头的队列
	mutex_unlock(&binder_procs_lock);
    
    // 如果/sys/kernel/debug/binder/proc 目录存在，在该目录中创建相应pid对应的文件，名称为pid，用来记录binder_proc的状态
	if (binder_debugfs_dir_entry_proc) {
		char strbuf[11];

		snprintf(strbuf, sizeof(strbuf), "%u", proc->pid);
		/*
		 * proc debug entries are shared between contexts, so
		 * this will fail if the process tries to open the driver
		 * again with a different context. The priting code will
		 * anyway print all contexts that a given PID has, so this
		 * is not a problem.
		 */
		proc->debugfs_entry = debugfs_create_file(strbuf, 0444,
			binder_debugfs_dir_entry_proc,
			(void *)(unsigned long)proc->pid,
			&binder_proc_fops);
	}

	return 0;
}

```

```
//kernel-4.9/drivers/android/binder.c

binder_procs 
binder_procs哈希链表, 存储了所有open() binder驱动的进程对象

Binder驱动中通过static HLIST_HEAD(binder_procs);，创建了全局的哈希链表binder_procs，用于保存所有的binder_proc队列，每次新创建的binder_proc对象都会加入binder_procs链表中。
```

![](image\binder_procs.png)

### binder_mmap

```
主要功能：首先在内核虚拟地址空间，申请一块与用户虚拟内存相同大小的内存；然后再申请page物理内存，

再将同一块物理内存分别映射到内核虚拟地址空间和用户虚拟内存空间，从而实现了用户空间的Buffer和内核空间的Buffer同步操作的功能。
```

```c
////kernel-4.9/drivers/android/binder.c
static int binder_mmap(struct file *filp, struct vm_area_struct *vma)
{
	int ret;
	struct binder_proc *proc = filp->private_data;//private_data保存了我们open设备时创建的binder_proc信息
	const char *failure_string;

	if (proc->tsk != current->group_leader)
		return -EINVAL;

    //vma->vm_end, vma->vm_start 指向要 映射的用户空间地址, map size 不允许 大于 4M
	if ((vma->vm_end - vma->vm_start) > SZ_4M)
		vma->vm_end = vma->vm_start + SZ_4M;

	binder_debug(BINDER_DEBUG_OPEN_CLOSE,
		     "%s: %d %lx-%lx (%ld K) vma %lx pagep %lx\n",
		     __func__, proc->pid, vma->vm_start, vma->vm_end,
		     (vma->vm_end - vma->vm_start) / SZ_1K, vma->vm_flags,
		     (unsigned long)pgprot_val(vma->vm_page_prot));

	if (vma->vm_flags & FORBIDDEN_MMAP_FLAGS) {
		ret = -EPERM;
		failure_string = "bad vm_flags";
		goto err_bad_arg;
	}
    // 将 VM_DONTCOP 置起，禁止 拷贝，禁止 写操作
	vma->vm_flags |= VM_DONTCOPY | VM_MIXEDMAP;
	vma->vm_flags &= ~VM_MAYWRITE;

	vma->vm_ops = &binder_vm_ops;
	vma->vm_private_data = proc;

    // 再次完善 binder buffer allocator
	ret = binder_alloc_mmap_handler(&proc->alloc, vma);
	if (ret)
		return ret;
	mutex_lock(&proc->files_lock);//同步锁
	proc->files = get_files_struct(current);
	mutex_unlock(&proc->files_lock);//释放锁
	return 0;

err_bad_arg:
	pr_err("%s: %d %lx-%lx %s failed %d\n", __func__,
	       proc->pid, vma->vm_start, vma->vm_end, failure_string, ret);
	return ret;
}

```

```
参数：
filp: 文件描述符
vma: 用户虚拟内存空间 

流程：
filp->private_data保存了我们open设备时创建的binder_proc信息;
为用户进程分配一块内核空间作为缓冲区;
把分配的缓冲区指针存放到binder_proc的buffer字段;
分配pages空间;
在内核分配一块同样页数的内核空间,并把它的物理内存和前面为用户进程分配的内存地址关联;
将刚才分配的内存块加入用户进程内存链表;
```

```c
//kernel-4.9/drivers/android/binder_alloc.c
/**
 * binder_alloc_mmap_handler() - map virtual address space for proc
 * @alloc:	alloc structure for this proc
 * @vma:	vma passed to mmap()
 *
 * Called by binder_mmap() to initialize the space specified in
 * vma for allocating binder buffers
 *
 * Return:
 *      0 = success
 *      -EBUSY = address space already mapped
 *      -ENOMEM = failed to map memory to given address space
 */
int binder_alloc_mmap_handler(struct binder_alloc *alloc,
			      struct vm_area_struct *vma)
{
	int ret;
	const char *failure_string;
	struct binder_buffer *buffer;//每一次Binder传输数据时，都会先从Binder内存缓存区中分配一个binder_buffer来存储传输数据

	mutex_lock(&binder_alloc_mmap_lock); //同步锁
	if (alloc->buffer) {// 不需要重复mmap
		ret = -EBUSY;
		failure_string = "already mapped";
		goto err_already_mapped;
	}

	alloc->buffer = (void __user *)vma->vm_start;//指向用户进程内核虚拟空间的 start地址
	mutex_unlock(&binder_alloc_mmap_lock); //释放锁

    //分配物理页的指针数组，数组大小为vma的等效page个数
	alloc->pages = kzalloc(sizeof(alloc->pages[0]) *
				   ((vma->vm_end - vma->vm_start) / PAGE_SIZE),
			       GFP_KERNEL);
	if (alloc->pages == NULL) {
		ret = -ENOMEM;
		failure_string = "alloc page array";
		goto err_alloc_pages_failed;
	}
	alloc->buffer_size = vma->vm_end - vma->vm_start;

	buffer = kzalloc(sizeof(*buffer), GFP_KERNEL);//申请一个binder_buffer的内存
	if (!buffer) {
		ret = -ENOMEM;
		failure_string = "alloc buffer struct";
		goto err_alloc_buf_struct_failed;
	}

	buffer->user_data = alloc->buffer;//指向用户进程内核虚拟空间的 start地址，即为当前进程mmap的内核空间地址
	list_add(&buffer->entry, &alloc->buffers);//将binder_buffer地址 加入到所属进程的buffers队列
	buffer->free = 1;
	binder_insert_free_buffer(alloc, buffer);//将 当前 buffer 加入到 红黑树 alloc->free_buffers 中，表示当前 buffer 是空闲buffer
    
	alloc->free_async_space = alloc->buffer_size / 2;// 将 异步事务 的空间大小设置为 整个空间的一半
	barrier();
	alloc->vma = vma;
	alloc->vma_vm_mm = vma->vm_mm;
	/* Same as mmgrab() in later kernel versions */
	atomic_inc(&alloc->vma_vm_mm->mm_count);

	return 0;

err_alloc_buf_struct_failed:
	kfree(alloc->pages);
	alloc->pages = NULL;
err_alloc_pages_failed:
	mutex_lock(&binder_alloc_mmap_lock);
	alloc->buffer = NULL;
err_already_mapped:
	mutex_unlock(&binder_alloc_mmap_lock);
	pr_err("%s: %d %lx-%lx %s failed %d\n", __func__,
	       alloc->pid, vma->vm_start, vma->vm_end, failure_string, ret);
	return ret;
}
```

#### binder_buffer

每一次Binder传输数据时，都会先从Binder内存缓存区中分配一个binder_buffer来存储传输数据。

```c
struct binder_buffer {
       struct list_head entry; //buffer实体的地址
       struct rb_node rb_node; //buffer实体的地址
       unsigned free:1;            //标记是否是空闲buffer，占位1bit
       unsigned allow_user_free:1;  //是否允许用户释放，占位1bit
       unsigned async_transaction:1;//占位1bit
       unsigned debug_id:29;          //占位29bit

       struct binder_transaction *transaction; //该缓存区的需要处理的事务

       struct binder_node *target_node; //该缓存区所需处理的Binder实体
       size_t data_size;          //数据大小
       size_t offsets_size;      //数据偏移量
       size_t extra_buffers_size;
       void __user *user_data;   //用户数据
};
```

#### 内存分配

```
ServiceManager启动后，会通过系统调用mmap向内核空间申请128K的内存，用户进程会通过mmap向内核申请(1M-8K)的内存空间。
kernel的“backing store”需要一个保护页，这使得1M用来分配碎片内存时变得很差，所以这里减去两页来提高效率，因为减去一页就变成了奇数。
系统定义：BINDER_VM_SIZE ((1 * 1024 * 1024) - sysconf(_SC_PAGE_SIZE) * 2)   = （1M- sysconf(_SC_PAGE_SIZE) * 2）

这里的8K，其实就是两个PAGE的SIZE, 物理内存的划分是按PAGE(页)来划分的，一般情况下，一个Page的大小为4K。

内核会增加一个guard page，再加上内核本身的guard page，正好是两个page的大小，减去后，就是用户空间可用的大小。 

```

### binder_ioctl

```
binder_ioctl()函数负责在两个进程间收发IPC数据和IPC reply数据，Native C\C++ 层传入不同的cmd和数据，根据cmd的值，进行相应的处理并返回
参数：
filp：文件描述符
cmd：ioctl命令
arg：数据类型
	(1) 文件描述符，是通过open()方法打开Binder Driver后返回值；
	(2) ioctl命令和数据类型是一体的，不同的命令对应不同的数据类型

```

| ioctl命令                | 数据类型                 | 操作                    |
| ------------------------ | ------------------------ | ----------------------- |
| **BINDER_WRITE_READ**    | struct binder_write_read | 收发Binder IPC数据      |
| BINDER_SET_MAX_THREADS   | __u32                    | 设置Binder线程最大个数  |
| BINDER_SET_CONTEXT_MGR   | __s32                    | 设置Service Manager节点 |
| BINDER_THREAD_EXIT       | __s32                    | 释放Binder线程          |
| BINDER_VERSION           | struct binder_version    | 获取Binder版本信息      |
| BINDER_SET_IDLE_TIMEOUT  | __s64                    | 没有使用                |
| BINDER_SET_IDLE_PRIORITY | __s32                    | 没有使用                |

```c
static long binder_ioctl(struct file *filp, unsigned int cmd, unsigned long arg)
{
	int ret;
    //filp->private_data 在open()binder驱动时，保存了一个创建的binder_proc，即是此时调用进程的binder_proc.
	struct binder_proc *proc = filp->private_data;
	struct binder_thread *thread;// binder线程
	unsigned int size = _IOC_SIZE(cmd);
	void __user *ubuf = (void __user *)arg;

	/*pr_info("binder_ioctl: %d:%d %x %lx\n",
			proc->pid, current->pid, cmd, arg);*/

	binder_selftest_alloc(&proc->alloc);

	trace_binder_ioctl(cmd, arg);
	//进入休眠状态，直到中断唤醒
	ret = wait_event_interruptible(binder_user_error_wait, binder_stop_on_user_error < 2);
	if (ret)
		goto err_unlocked;

    //获取binder线程信息，如果是第一次调用ioctl()，则会为该进程创建一个线程
	thread = binder_get_thread(proc);
	if (thread == NULL) {
		ret = -ENOMEM;
		goto err;
	}

	switch (cmd) {
    //binder的读写操作，使用频率较高
	case BINDER_WRITE_READ:
		ret = binder_ioctl_write_read(filp, cmd, arg, thread);
		if (ret)
			goto err;
		break;
    //设置Binder线程最大个数
	case BINDER_SET_MAX_THREADS: {
		int max_threads;

		if (copy_from_user(&max_threads, ubuf,
				   sizeof(max_threads))) {
			ret = -EINVAL;
			goto err;
		}
		binder_inner_proc_lock(proc);
		proc->max_threads = max_threads;
		binder_inner_proc_unlock(proc);
		break;
	}
    //设置Service Manager节点，带flag参数， servicemanager进程成为上下文管理者
	case BINDER_SET_CONTEXT_MGR_EXT: {
		struct flat_binder_object fbo;

		if (copy_from_user(&fbo, ubuf, sizeof(fbo))) {
			ret = -EINVAL;
			goto err;
		}
		ret = binder_ioctl_set_ctx_mgr(filp, &fbo);
		if (ret)
			goto err;
		break;
	}
   //设置Service Manager节点，不带flag参数， servicemanager进程成为上下文管理者
	case BINDER_SET_CONTEXT_MGR:
		ret = binder_ioctl_set_ctx_mgr(filp, NULL);
		if (ret)
			goto err;
		break;
	case BINDER_THREAD_EXIT:
		binder_debug(BINDER_DEBUG_THREADS, "%d:%d exit\n",
			     proc->pid, thread->pid);
		binder_thread_release(proc, thread);
		thread = NULL;
		break;
    //获取Binder版本信息
	case BINDER_VERSION: {
		struct binder_version __user *ver = ubuf;

		if (size != sizeof(struct binder_version)) {
			ret = -EINVAL;
			goto err;
		}
		if (put_user(BINDER_CURRENT_PROTOCOL_VERSION,
			     &ver->protocol_version)) {
			ret = -EINVAL;
			goto err;
		}
		break;
	}
	case BINDER_GET_NODE_INFO_FOR_REF: {
		struct binder_node_info_for_ref info;

		if (copy_from_user(&info, ubuf, sizeof(info))) {
			ret = -EFAULT;
			goto err;
		}

		ret = binder_ioctl_get_node_info_for_ref(proc, &info);
		if (ret < 0)
			goto err;

		if (copy_to_user(ubuf, &info, sizeof(info))) {
			ret = -EFAULT;
			goto err;
		}

		break;
	}
	case BINDER_GET_NODE_DEBUG_INFO: {
		struct binder_node_debug_info info;

		if (copy_from_user(&info, ubuf, sizeof(info))) {
			ret = -EFAULT;
			goto err;
		}

		ret = binder_ioctl_get_node_debug_info(proc, &info);
		if (ret < 0)
			goto err;

		if (copy_to_user(ubuf, &info, sizeof(info))) {
			ret = -EFAULT;
			goto err;
		}
		break;
	}
	default:
		ret = -EINVAL;
		goto err;
	}
	ret = 0;
err:
	if (thread)
		thread->looper_need_return = false;
	wait_event_interruptible(binder_user_error_wait, binder_stop_on_user_error < 2);
	if (ret && ret != -ERESTARTSYS)
		pr_info("%d:%d ioctl %x %lx returned %d\n", proc->pid, current->pid, cmd, arg, ret);
err_unlocked:
	trace_binder_ioctl_done(ret);
	return ret;
}
```

#### binder_get_thread

```
作用：从当前进程中获取线程信息，如果当前进程中没有线程信息，那么创建一个线程，把proc指向当前进程，并进行线程初始化
```

```c
static struct binder_thread *binder_get_thread(struct binder_proc *proc)
{
       struct binder_thread *thread;
       struct binder_thread *new_thread;

       binder_inner_proc_lock(proc);
       //从当前进程中获取线程
       thread = binder_get_thread_ilocked(proc, NULL);
       binder_inner_proc_unlock(proc);
       if (!thread) {
              //如果当前进程中没有线程，那么创建一个
               new_thread = kzalloc(sizeof(*thread), GFP_KERNEL);
               if (new_thread == NULL)
                       return NULL;
               binder_inner_proc_lock(proc);
               thread = binder_get_thread_ilocked(proc, new_thread);
               binder_inner_proc_unlock(proc);
               if (thread != new_thread)
                       kfree(new_thread);
       }
       return thread;
}
```



```c
static int binder_ioctl_write_read(struct file *filp,
				unsigned int cmd, unsigned long arg,
				struct binder_thread *thread)
{
	int ret = 0;
	struct binder_proc *proc = filp->private_data;
	unsigned int size = _IOC_SIZE(cmd);
	void __user *ubuf = (void __user *)arg;
	struct binder_write_read bwr;

	if (size != sizeof(struct binder_write_read)) {
		ret = -EINVAL;
		goto out;
	}
	if (copy_from_user(&bwr, ubuf, sizeof(bwr))) {
		ret = -EFAULT;
		goto out;
	}
	binder_debug(BINDER_DEBUG_READ_WRITE,
		     "%d:%d write %lld at %016llx, read %lld at %016llx\n",
		     proc->pid, thread->pid,
		     (u64)bwr.write_size, (u64)bwr.write_buffer,
		     (u64)bwr.read_size, (u64)bwr.read_buffer);

	if (bwr.write_size > 0) {
		ret = binder_thread_write(proc, thread,
					  bwr.write_buffer,
					  bwr.write_size,
					  &bwr.write_consumed);
		trace_binder_write_done(ret);
		if (ret < 0) {
			bwr.read_consumed = 0;
			if (copy_to_user(ubuf, &bwr, sizeof(bwr)))
				ret = -EFAULT;
			goto out;
		}
	}
	if (bwr.read_size > 0) {
		ret = binder_thread_read(proc, thread, bwr.read_buffer,
					 bwr.read_size,
					 &bwr.read_consumed,
					 filp->f_flags & O_NONBLOCK);
		trace_binder_read_done(ret);
		binder_inner_proc_lock(proc);
		if (!binder_worklist_empty_ilocked(&proc->todo))
			binder_wakeup_proc_ilocked(proc);
		binder_inner_proc_unlock(proc);
		if (ret < 0) {
			if (copy_to_user(ubuf, &bwr, sizeof(bwr)))
				ret = -EFAULT;
			goto out;
		}
	}
	binder_debug(BINDER_DEBUG_READ_WRITE,
		     "%d:%d wrote %lld of %lld, read return %lld of %lld\n",
		     proc->pid, thread->pid,
		     (u64)bwr.write_consumed, (u64)bwr.write_size,
		     (u64)bwr.read_consumed, (u64)bwr.read_size);
	if (copy_to_user(ubuf, &bwr, sizeof(bwr))) {
		ret = -EFAULT;
		goto out;
	}
out:
	return ret;
}
```



## ServiceManager

![](..\android\images\B-启动ServiceManager时序图.jpg)

```c
//frameworks/native/cmds/servicemanager/service_manager.c
int main(int argc, char** argv)
{
    if (argc > 1) {
        driver = argv[1];
    } else {
        driver = "/dev/binder";
    }

    bs = binder_open(driver, 128*1024);//打开驱动      128k 字节大小的内存空间
    if (binder_become_context_manager(bs)) {//设为守护进程
        ALOGE("cannot become context manager (%s)\n", strerror(errno));
        return -1;
    }
    binder_loop(bs, svcmgr_handler);//开启循环
}
```

```c

struct binder_state
{
    int fd;           // 文件节点"/dev/binder"的句柄
    void *mapped;     // 映射内存的起始地址
    unsigned mapsize; // 映射内存的大小
}; 
```

```c
//frameworks/native/cmds/servicemanager/binder.h
//kernel-4.9/drivers/android/binder.c
//frameworks/native/cmds/servicemanager/binder.c
struct binder_state *binder_open(const char* driver, size_t mapsize)
{
    struct binder_state *bs;
    struct binder_version vers;

    bs = malloc(sizeof(*bs));
    if (!bs) {
        errno = ENOMEM;
        return NULL;
    }
	//通过系统调用陷入内核，打开Binder设备驱动
    bs->fd = open(driver, O_RDWR | O_CLOEXEC);
    if (bs->fd < 0) {
        fprintf(stderr,"binder: cannot open %s (%s)\n",
                driver, strerror(errno));
        goto fail_open;
    }
	//通过系统调用，ioctl获取binder版本信息
    if ((ioctl(bs->fd, BINDER_VERSION, &vers) == -1) ||
        (vers.protocol_version != BINDER_CURRENT_PROTOCOL_VERSION)) {
        fprintf(stderr,
                "binder: kernel driver version (%d) differs from user space version (%d)\n",
                vers.protocol_version, BINDER_CURRENT_PROTOCOL_VERSION);
        goto fail_open;
    }

    bs->mapsize = mapsize;
    //通过系统调用，mmap内存映射，mmap必须是page的整数倍
    bs->mapped = mmap(NULL, mapsize, PROT_READ, MAP_PRIVATE, bs->fd, 0);
    if (bs->mapped == MAP_FAILED) {
        fprintf(stderr,"binder: cannot map device (%s)\n",
                strerror(errno));
        goto fail_map;
    }

    return bs;

fail_map:
    close(bs->fd);
fail_open:
    free(bs);
    return NULL;
}

```

```
先调用open()打开binder设备，open()方法经过系统调用，进入Binder驱动，然后调用方法binder_open()，该方法会在Binder驱动层创建一个binder_proc对象，再将binder_proc对象赋值给fd->private_data，同时放入全局链表binder_procs。再通过ioctl()检验当前binder版本与Binder驱动层的版本是否一致。

调用mmap()进行内存映射，同理mmap()方法经过系统调用，对应于Binder驱动层的binder_mmap()方法，该方法会在Binder驱动层创建Binder_buffer对象，并放入当前binder_proc的proc->buffers链表。
```

```c
int binder_become_context_manager(struct binder_state *bs)
{
    struct flat_binder_object obj;
    memset(&obj, 0, sizeof(obj));
    obj.flags = FLAT_BINDER_FLAG_TXN_SECURITY_CTX;

    //通过ioctl，传递BINDER_SET_CONTEXT_MGR_EXT指令
    int result = ioctl(bs->fd, BINDER_SET_CONTEXT_MGR_EXT, &obj);

    // fallback to original method
    if (result != 0) {
        android_errorWriteLog(0x534e4554, "121035042");

        result = ioctl(bs->fd, BINDER_SET_CONTEXT_MGR, 0);
    }
    return result;
}
```

```c
int binder_write(struct binder_state *bs, void *data, size_t len)
{
    struct binder_write_read bwr;
    int res;

    bwr.write_size = len;
    bwr.write_consumed = 0;
    bwr.write_buffer = (uintptr_t) data;
    bwr.read_size = 0;
    bwr.read_consumed = 0;
    bwr.read_buffer = 0;
    //通过ioctl，BINDER_WRITE_READ
    res = ioctl(bs->fd, BINDER_WRITE_READ, &bwr);
    if (res < 0) {
        fprintf(stderr,"binder_write: ioctl failed (%s)\n",
                strerror(errno));
    }
    return res;
}
```



### ServiceManager获取

![](image\get_servicemanager.jpg)

```c++
//frameworks/native/libs/binder/IServiceManager.cpp
sp<IServiceManager> defaultServiceManager()
{
    if (gDefaultServiceManager != nullptr) return gDefaultServiceManager;

    {
        AutoMutex _l(gDefaultServiceManagerLock);
        while (gDefaultServiceManager == nullptr) {
            gDefaultServiceManager = interface_cast<IServiceManager>(
                ProcessState::self()->getContextObject(nullptr));
            if (gDefaultServiceManager == nullptr)
                sleep(1);
        }
    }

    return gDefaultServiceManager;
}
```

```
	获取ServiceManager对象采用单例模式，当gDefaultServiceManager存在，则直接返回，否则创建一个新对象。 发现与一般的单例模式不太一样，里面多了一层while循环，这是google在2013年1月Todd Poynor提交的修改。当尝试创建或获取ServiceManager时，ServiceManager可能尚未准备就绪，这时通过sleep 1秒后，循环尝试获取直到成功。gDefaultServiceManager的创建过程,

可分解为以下3个步骤：

ProcessState::self()：用于获取ProcessState对象(也是单例模式)，每个进程有且只有一个ProcessState对象，存在则直接返回，不存在则创建
getContextObject()： 用于获取BpBinder对象，对于handle=0的BpBinder对象，存在则直接返回，不存在才创建
interface_cast<IServiceManager>()：用于获取BpServiceManager对象
```

```c++
//frameworks/native/libs/binder/ProcessState.cpp
sp<ProcessState> ProcessState::self()
{
    Mutex::Autolock _l(gProcessMutex);
    if (gProcess != nullptr) {
        return gProcess;
    }
    gProcess = new ProcessState(kDefaultDriver);
    return gProcess;
}
```

```c++
//frameworks/native/libs/binder/ProcessState.cpp
ProcessState::ProcessState(const char *driver)
    : mDriverName(String8(driver))
    , mDriverFD(open_driver(driver))// 打开Binder驱动
    , mVMStart(MAP_FAILED)
    , mThreadCountLock(PTHREAD_MUTEX_INITIALIZER)
    , mThreadCountDecrement(PTHREAD_COND_INITIALIZER)
    , mExecutingThreadsCount(0)
    , mMaxThreads(DEFAULT_MAX_BINDER_THREADS)
    , mStarvationStartTimeMs(0)
    , mManagesContexts(false)
    , mBinderContextCheckFunc(nullptr)
    , mBinderContextUserData(nullptr)
    , mThreadPoolStarted(false)
    , mThreadPoolSeq(1)
    , mCallRestriction(CallRestriction::NONE)
{
    if (mDriverFD >= 0) {
        // mmap the binder, providing a chunk of virtual address space to receive transactions.
         //采用内存映射函数mmap，给binder分配一块虚拟地址空间,用来接收事务
        mVMStart = mmap(nullptr, BINDER_VM_SIZE, PROT_READ, MAP_PRIVATE | MAP_NORESERVE, mDriverFD, 0);
        if (mVMStart == MAP_FAILED) {
            // *sigh*
            ALOGE("Using %s failed: unable to mmap transaction memory.\n", mDriverName.c_str());
            close(mDriverFD);//没有足够空间分配给/dev/binder,则关闭驱动
            mDriverFD = -1;
            mDriverName.clear();
        }
    }

    LOG_ALWAYS_FATAL_IF(mDriverFD < 0, "Binder driver could not be opened.  Terminating.");
}
```

```c++
//frameworks/native/libs/binder/ProcessState.cpp
//open_driver作用是打开/dev/binder设备，设定binder支持的最大线程数。
static int open_driver(const char *driver)
{
     // 打开/dev/binder设备，建立与内核的Binder驱动的交互通道
    int fd = open(driver, O_RDWR | O_CLOEXEC);
    if (fd >= 0) {
        int vers = 0;
        status_t result = ioctl(fd, BINDER_VERSION, &vers);
        if (result == -1) {
            ALOGE("Binder ioctl to obtain version failed: %s", strerror(errno));
            close(fd);
            fd = -1;
        }
        if (result != 0 || vers != BINDER_CURRENT_PROTOCOL_VERSION) {
          ALOGE("Binder driver protocol(%d) does not match user space protocol(%d)! ioctl() return value: %d",
                vers, BINDER_CURRENT_PROTOCOL_VERSION, result);
            close(fd);
            fd = -1;
        }
        size_t maxThreads = DEFAULT_MAX_BINDER_THREADS;
        // 通过ioctl设置binder驱动，能支持的最大线程数
        result = ioctl(fd, BINDER_SET_MAX_THREADS, &maxThreads);
        if (result == -1) {
            ALOGE("Binder ioctl to set max threads failed: %s", strerror(errno));
        }
    } else {
        ALOGW("Opening '%s' failed: %s\n", driver, strerror(errno));
    }
    return fd;
}
```

```c++
//frameworks/native/libs/binder/ProcessState.cpp
//获取BpBinder对象
sp<IBinder> ProcessState::getContextObject(const sp<IBinder>& /*caller*/)
{
    return getStrongProxyForHandle(0);
}
```

```c++
//frameworks/native/libs/binder/ProcessState.cpp
sp<IBinder> ProcessState::getStrongProxyForHandle(int32_t handle)
{
    sp<IBinder> result;

    AutoMutex _l(mLock);
	//查找handle对应的资源项
    handle_entry* e = lookupHandleLocked(handle);

    if (e != nullptr) {
        // We need to create a new BpBinder if there isn't currently one, OR we
        // are unable to acquire a weak reference on this current one.  See comment
        // in getWeakProxyForHandle() for more info about this.
        IBinder* b = e->binder;
        if (b == nullptr || !e->refs->attemptIncWeak(this)) {
            if (handle == 0) {
               

                Parcel data;
                status_t status = IPCThreadState::self()->transact(
                        0, IBinder::PING_TRANSACTION, data, nullptr, 0);
                if (status == DEAD_OBJECT)
                   return nullptr;
            }
			//当handle值所对应的IBinder不存在或弱引用无效时，则创建BpBinder对象
            b = BpBinder::create(handle);
            e->binder = b;
            if (b) e->refs = b->getWeakRefs();
            result = b;
        } else {
            // This little bit of nastyness is to allow us to add a primary
            // reference to the remote proxy when this team doesn't have one
            // but another team is sending the handle to us.
            result.force_set(b);
            e->refs->decWeak(this);
        }
    }

    return result;
}
```

```c++
//frameworks/native/libs/binder/BpBinder.cpp
BpBinder::BpBinder(int32_t handle, int32_t trackedUid)
    : mHandle(handle)
    , mAlive(1)
    , mObitsSent(0)
    , mObituaries(nullptr)
    , mTrackedUid(trackedUid)
{
    ALOGV("Creating BpBinder %p handle %d\n", this, mHandle);

    extendObjectLifetime(OBJECT_LIFETIME_WEAK); //延长对象的生命时间
    IPCThreadState::self()->incWeakHandle(handle, this); //handle所对应的bindle弱引用 + 1
}
```

### BpServiceManager获取

```
interface_cast<IServiceManager>(
                ProcessState::self()->getContextObject(nullptr));
```

```c
//frameworks/native/libs/binder/include/binder/IInterface.h
template<typename INTERFACE>
inline sp<INTERFACE> interface_cast(const sp<IBinder>& obj)
{
    return INTERFACE::asInterface(obj);
}

//这是一个模板函数，可得出，interface_cast<IServiceManager>() 等价于 //IServiceManager::asInterface()。接下来,再来说说asInterface()函数的具体功能。
//frameworks/native/libs/binder/include/binder/IInterface.h 中的
//宏定义
#define DECLARE_META_INTERFACE(INTERFACE)      ...
#define IMPLEMENT_META_INTERFACE(INTERFACE, NAME)  ...

//将上面定义的带参数宏
位于IServiceManager.cpp文件中,
INTERFACE=ServiceManager, 
NAME=”android.os.IServiceManager”
可以得出下面
    
IServiceManager::asInterface()=
new BpServiceManager(BpBinder)
```

```
defaultServiceManager 等价于 new BpServiceManager(new BpBinder(0));
```

```
http://gityuan.com/2015/11/14/binder-add-service/
```



## 注册服务

http://gityuan.com/2015/11/21/binder-framework/

```java
/**frameworks/base/services/java/com/android/server/SystemServer.java*/
//SystemServer

// maximum number of binder threads used for system_server
// 用于system_server的绑定器线程的最大数量  

// will be higher than the system default
// 会高于系统默认值吗
private static final int sMaxBinderThreads = 31;

/**
  * The main entry point from zygote.
  */
public static void main(String[] args) {
     new SystemServer().run();
}

run->
    // Ensure binder calls into the system always run at foreground priority.
    BinderInternal.disableBackgroundScheduling(true);

    // Increase the number of binder threads in system_server
    BinderInternal.setMaxThreads(sMaxBinderThreads);

//启动系统服务
startOtherServices()->
    ServiceManager.addService("service name", service_object);

//向 ServiceManager 注册服务
///frameworks/base/core/java/android/os/ServiceManager.java
//ServiceManager
addService()->
    getIServiceManager().addService(name, service, allowIsolated, dumpPriority);


//获取 ServiceManager 代理
private static IServiceManager getIServiceManager() {
        if (sServiceManager != null) {
            return sServiceManager;
        }

        // Find the service manager
        sServiceManager = ServiceManagerNative
                .asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
        return sServiceManager;
}

//frameworks/base/core/java/com/android/internal/os/BinderInternal.java
//BinderInternal.java
/**
     * Return the global "context object" of the system.  This is usually
     * an implementation of IServiceManager, which you can use to find
     * other services.
     */
public static final native IBinder getContextObject();
//frameworks/base/core/jni/android_util_Binder.cpp
static jobject android_os_BinderInternal_getContextObject(JNIEnv* env, jobject clazz)
{
    sp<IBinder> b = ProcessState::self()->getContextObject(NULL);
    return javaObjectForIBinder(env, b);
}
//frameworks/native/libs/binder/ProcessState.cpp
sp<IBinder> ProcessState::getContextObject(const sp<IBinder>& /*caller*/)
{
    return getStrongProxyForHandle(0);
}
ProcessState::self()->getContextObject()等价于 new BpBinder(0);
ServiceManagerNative.asInterface(BinderInternal.getContextObject()) 等价于
ServiceManagerNative.asInterface(new BinderProxy())


//ServiceManager 代理
///frameworks/base/core/java/android/os/ServiceManagerNative.java
//ServiceManagerNative
@UnsupportedAppUsage
static public IServiceManager asInterface(IBinder obj)
    {
        if (obj == null) {
            return null;
        }
        IServiceManager in =
            (IServiceManager)obj.queryLocalInterface(descriptor);
        if (in != null) {
            return in;
        }

        return new ServiceManagerProxy(obj);
}

public void addService(String name, IBinder service, boolean allowIsolated, int dumpPriority)
            throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IServiceManager.descriptor);
        data.writeString(name);
        //注意这个地方
        data.writeStrongBinder(service);
        data.writeInt(allowIsolated ? 1 : 0);
        data.writeInt(dumpPriority);
        //调用BindProxy的transact函数
        mRemote.transact(ADD_SERVICE_TRANSACTION, data, reply, 0);
        reply.recycle();
        data.recycle();
    }

//jni
//frameworks/base/core/java/android/os/BinderProxy.java
//BindProxy
transact()->transactNative(int code, Parcel data, Parcel reply,
            int flags)
    
//frameworks/base/core/jni/android_util_Binder.cpp
```

```c++
//frameworks/base/core/jni/android_util_Binder.cpp

//android_util_Binder.cpp
 
static jboolean android_os_BinderProxy_transact(JNIEnv* env, jobject obj,
        jint code, jobject dataObj, jobject replyObj, jint flags)
{
    ........
    //将java对象转化为native对象
    Parcel* data = parcelForJavaObject(env, dataObj);
    .........
    Parcel* reply = parcelForJavaObject(env, replyObj);
    ........
    //得到native层的BpBinder
     IBinder* target = getBPNativeData(env, obj)->mObject.get();
    ........
    //通过BpBinder利用IPCThreadState，将请求通过Binder驱动发送给SM进程
    status_t err = target->transact(code, *data, reply, flags);
    ........
}

BinderProxyNativeData* getBPNativeData(JNIEnv* env, jobject obj) {
    return (BinderProxyNativeData *) env->GetLongField(obj, 		  gBinderProxyOffsets.mNativeData);
}
```



```c
//frameworks/native/cmds/servicemanager/service_manager.c
svcmgr_handler(){
    switch(txn->code) {
            case SVC_MGR_ADD_SERVICE:
            	do_add_service()
    }
}


do_add_service(){
     //判断服务有没有权限注册，并不是所有的服务都能注册
    if (!svc_can_register(s, len, spid)) {
        return -1;
    }
    //查询服务有没有注册过
    si = find_svc(s, len);
    if (si) {
        if (si->handle) {
            ALOGE("add_service('%s',%x) uid=%d - ALREADY REGISTERED, OVERRIDE\n",
                 str8(s, len), handle, uid);
            svcinfo_death(bs, si);
        }
        si->handle = handle;
    } else {
        si = malloc(sizeof(*si) + (len + 1) * sizeof(uint16_t));
        if (!si) {
            ALOGE("add_service('%s',%x) uid=%d - OUT OF MEMORY\n",
                 str8(s, len), handle, uid);
            return -1;
        }
        si->handle = handle;
        si->len = len;
        memcpy(si->name, s, (len + 1) * sizeof(uint16_t));
        si->name[len] = '\0';
        si->death.func = (void*) svcinfo_death;
        si->death.ptr = si;
        si->allow_isolated = allow_isolated;
        si->dumpsys_priority = dumpsys_priority;
        si->next = svclist;
        svclist = si;
    }

    binder_acquire(bs, handle);
    binder_link_to_death(bs, handle, &si->death);
    return 0;
}
    
```

```

data.writeStrongBinder(service);

public final void writeStrongBinder(IBinder val) {
    //调用了native函数
    nativeWriteStrongBinder(mNativePtr, val);
}

//frameworks/base/core/jni/android_os_Parcel.cpp

```

```c++
//frameworks/base/core/jni/android_os_Parcel.cpp
static void android_os_Parcel_writeStrongBinder(JNIEnv* env, jclass clazz, jlong nativePtr, jobject object)
{
    //native层的parcel
    Parcel* parcel = reinterpret_cast<Parcel*>(nativePtr);
    if (parcel != NULL) {
        const status_t err = parcel->writeStrongBinder(ibinderForJavaObject(env, object));
        if (err != NO_ERROR) {
            signalExceptionForError(env, clazz, err);
        }
    }
}

//frameworks/base/core/jni/android_util_Binder.cpp
sp<IBinder> ibinderForJavaObject(JNIEnv* env, jobject obj)
{
    if (obj == NULL) return NULL;

    // Instance of Binder?
    //obj为Binder类
    if (env->IsInstanceOf(obj, gBinderOffsets.mClass)) {
        //调用了JavaBBinderHolder的get方法
        JavaBBinderHolder* jbh = (JavaBBinderHolder*)
            env->GetLongField(obj, gBinderOffsets.mObject);
        return jbh->get(env, obj);
    }

    // Instance of BinderProxy?
    //obj为BinderProxy类
    if (env->IsInstanceOf(obj, gBinderProxyOffsets.mClass)) {
        return getBPNativeData(env, obj)->mObject;
    }

    ALOGW("ibinderForJavaObject: %p is not a Binder object", obj);
    return NULL;
}
```

```c++
//frameworks/native/libs/binder/include/binder/Parcel.h
//frameworks/native/libs/binder/Parcel.cpp

status_t Parcel::writeStrongBinder(const sp<IBinder>& val)
{
    return flatten_binder(ProcessState::self(), val, this);
}
status_t flatten_binder(const sp<ProcessState>& /*proc*/,
    const sp<IBinder>& binder, Parcel* out)
{
    flat_binder_object obj;

    if (IPCThreadState::self()->backgroundSchedulingDisabled()) {
        /* minimum priority for all nodes is nice 0 */
        obj.flags = FLAT_BINDER_FLAG_ACCEPTS_FDS;
    } else {
        /* minimum priority for all nodes is MAX_NICE(19) */
        obj.flags = 0x13 | FLAT_BINDER_FLAG_ACCEPTS_FDS;
    }

    if (binder != nullptr) {
        BBinder *local = binder->localBinder();//是不是本地binder，本地的意思是同一个进程中的调用
        if (!local) {
            BpBinder *proxy = binder->remoteBinder();
            if (proxy == nullptr) {
                ALOGE("null proxy");
            }
            const int32_t handle = proxy ? proxy->handle() : 0;
            obj.hdr.type = BINDER_TYPE_HANDLE;
            obj.binder = 0; /* Don't pass uninitialized stack data to a remote process */
            obj.handle = handle;
            obj.cookie = 0;
        } else {
            if (local->isRequestingSid()) {
                obj.flags |= FLAT_BINDER_FLAG_TXN_SECURITY_CTX;
            }
            obj.hdr.type = BINDER_TYPE_BINDER;
            obj.binder = reinterpret_cast<uintptr_t>(local->getWeakRefs());
            obj.cookie = reinterpret_cast<uintptr_t>(local);
        }
    } else {
        obj.hdr.type = BINDER_TYPE_BINDER;
        obj.binder = 0;
        obj.cookie = 0;
    }

    return finish_flatten_binder(binder, obj, out);
}

```

## 获取服务

```
//这里我们的App进程从SM进程得到AMS服务对应的客户端代理BinderProxy
ServiceManager.getService(Context.ACTIVITY_SERVICE);
```

```c++
//frameworks/native/cmds/servicemanager/service_manager.c
svcmgr_handler(){
	switch(txn->code) {
    	case SVC_MGR_GET_SERVICE:
    	case SVC_MGR_CHECK_SERVICE:
            handle = do_find_service(s, len, txn->sender_euid, txn->sender_pid,
                                 (const char*) txn_secctx->secctx);
            if (!handle)
            	break;
        	bio_put_ref(reply, handle);//写入数据到reply
        	return 0;
	}
    bio_put_uint32(reply, 0);//把写入数据后的reply返回

}
```

```java
//frameworks/base/core/java/android/os/ServiceManagerNative.java
//ServiceManagerProxy
@UnsupportedAppUsage
    public IBinder getService(String name) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IServiceManager.descriptor);
        data.writeString(name);
        mRemote.transact(GET_SERVICE_TRANSACTION, data, reply, 0);
    	//看这里readStrongBinder，是不是感觉跟我们上面的writeStrongBinder感觉是一对的
        IBinder binder = reply.readStrongBinder();
        reply.recycle();
        data.recycle();
        return binder;
    }
```

```c++
///frameworks/base/core/jni/android_os_Parcel.cpp
static jobject android_os_Parcel_readStrongBinder(JNIEnv* env, jclass clazz, jlong nativePtr)
{
    Parcel* parcel = reinterpret_cast<Parcel*>(nativePtr);
    if (parcel != NULL) {
        return javaObjectForIBinder(env, parcel->readStrongBinder());
    }
    return NULL;
}
```

```c++

```

```c++
///frameworks/native/libs/binder/Parcel.cpp
status_t Parcel::readStrongBinder(sp<IBinder>* val) const
{
    status_t status = readNullableStrongBinder(val);
    if (status == OK && !val->get()) {
        status = UNEXPECTED_NULL;
    }
    return status;
}
status_t Parcel::readNullableStrongBinder(sp<IBinder>* val) const
{
    return unflatten_binder(ProcessState::self(), *this, val);
}
status_t unflatten_binder(const sp<ProcessState>& proc,
    const Parcel& in, sp<IBinder>* out)
{
    const flat_binder_object* flat = in.readObject(false);

    if (flat) {
        switch (flat->hdr.type) {
            case BINDER_TYPE_BINDER:
                *out = reinterpret_cast<IBinder*>(flat->cookie);
                return finish_unflatten_binder(nullptr, *flat, in);
            case BINDER_TYPE_HANDLE:
                 //调用ProcessState的getStrongProxyForHandle函数
                *out = proc->getStrongProxyForHandle(flat->handle);
                return finish_unflatten_binder(
                    static_cast<BpBinder*>(out->get()), *flat, in);
        }
    }
    return BAD_TYPE;
}
```

```C++
static struct binderproxy_offsets_t
{
    // Class state.
    jclass mClass;
    jmethodID mGetInstance;
    jmethodID mSendDeathNotice;

    // Object state.
    jfieldID mNativeData;  // Field holds native pointer to BinderProxyNativeData.
} gBinderProxyOffsets;

struct BinderProxyNativeData {
    // Both fields are constant and not null once javaObjectForIBinder returns this as
    // part of a BinderProxy.

    // The native IBinder proxied by this BinderProxy.
    sp<IBinder> mObject;

    // Death recipients for mObject. Reference counted only because DeathRecipients
    // hold a weak reference that can be temporarily promoted.
    sp<DeathRecipientList> mOrgue;  // Death recipients for mObject.
};
BinderProxyNativeData* getBPNativeData(JNIEnv* env, jobject obj) {
    return (BinderProxyNativeData *) env->GetLongField(obj, gBinderProxyOffsets.mNativeData);
}
```



```c++
////frameworks/native/libs/binder/ProcessState.cpp
sp<IBinder> ProcessState::getStrongProxyForHandle(int32_t handle)
{
    sp<IBinder> result;

    AutoMutex _l(mLock);

    handle_entry* e = lookupHandleLocked(handle);

    if (e != nullptr) {
        // We need to create a new BpBinder if there isn't currently one, OR we
        // are unable to acquire a weak reference on this current one.  See comment
        // in getWeakProxyForHandle() for more info about this.
        IBinder* b = e->binder;
        if (b == nullptr || !e->refs->attemptIncWeak(this)) {
            if (handle == 0) {//这里handle为0的情况是为SM准备的
              
                Parcel data;
                status_t status = IPCThreadState::self()->transact(
                        0, IBinder::PING_TRANSACTION, data, nullptr, 0);
                if (status == DEAD_OBJECT)
                   return nullptr;
            }
            //我们的不为0，在这里创建了BpBinder
            b = BpBinder::create(handle);
            e->binder = b;
            if (b) e->refs = b->getWeakRefs();
            result = b;
        } else {
            // This little bit of nastyness is to allow us to add a primary
            // reference to the remote proxy when this team doesn't have one
            // but another team is sending the handle to us.
            result.force_set(b);
            e->refs->decWeak(this);
        }
    }

    return result;
}
```

```c++
///frameworks/base/core/jni/android_util_Binder.cpp

// If the argument is a JavaBBinder, return the Java object that was used to create it.
// Otherwise return a BinderProxy for the IBinder. If a previous call was passed the
// same IBinder, and the original BinderProxy is still alive, return the same BinderProxy.
jobject javaObjectForIBinder(JNIEnv* env, const sp<IBinder>& val)
{
    if (val == NULL) return NULL;
	//如果val是Binder对象，进入下面分支，此时val是BpBinder
    if (val->checkSubclass(&gBinderOffsets)) {
        // It's a JavaBBinder created by ibinderForJavaObject. Already has Java object.
        jobject object = static_cast<JavaBBinder*>(val.get())->object();
        LOGDEATH("objectForBinder %p: it's our own %p!\n", val.get(), object);
        return object;
    }

    BinderProxyNativeData* nativeData = new BinderProxyNativeData();
    nativeData->mOrgue = new DeathRecipientList;
    nativeData->mObject = val;

     //创建一个新的BinderProxy对象
    jobject object = env->CallStaticObjectMethod(gBinderProxyOffsets.mClass,
            gBinderProxyOffsets.mGetInstance, (jlong) nativeData, (jlong) val.get());
    if (env->ExceptionCheck()) {
        // In the exception case, getInstance still took ownership of nativeData.
        return NULL;
    }
    BinderProxyNativeData* actualNativeData = getBPNativeData(env, object);
    if (actualNativeData == nativeData) {
        // Created a new Proxy
        uint32_t numProxies = gNumProxies.fetch_add(1, std::memory_order_relaxed);
        uint32_t numLastWarned = gProxiesWarned.load(std::memory_order_relaxed);
        if (numProxies >= numLastWarned + PROXY_WARN_INTERVAL) {
            // Multiple threads can get here, make sure only one of them gets to
            // update the warn counter.
            if (gProxiesWarned.compare_exchange_strong(numLastWarned,
                        numLastWarned + PROXY_WARN_INTERVAL, std::memory_order_relaxed)) {
                ALOGW("Unexpectedly many live BinderProxies: %d\n", numProxies);
            }
        }
    } else {
        delete nativeData;
    }

    return object;
}

```

```C++

```


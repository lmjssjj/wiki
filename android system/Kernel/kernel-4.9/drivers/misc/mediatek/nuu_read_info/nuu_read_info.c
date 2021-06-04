
#include <linux/module.h>  
#include <linux/kernel.h> 
#include <linux/init.h>
#include <linux/kobject.h>
#include <linux/sysfs.h>	// struct attribute
#include <linux/device.h>  // struct device_attribute
//#include <linux/types.h> // ssize_t
//#include <linux/string.h>
#include <linux/sched.h>
#include <linux/stat.h>
#include <linux/fs.h>
#include <linux/kdev_t.h>
#include <linux/proc_fs.h>

#include <upmu_common.h>


static ssize_t nuu_info_write(struct file *file_p, const char __user *buf, 
	size_t size, loff_t *loft)
{
	return 0;
}

static int nuu_info_show(struct seq_file *seq, void *v)
{
	seq_printf(seq,
		"%s: %s\n"
		"%s: %s\n"
		"%s: %s\n"
		"%s: %s\n"
		"%s: %s\n"
		"%s: %s\n",
	"LCM_IC","GC9503V",
	"LCM_Vendor","DEZHIXIN",
	"LCM_Glass_Vendor","BOE",
	"MAIN_CAMERA","GC8034",
	"MEMORY","MT53E512M32D2",
	"MEMORY_VENDOR","KinstonI");

	return 0;
}

static int nuu_info_read(struct inode* i_node, struct file* file_p)
{
	single_open(file_p, &nuu_info_show, NULL);
	return 0;
}

static const struct file_operations nuu_info_ops = {
	.owner = THIS_MODULE,
	.read = seq_read,
	.write = nuu_info_write,
	.open = nuu_info_read,
	.llseek = seq_lseek,
	.release = single_release,
};

static int __init zhaoyang_init(void)
{
//	p_kobj_zy_son = kobject_create_and_add("zhaoyang_kobj_son",p_kobj_zy);
	proc_create("nuu_info_read",0644,NULL,&nuu_info_ops);

	return 0;
}

static void __exit zhaoyang_exit(void)
{
	printk("zhaoyang exit\n");
	return ;
}

module_init(zhaoyang_init);
module_exit(zhaoyang_exit);

MODULE_LICENSE("GPL"); 

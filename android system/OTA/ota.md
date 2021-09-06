# ota包生成

```
编译系统
全包
    ①$ .build/envsetup.sh
    ②$ lunch 然后选择你需要的配置
    ③$ make otapackage
```

# Android OTA增量包update.zip的生成

```
1、在源码根目录下依次执行下列命令
     $ .build/envsetup.sh
     $ lunch 然后选择你需要的配置
     $ make
     $ make otapackage
	执行上面的命令后会在out/target/product/你的项目/下生成我们第一个系统升级包。我们先将其命名为A.zip
	out/target/product/你的项目/obj/PACKAGING/target_files_intermediates/目录下，它是在用命令make otapackage之后的中间生产物，是最原始的升级包。
	
2、在源码中修改我们需要改变的部分，比如修改内核配置，增加新的驱动等等。修改后再一次执行上面的命令。就会生成第二个我们修改后生成的update.zip升级包。将  其命名为B.zip。

3、./build/tools/releasetools/ota_from_target_files -i A.zip B.zip update.zip

4、如果因为缺少misc_info.txt文件，则使用out/target/product/你的项目/obj/PACKAGING/target_files_intermediates/目录下，它是在用命令make otapackage之后的中间生产物，是最原始的升级包 下来生成 update.zip

```



# BCB(BootloaderControl Block)

```
android 系统正常启动
若启动过程中用户没有按下任何组合键，
bootloader会读取位于MISC分区的启动控制信息BCB，它是一个结构体，存放着启动命令command。根据不同的命令，系统又可以进入三种不同的启动模式；
结构体：
bootable/recovery/bootloader_message/include/bootloader_message/bootloader_message.h
```

```c++
struct bootloader_message {
    char command[32];//存放不同的启动命令
    char status[32];//update-radio或update-hboot完成存放执行结果
    char recovery[768];//存放/cache/recovery/command中的命令

    // The 'recovery' field used to be 1024 bytes.  It has only ever
    // been used to store the recovery command line, so 768 bytes
    // should be plenty.  We carve off the last 256 bytes to store the
    // stage string (for multistage packages) and possible future
    // expansion.
    char stage[32];

    // The 'reserved' field used to be 224 bytes when it was initially
    // carved off from the 1024-byte recovery field. Bump it up to
    // 1184-byte so that the entire bootloader_message struct rounds up
    // to 2048-byte.
    char reserved[1184];
};
```

```
command可能的值有两种，与值为空（即没有命令)一起区分三种启动模式。
1.command=="boot-recovery"时，系统会进入Recovery模式。
Recovery服务会具体根据/cache/recovery/command中的命令执行相应的操作(例如，升级update.zip或擦除cache , data等)。
	
2.command=="update-radia"或"update-hboot"时，
系统会进入更新firmware(更新bootloader)，具体由bootloader完成。

3.command为空时，即没有任何命令，系统会进入正常的启动，
最后进入主系统( mainsystem)。这种是最通常的启动流程。

  Android系统不同的启动模式的进入是在不同的情形下触发的，我们从SD卡中升级我们的update.zip时会进入Recovery模式是其中一种，其他的比如：系统崩溃，或则在命令行输入启动命令式也会进入Recovery或其他的启动模式。
```


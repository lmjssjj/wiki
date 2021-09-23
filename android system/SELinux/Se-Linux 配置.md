https://blog.csdn.net/tkwxty/article/details/98213671

https://source.android.com/security/selinux/customize

# Se-Linux 配置-文件节点域配置方法

## 1 配置 *_context

```
由于我们是给Linux的文件节点配置 SE-linux，故需要先在 *_context 中定义

device/mediatek/sepolicy/basic/non_plat/genfs_contexts
genfs_contexts 的原因是很多/sys/**/目录下的文件都在这里定义，故添加如下类型

genfscon sysfs /devices/platform/battery/chg_enable   u:object_r:sysfs_chg_enable:s0

genfscon sysfs /class/power_supply/battery/coulomb_count u:object_r:sysfs_coulomb_co

```

## 2 配置 file.te

```
注意这里的 fs_type, sysfs_type 不要漏掉

type sysfs_chg_enable, fs_type, sysfs_type;
type sysfs_coulomb_count, fs_type, sysfs_type;

```

## 3 配置运行时报的avc问题

**SE-LINUX 配置公式**

```
 avc: denied { read } for pid=8548 comm="owercurrenttest" name="chg_enable" dev="sysfs" ino=26104 scontext=u:r:system_app:s0 tcontext=u:object_r:sysfs_chg_enable:s0 tclass=file permissive=0
```

```
allow system_app sysfs_chg_enable:file { read write open getattr };
```

#### 3.2 avc 日志 2

```
06-27 01:55:05.304000  9318  9318 I .cty.kuluncount: type=1400 audit(0.0:1174): avc: denied { search } for name="battery" dev="sysfs" ino=12400 scontext=u:r:untrusted_app_27:s0:c512,c768 tcontext=u:object_r:sysfs_batteryinfo:s0 tclass=dir permissive=1
06-27 01:55:05.308000  9318  9318 I .cty.kuluncount: type=1400 audit(0.0:1177): avc: denied { getattr } for path="/sys/devices/platform/battery/power_supply/battery/coulomb_count" dev="sysfs" ino=26050 scontext=u:r:untrusted_app_27:s0:c512,c768 tcontext=u:object_r:sysfs_batteryinfo:s0 tclass=file permissive=1
```

完整配置，在 untrusted_app_27.te配置如下

```
allow untrusted_app_27 sysfs_batteryinfo:dir search;
allow untrusted_app_27 sysfs_batteryinfo:file { getattr open read };```


```

## 4 查看配置情况

```
find ./out/target/product/k63v1us_64_bsp/obj/ -name "policy.conf"
./out/target/product/k63v1us_64_bsp/obj/ETC/sepolicy_neverallows_intermediates/policy.conf

```

```
grep -irn “chg_enable” ./out/target/product/k63v1us_64_bsp/obj/ETC/sepolicy_neverallows_intermediates/policy.conf
```

```
grep -irn "chg_enable" ./out/target/product/k63v1us_64_bsp/obj/ETC/sepolicy_neverallows_intermediates/policy.conf
52203:type sysfs_chg_enable, fs_type, sysfs_type;
63219:allow system_app sysfs_chg_enable:file { read write open getattr };
79911:genfscon sysfs /devices/platform/battery/chg_enable   u:object_r:sysfs_chg_enable:s0


```

## 5 验证测试

下述中可以正常进行文件节点的读写

```
	Line 3394: 06-26 09:15:15.845106 10273 10273 D SU_DEBUG: writeNodeState nodeType = NODE_TYPE_BATTERY_CHARGING_ENABLED, value = 0
	Line 3395: 06-26 09:15:15.845650 10273 10273 D SU_DEBUG: writeFile: start>>>>>>>>>>>>>>>>>>
	Line 3397: 06-26 09:15:15.854321 10273 10273 D SU_DEBUG: getChargingEnable value = 0
	Line 3398: 06-26 09:15:15.854956 10273 10273 D SU_DEBUG: writeNodeState getChargingEnable = 0
	Line 3489: 06-26 09:15:17.356138 10273 10273 D SU_DEBUG: getNodeState nodeType = NODE_TYPE_BATTERY_CHARGING_ENABLED
	Line 3490: 06-26 09:15:17.360332 10273 10273 D SU_DEBUG: getChargingEnable value = 0
	Line 3491: 06-26 09:15:17.361708 10273 10273 D SU_DEBUG: writeNodeState nodeType = NODE_TYPE_BATTERY_CHARGING_ENABLED, value = 1
	Line 3492: 06-26 09:15:17.361872 10273 10273 D SU_DEBUG: writeFile: start>>>>>>>>>>>>>>>>>>
	Line 3493: 06-26 09:15:17.371975 10273 10273 D SU_DEBUG: getChargingEnable value = 1
	Line 3494: 06-26 09:15:17.372573 10273 10273 D SU_DEBUG: writeNodeState getChargingEnable = 1

```

```
device/mediatek/sepolicy/bsp/non_plat/system_app.te
```

```

```


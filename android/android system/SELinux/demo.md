```
avc: denied { open } for path="/data/test.png" dev="mmcblk0p37" ino=12 scontext=u:r:untrusted_app_27:s0:c79,c256,c512,c768 tcontext=u:object_r:system_data_file:s0 tclass=file permissive=0 app=com.nuumobile.nuuretailmode
```

```
device\mediatek\sepolicy\basic\non_plat\untrusted_app_27.te
allow untrusted_app_27 system_data_file:file { open read };
```


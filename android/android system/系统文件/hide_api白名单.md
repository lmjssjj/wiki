```
android9.0后对hide方法反射限制需要系统签名或者加入白名单才可以反射hide方法
android Q开始,禁止所有反射方法调用,需要系统签名或者加入白名单才可以反射
frameworks/base/data/etc/hiddenapi-package-whitelist.xml
/system/etc/sysconfig # ls hiddenapi-package-whitelist.xml
```


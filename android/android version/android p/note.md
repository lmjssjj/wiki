# Android P 限制了明文流量的网络请求

Android P 限制了明文流量的网络请求也就拒绝http请求

```
1、在application中添加
   android:usesCleartextTraffic="true"
2、
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <base-config cleartextTrafficPermitted="true" />
    </network-security-config>
    在application中添加
    android:networkSecurityConfig="@xml/network_security_config"


```


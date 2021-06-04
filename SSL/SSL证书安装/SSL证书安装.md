# SSL证书安装部署指南

## Tomcat9

```xml
<!--在conf文件夹，我们找到server.xml文件-->
<Connector port="80" protocol="HTTP/1.1" 
               connectionTimeout="20000" 
               redirectPort="8443" />
<Connector port="443" protocol="HTTP/1.1" SSLEnabled="true"  
               maxThreads="150" scheme="https" secure="true"  
               clientAuth="false" sslProtocol="TLS"   
   			   keystoreType="PKCS12"
        	   keystoreFile="证书路径"  
        	   keystorePass="证书密码" />  
```

## Tomcat8

```xml
<Connector port="80" protocol="HTTP/1.1" 
               connectionTimeout="20000" 
               redirectPort="8443" />
<Connector port="443" protocol="HTTP/1.1" SSLEnabled="true"  
               maxThreads="150" scheme="https" secure="true"  
               clientAuth="false" sslProtocol="TLS"   
   			   keystoreType="PKCS12"
               keystoreFile="证书路径"  
               keystorePass="证书密码" />  
```


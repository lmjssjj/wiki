```
android/build/target/product/security/ 找到签名文件“platform.pk8”和“platform.x509.pem” 平台签名

```

1. 生成platform.pem文件

   ```
   openssl pkcs8 -inform DER -nocrypt -in platform.pk8 -out platform.pem
   ```

2. 生成platform.p12文件，设置别名和密码，即AS打包APK时输入的别名和密码

   ```
   openssl pkcs12 -export -in platform.x509.pem -out platform.p12 -inkey platform.pem -password pass:android（这个是密码） -name key(这个是别名)
   ```

3. 生成platform.jks(钥匙文件) （-srcstorepass android）是.jks文件的密码

   ```
   keytool -importkeystore -deststorepass android -destkeystore ./platform.jks -srckeystore ./platform.p12 -srcstoretype PKCS12 -srcstorepass android
   ```

   
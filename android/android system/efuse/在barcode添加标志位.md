## 存储是否efuse节点

```
EFUSE_NODE = "sys/kernel/security_chip_check/status"
```

## 给系统应用读取文件权限

```
在文件中授权
system_app.te
allow system_app sysfs_efuse_file:file { read open };
```

## 读取efuse文件节点

```java
//命令工具 ShellExe.java
public static String getInfo(String file) {
     String result = null;
     try {
          int ret = ShellExe.execCommand("cat" + file);
         if (0 == ret) {
               result = ShellExe.getOutput();
         } else {
              result = null;
         }
      } catch (IOException e) {
          result = null;
      }
      return result;
}
```

## 开机完成广播保存更新标志位

```java
 mBarcodeBytes_efuse = NvRAMHelperUtil.readBarCodeFromProductFile();
            String efuse = Utils.getInfo(EFUSE_NODE);
            if(mBarcodeBytes_efuse!=null && mBarcodeBytes_efuse[BARCODE_EFUSE_OFFSET] != BARCODE_PASS_P ) {
                if (!"1".equals(efuse)) {
                    mBarcodeBytes_efuse[BARCODE_EFUSE_OFFSET] = BARCODE_FAIL_F;
                } else {
                    mBarcodeBytes_efuse[BARCODE_EFUSE_OFFSET] = BARCODE_PASS_P;
                }          
                NvRAMHelperUtil.writeBarCodeToProductFile(mBarcodeBytes_efuse);
            }
```


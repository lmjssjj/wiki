```
/0609-0613/wind/A460/master/device/mediatek/common/overlay/ago/frameworks/base/core/res/res/drawable-nodpi/default_wallpaper.jpg//默认壁纸
/0609-0613/wind/A460/master/device/mediateksample/A460/bootanimation/bootanimation.zip//开机动画
/0609-0613/wind/A460/master/device/mediateksample/A460/BoardConfig.mk//分区大小
/0609-0613/wind/A460/master/device/mediateksample/A460/device.mk//里面拷贝复制一些文件
/0609-0613/wind/A460/master/device/mediateksample/A460/system.prop // 系统属性
/0609-0613/wind/A460/master/device/mediateksample/A460/version//版本
/0609-0613/wind/A460/master/device/mediateksample/A460/windconfig//定义一些信息
/0609-0613/wind/A460/master/device/mediateksample/A460/custom.conf  //UA
/0609-0613/wind/A460/master/device/mediateksample/A460/android.hardware.usb.host.xml
/0609-0613/wind/A460/master/build/make/target/product/security/wind///签名文件
/0609-0613/wind/A460/Sprint/device/mediatek/common/device.mk//  里面拷贝了 apns-conf.xml  spn-conf.xml
/0609-0613/wind/A460/Sprint/device/mediatek/config/apns-conf.xml
/0609-0613/wind/A460/Sprint/device/mediatek/common/spn-conf.xml
/0609-0613/wind/A460/USCC/vendor/partner_gms/apps///GMS
/0609-0613/wind/A460/USCC/vendor/partner_gms/products/gms_go.mk

frameworks/base/data/etc/ system/etc/

device\mediatek\build\build\tools\ptgen\MT6761ptgen.pl //"$PRODUCT_OUT/$ArgList{PLATFORM}_Android_scatter.txt";


device\mediatek\vendor\common\device.mk  //添加PRODUCT_PACKAGES += AntAgingTest
device\mediatek\folder\device.mk         //添加PRODUCT_PACKAGES += AntAgingTest

BUILD_FINGERPRINT  //build\make\core\Makefile
                   //getprop ro.build.fingerprint
                   //out\target\product\project\build_fingerprint.txt



```



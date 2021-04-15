# APN

```
	APN全称是Access Point Name，中文即接入点，是通过手机上网时必须配置的一个参数，它决定了手机通过哪种接入方式来访问网络。
```

```
	Android的网络配置作为资源文件写入了XML(/frameworks/base/core/res/res/xml/apns.xml)，这个资源文件作为Android的默认Apns配置，不建议修改该文件，且一般为空。因为Apn的配置是根据不同的硬件产品而不同，所以为不同的硬件产品建立各自的配置文件(system/etc/apns-conf.xml) ，而不去改动默认的配置文件(apns.xml)。
```

```
/packages/providers/TelephonyProvider/src/com/android/providers/telephony/TelephonyProvider.java
apn //数据库创建 初始化
```

```
<uses-permission android:name="android.permission.WRITE_APN_SETTINGS"/>
```

```java
ContentResolver resolver = getContentResolver();
ContentValues values = new ContentValues();
values.put("name", "专用APN1");                                  
values.put("apn", "unim2m.njm2mapn1");                                     
values.put("type", "default,supl");                            			
Cursor c = null;
Uri newRow = resolver.insert(APN_URI, values);
if (newRow != null) {
	c = resolver.query(newRow, null, null, null, null);
	int idIndex = c.getColumnIndex("_id");
	c.moveToFirst();
	id = c.getShort(idIndex);
	Log.v("lmjssjj", "new id:" + id);
	Toast.makeText(this,"new id:" + id,Toast.LENGTH_SHORT).show();
}else {
	Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show();
}
if (c != null)
	c.close();
```


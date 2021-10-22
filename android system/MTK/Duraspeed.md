## 关闭快霸

```
/** 在device\mediateksample\k61v1_32_bsp_hdp中修改 **/
//MTK_DURASPEED_DEFAULT_ON = yes
//MTK_DURASPEED_SUPPORT = yes
MTK_DURASPEED_DEFAULT_ON = no
MTK_DURASPEED_SUPPORT = no
```

## 快霸白名单

```

```

## 

```java
duraspeed的核心功能是以jar包导入的，具体可查看
vendor/mediatek/proprietary/frameworks/opt/duraspeed_lib下的jar包

vendor\mediatek\proprietary\frameworks\base\services\core\java\com\mediatek\server\am\AmsExtImpl.java\
public AmsExtImpl() {
	if (isDuraSpeedSupport) {
            String className1 = "com.mediatek.duraspeed.manager.DuraSpeedService";
            String className2 = "com.mediatek.duraspeed.suppress.SuppressAction";
            String classPackage = "/system/framework/duraspeed.jar";
            Class<?> clazz = null;
            try {
                sClassLoader = new PathClassLoader(classPackage, AmsExtImpl.class.getClassLoader());
                clazz = Class.forName(className1, false, sClassLoader);
                mDuraSpeedService = (IDuraSpeedNative) clazz.getConstructor().newInstance();

                clazz = Class.forName(className2, false, sClassLoader);
                mSuppressAction = (ISuppressAction) clazz.getConstructor().newInstance();
            } catch (Exception e) {
                Slog.e("AmsExtImpl", e.toString());
            }

        //add 过滤应用 begin    
		try {
                Method method= mDuraSpeedService.getClass().getMethod("getPlatformWhitelist", null);
                //获取白名单列表
                Object obj = method.invoke(mDuraSpeedService);                
                if(obj != null && obj instanceof List) {
                        List<String> list = (List<String>) obj;
                        //加入自己的过滤app
                        list.add("com.my.launcher");
                        //更新白名单
                        method = mDuraSpeedService.getClass().getMethod("setAppWhitelist", List.class);
                        method.invoke(mDuraSpeedService, list);
                }
            }catch (Exception e){
                Slog.e("AmsExtImpl add app to whitelist ", e.toString());
            }
        }
       //add 过滤应用 end
}
```


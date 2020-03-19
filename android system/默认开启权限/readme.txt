1、添加默认开启权限
	default-permissions.xml 中配置开启权限的应用包名及权限
	PRODUCT_COPY_FILES += $(LOCAL_PATH)/default-permissions.xml:$(TARGET_COPY_OUT_VENDOR)/etc/default-permissions/default-permissions.xml

2、开启其他权限
	在vendor\mediatek\proprietary\frameworks\base\data\etc\目录下添加配置文件 开启权限
	正常在privapp-permissions-mediatek.xml文件添加对应包名及其权限
	
	

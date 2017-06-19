
    public static boolean isNycOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
/**
	 * 
	 * @Title: isBootCompleted   
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 
	 * @author: lmjssjj
	 * @date:  2017年6月16日 下午2:14:20    
	 * @param: @return    
	 * @return: boolean      
	 * @throws
	 */
    public static boolean isBootCompleted() {
        return "1".equals(getSystemProperty("sys.boot_completed", "1"));
    }
    /**
     * @Title: getSystemProperty   
     * @Description: TODO(这里用一句话描述这个方法的作用) 
     
     * @author: lmjssjj
     * @date:  2017年6月16日 下午2:14:11    
     * @param: @param property
     * @param: @param defaultValue
     * @param: @return    
     * @return: String      
     * @throws
     */
    public static String getSystemProperty(String property, String defaultValue) {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method getter = clazz.getDeclaredMethod("get", String.class);
            String value = (String) getter.invoke(null, property);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to read system properties");
        }
        return defaultValue;
    }
    /**
     * @Title: aaa   
     * @Description: TODO(这里用一句话描述这个方法的作用) 
     
     * @author: lmjssjj
     * @date:  2017年6月16日 下午2:13:41    
     * @param:     
     * @return: void      
     * @throws
     */
    public static boolean isSdCardReady(Context context) {
    	if(isNycOrAbove()){
    		return isBootCompleted();
    	}else{
    		return context.registerReceiver(null,
					new IntentFilter(StartupReceiver.SYSTEM_READY)) != null;
    	}
	}

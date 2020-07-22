
import android.os.Bundle;

//*******************Caution:Add all new methods at the end.Otherwise EnterpriseAgent will stop working**************************// 

interface IEnterpriseAgentService 
{
     String subract(in int ValueFirst, in int valueSecond);
    
     double  getEnterpriseAgentVersion();
     
     //New apis 
     String clearData(String packageName);
     int selfUpdateAdmin(String apkFilePath);
     void copyToSystemFolder();
     void removeFromSystemFolder(boolean isUninstall);
     void executeCommands(in Bundle script,int delay); 
     void putGlobalSettings(String key,String value);
     
     //Below are apis of ServiceIX
     String hasSpecialPermissions();
     String isSupported();
     String isActivated();
     String isAboveLockRequired(); 
     String isAdminRequired();
     String canToggleGPS();
     String canToggleAirplaneMode();
     String isRebootRequired();
     int getPriority();
     void activate();
     void reboot();
     void safeMode(boolean disable);     
     void factoryReset(boolean disable);
     void allowMultiWindowMode(boolean disable);  
     void kioskMode( boolean disable);
     void enableAllHardwareKeys( );
     void disbleHardwareKeys(in Bundle currentHdKeys);
     void disableUsbStorage(boolean disable);
     void disableBottomBar( boolean enable);
     void gps(int value);
     void mobileData(int state);
     void blueToothState(int state);
     void wifiState(int state);   
     void suppressSystemWindows(boolean enabled); 
     String runAboveLockScreen();
     String isSureFoxDefaultHomeRequired();
     String clearNotifications();
     String wipeRecentTasks(boolean enabled);
     String getKioskHomePackage();
     String airplaneMode( int state,String flightModeRadios);
     String clearProxyHostServer(String host, int port); 
     String allowSVoice (boolean allow);
     void nfcMode(int val);
     String disableAirViewMode(boolean allow);
     String disableAirCommandMode(boolean allow);
     String disableSmartClipMode(boolean allow);
     String allowMultipleUsers(boolean allow); 
     String multipleUsersSupported(); 
     String allowLockScreenView(int view, boolean allow);
     
    
     
     //Below are apis of ApplicationUtilityIX
     String isApplicationUtilitySupported();
     String enableApplication( String pkg);
     String disableApplication( String pkg);
     void enableApplications(in String[] pkgs);
     String[] enableAllDisabledApps();
     void disableApplications(in String[] pkgs);
     String killApplication(String pkg);
     void killApplications(in String[] pkgs);
     String killProcess(int processId);
     String isDisableOtherHomeScreensSupported();
     
     //Below are apis of HideBottomBarUtility
     String isHideBottomBarSupported(boolean forceCheck);
     String hideBottomBar( boolean useAdvHide);
     String showBottomBar( boolean useAdvHide);
     String hasSignaturePermissions();
     
     //Switch between KNOX and Normal
     void switchToNormal();//Need to modifiy in future 
     void switchToSamsung();
     
      //NixIx API's added to ServiceIx.
     String install(String apkPath);
     String setKiosk(String packageName);
     String uninstall(String packageName);
     String canHaveSpecialPermission();
     //boolean obtainPermissions(ActivationCallback callback);
     String relinquishPermissions();
     String wipeApplicationData(String packageName);
     String shutdown();
     String setDateTime(long timeStamp);
     void lockUnLockDevice(boolean isLocked);
     void powerOnOff();
     void injectPointerEvent(int eventType, float posX, float posY);
     void inputKeyEvent(int keyCode);
     void injectKeyEvent(int keyCode, boolean isShiftEventRequired);
     Bitmap screencap(float currentWidth, float currentHeight, boolean cmdLine); 
     String addAccount(String userName,String password, String serverType, String incomingServerAddress, String outgoingServerAddress, int incomingPort, int outgoingPort, boolean useSSL, boolean useSSLCertificate, boolean useTLS, boolean useTLSCertificate, String signature, String  emailAddress, String  domainName); 
     String deleteAccount(String userName,String password, String serverType, String incomingServerAddress, String outgoingServerAddress, int incomingPort, int outgoingPort, boolean useSSL, boolean useSSLCertificate, boolean useTLS, boolean useTLSCertificate, String signature, String  emailAddress, String  domainName);
     String addMSExchangeAccount(String userName,String password, String serverType, String incomingServerAddress, String outgoingServerAddress, int incomingPort, int outgoingPort, boolean useSSL, boolean useSSLCertificate, boolean useTLS, boolean useTLSCertificate, String signature, String  emailAddress, String  domainName);
     String deleteMSExchangeAccount(String userName,String password, String serverType, String incomingServerAddress, String outgoingServerAddress, int incomingPort, int outgoingPort, boolean useSSL, boolean useSSLCertificate, boolean useTLS, boolean useTLSCertificate, String signature, String  emailAddress, String  domainName);
     String getSupportedType();
     String isMDMScriptSupported(); 
     void enableAPN(String apnname, String user, String password, String server, String mmsc, String mmsProxy, String mmsPort, String mcc, String mnc, String type, String name);
     String disableApplicationUninstall(in String[] packages);
     String enableApplicationUninstall (in String[] packages);
     String  canReadFrameBuffer(); 
     void swipe(String command, int direction);
     void click(int x, int y, boolean isLong, int motion);
     void sendPointerSync(in MotionEvent event);
     void swipeArr(in int[] scaledCordinates, int action);
     String hasPermission(String permission);
     String allowOTAUpgrade(boolean allow);

    //*******************Caution:Always reuse the invokeMethod , adding of new methods is strictly prohibited **************************//
     Bundle invokeMethod(String methodName, in Bundle inArguments, out Bundle outArguments);
     void switchMobileData(int id);//#18755 This methoded is added above invoke method by raaziya causing issues.instead of creating this method implementation should have be done inside invoke method
    //*******************Caution:Always reuse the invokeMethod , adding of new methods is strictly  prohibited **************************//
}
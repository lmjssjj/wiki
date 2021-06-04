## PhoneGlobals创建

```java
//创建了PhoneGlobals public class PhoneApp extends Application 
com.android.phone.PhoneApp.onCreate(){
	mPhoneGlobals = new PhoneGlobals(this);
    mPhoneGlobals.onCreate();
}
```

```java
//注册一系列的监听
com.android.phone.PhoneGlobals.onCreate(){
    // Initialize the telephony framework
    PhoneFactory.makeDefaultPhones(this);
}
```

```java
//创建和初始化一系列
com.android.internal.telephony.PhoneFactory
com.android.internal.telephony.PhoneFactory.makeDefaultPhone(Context){
	/* In case of multi SIM mode two instances of Phone, RIL are created,
                   where as in single SIM mode only instance. isMultiSimEnabled() function checks
                   whether it is single SIM or multi SIM mode */
          int numPhones = TelephonyManager.getDefault().getPhoneCount();

          int[] networkModes = new int[numPhones];
          sPhones = new Phone[numPhones];
          sCommandsInterfaces = new RIL[numPhones];
          sTelephonyNetworkFactories = new TelephonyNetworkFactory[numPhones];

          for (int i = 0; i < numPhones; i++) {
                    // reads the system properties and makes commandsinterface
                    // Get preferred network type.
                networkModes[i] = RILConstants.PREFERRED_NETWORK_MODE;

                  Rlog.i(LOG_TAG, "Network Mode set to " 		            +Integer.toString(networkModes[i]));
                    sCommandsInterfaces[i] =
                            telephonyComponentFactory.makeRil(context, networkModes[i],
                            cdmaSubscription, i);
                }
    
    //创建UiccController
    sUiccController = UiccController.make(context, sCommandsInterfaces);
    
    
	for (int i = 0; i < numPhones; i++) {
                    Phone phone = null;
                    int phoneType = TelephonyManager.getPhoneType(networkModes[i]);
                    if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
                        phone = telephonyComponentFactory.makePhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_GSM,
                                telephonyComponentFactory.getInstance());
                    } else if (phoneType == PhoneConstants.PHONE_TYPE_CDMA) {
                        phone = telephonyComponentFactory.makePhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_CDMA_LTE,
                                telephonyComponentFactory.getInstance());
                    }
                    Rlog.i(LOG_TAG, "Creating Phone with type = " + phoneType + " sub = " + i);

                    sPhones[i] = phone;
                }
}
```

```java
 //创建UiccController
com.android.internal.telephony.uicc.UiccController.make(Context, CommandsInterface[])
 	mInstance = telephonyComponentFactory.makeUiccController(c, ci);
public UiccController makeUiccController(Context c, CommandsInterface[] ci) {
        return new UiccController(c, ci);
}
/**
 * This class is responsible for keeping all knowledge about
 * Universal Integrated Circuit Card (UICC), also know as SIM's,
 * in the system. It is also used as API to get appropriate
 * applications to pass them to phone and service trackers.
 *
 * UiccController is created with the call to make() function.
 * UiccController is a singleton and make() must only be called once
 * and throws an exception if called multiple times.
 *
 * Once created UiccController registers with RIL for "on" and "unsol_sim_status_changed"
 * notifications. When such notification arrives UiccController will call
 * getIccCardStatus (GET_SIM_STATUS). Based on the response of GET_SIM_STATUS
 * request appropriate tree of uicc objects will be created.
 *
 * Following is class diagram for uicc classes:
 *
 *                       UiccController
 *                            #
 *                            |
 *                        UiccSlot[]
 *                            #
 *                            |
 *                        UiccCard
 *                            #
 *                            |
 *                       UiccProfile
 *                          #   #
 *                          |   ------------------
 *                    UiccCardApplication    CatService
 *                      #            #
 *                      |            |
 *                 IccRecords    IccFileHandler
 *                 ^ ^ ^           ^ ^ ^ ^ ^
 *    SIMRecords---- | |           | | | | ---SIMFileHandler
 *    RuimRecords----- |           | | | ----RuimFileHandler
 *    IsimUiccRecords---           | | -----UsimFileHandler
 *                                 | ------CsimFileHandler
 *                                 ----IsimFileHandler
 *
 * Legend: # stands for Composition
 *         ^ stands for Generalization
 *
 * See also {@link com.android.internal.telephony.IccCard}
 */
public class UiccController extends Handler {
    public UiccController(Context c, CommandsInterface []ci) {
		mUiccSlots = new UiccSlot[numPhysicalSlots];
        //注册状态监听
        for (int i = 0; i < mCis.length; i++) {
            mCis[i].registerForIccStatusChanged(this, EVENT_ICC_STATUS_CHANGED, i);        
            if (!StorageManager.inCryptKeeperBounce()) {
                mCis[i].registerForAvailable(this, EVENT_RADIO_AVAILABLE, i);
            } else {
                mCis[i].registerForOn(this, EVENT_RADIO_ON, i);
            }
            mCis[i].registerForNotAvailable(this, EVENT_RADIO_UNAVAILABLE, i);
            mCis[i].registerForIccRefresh(this, EVENT_SIM_REFRESH, i);
        }
    }
}

```



```java
//创建phone 对象
com.android.internal.telephony.TelephonyComponentFactory.makePhone(Context, CommandsInterface, PhoneNotifier, int, int, TelephonyComponentFactory){
     return new GsmCdmaPhone(context, ci, notifier, phoneId, precisePhoneType,
                telephonyComponentFactory);
}

public class GsmCdmaPhone extends Phone
//父类构造函数中
mUiccController = UiccController.getInstance();
mUiccController.registerForIccChanged(this, EVENT_ICC_CHANGED, null);
//当icc change 时：
public void handleMessage(Message msg) {
			case EVENT_ICC_CHANGED:
                onUpdateIccAvailability();
                break;
    
onUpdateIccAvailability：
     if (isPhoneTypeCdmaLte() || isPhoneTypeCdma()) {   
            SIMRecords newSimRecords = null;
            if (newUiccApplication != null) {
                newSimRecords = (SIMRecords) newUiccApplication.getIccRecords();
            }
            mSimRecords = newSimRecords;
            if (mSimRecords != null) {
                mSimRecords.registerForRecordsLoaded(this, EVENT_SIM_RECORDS_LOADED, null);
            }

```



## 读取EF文件信息

​		读取SIM卡EF文件信息的过程是由 IccFileHandler 来实现的，根据EF文件的类型，调用不同的方法，loadEFTransparent()和loadEFLinearFixed()最终都会调用RIL.java的iccIOForApp()方法;

```java
com.android.internal.telephony.uicc.SIMRecords.fetchSimRecords(){
		...
 		mFh.loadEFTransparent(EF_ICCID, obtainMessage(EVENT_GET_ICCID_DONE));
        mRecordsToLoad++;

        // FIXME should examine EF[MSISDN]'s capability configuration
        // to determine which is the voice/data/fax line
        new AdnRecordLoader(mFh).loadFromEF(EF_MSISDN, getExtFromEf(EF_MSISDN), 1,
                    obtainMessage(EVENT_GET_MSISDN_DONE));
        mRecordsToLoad++;

        // Record number is subscriber profile
        mFh.loadEFLinearFixed(EF_MBI, 1, obtainMessage(EVENT_GET_MBI_DONE));
        mRecordsToLoad++;

        mFh.loadEFTransparent(EF_AD, obtainMessage(EVENT_GET_AD_DONE));
        mRecordsToLoad++;
        ...
}

```

​		通过IccFileHandler，在通过RIL 发送请求

```java
com.android.internal.telephony.uicc.IccFileHandler
//eg
public void loadEFLinearFixed(int fileid, String path, int recordNum, Message onLoaded) {
        String efPath = (path == null) ? getEFPath(fileid) : path;
        Message response
                = obtainMessage(EVENT_GET_RECORD_SIZE_DONE,
                        new LoadLinearFixedContext(fileid, recordNum, efPath, onLoaded));

        mCi.iccIOForApp(COMMAND_GET_RESPONSE, fileid, efPath,
                        0, 0, GET_RESPONSE_EF_SIZE_BYTES, null, null, mAid, response);
    }
```

```java
	com.android.internal.telephony.RIL
   public void iccIOForApp(int command, int fileId, String path, int p1, int p2, int p3,
                 String data, String pin2, String aid, Message result) {
        IRadio radioProxy = getRadioProxy(result);
        ...
        ...
            try {
                radioProxy.iccIOForApp(rr.mSerial, iccIo);
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(rr, "iccIOForApp", e);
            }
        }
    }
```

​		数据通过RadioResponse responseXXX回调结果

```java
com.android.internal.telephony.RadioResponse
//通过responseXXX 在通通过sendMessageResponse 把结果回调到，调用者
    public static void sendMessageResponse(Message msg, Object ret) {
        if (msg != null) {
            AsyncResult.forMessage(msg, ret, null);
            msg.sendToTarget();
        }
    }


```


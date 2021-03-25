package com.nuumobile.emenu.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.AppUtils;

import java.util.Locale;

public class ResUtils {
    public static final String TAG = "LanguageUtil";
    private static final String APPNAME = "you app packagename";
    public static final String ERROR_LABEL = "";
    private static final String DEFAULT_COUNTRY = "US";
    private static final String DEFAULT_LANGUAGE = "en";

	//getStringByLocale(context, stringId, “en”, “US”);
    public static String getStringByLocale(Context context, int stringId, String language, String country) {
        /*for (Locale locale : getAvailableLocales()) {

            String lang = locale.getLanguage();
            String coun = getCountryPrivate(locale);
            String localeStr = country.equals("") ? language : (language + "_" + country);

            Loger.w("ruijie", "language = " + lang + ", country = " + coun + "," +
                    "localeStr = " + localeStr);
        }*/
        Resources resources = getApplicationResource(context.getApplicationContext().getPackageManager(),
                APPNAME, new Locale(language, country));
        if (resources == null) {
            return ERROR_LABEL;
        } else {
            try {
                return resources.getString(stringId);
            } catch (Exception e) {
                return ERROR_LABEL;
            }
        }
    }

    public static String getStringToEnglish(Context context, int stringId) {
        return getStringByLocale(context, stringId, DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
    }

    private static Resources getApplicationResource(PackageManager pm, String pkgName, Locale l) {
        Resources resourceForApplication = null;
        try {
            resourceForApplication = pm.getResourcesForApplication(pkgName);
            updateResource(resourceForApplication, l);
        } catch (PackageManager.NameNotFoundException e) {

        }
        return resourceForApplication;
    }

    private static void updateResource(Resources resource, Locale l) {
        Configuration config = resource.getConfiguration();
        config.locale = l;
        resource.updateConfiguration(config, null);
    }

}

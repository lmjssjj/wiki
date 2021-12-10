	public void loadVerifyApps(ArrayList<AppInfo> removeApps) {

		final ContentResolver contentResolver = context.getContentResolver();
		final PackageManager manager = context.getPackageManager();
		final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);

		LauncherAppState app = LauncherAppState.getInstance();
		InvariantDeviceProfile profile = app.getInvariantDeviceProfile();

		SharedPreferences prefs = context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
				Context.MODE_PRIVATE);
		boolean bl = prefs.getBoolean(LauncherProvider.EMPTY_DATABASE_CREATED, false);

		if (bl||removeApps==null||removeApps.isEmpty()) {
			return;
		}

		final ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
		final ArrayList<Long> removeIds = new ArrayList<>();
		Uri uri = LauncherSettings.Favorites.CONTENT_URI;
		final Cursor c = contentResolver.query(uri, null, null, null, null);

		try {
			final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
			final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
			final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
			final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
			final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
			final int appWidgetIdIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_ID);
			final int appWidgetProviderIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_PROVIDER);
			final int profileIdIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.PROFILE_ID);
			final LongSparseArray<UserHandleCompat> allUsers = new LongSparseArray<>();
			for (UserHandleCompat user : mUserManager.getUserProfiles()) {
				allUsers.put(mUserManager.getSerialNumberForUser(user), user);
			}
			final CursorIconInfo cursorIconInfo = new CursorIconInfo(c);
			ShortcutInfo info;
			String intentDescription;
			LauncherAppWidgetInfo appWidgetInfo;
			int container;
			long id;
			long serialNumber;
			Intent intent;
			UserHandleCompat user;

			while (c.moveToNext()) {
				int itemType = c.getInt(itemTypeIndex);
				container = c.getInt(containerIndex);

				switch (itemType) {
				case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
				case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
					id = c.getLong(idIndex);
					intentDescription = c.getString(intentIndex);
					serialNumber = c.getInt(profileIdIndex);
					user = allUsers.get(serialNumber);

					intent = Intent.parseUri(intentDescription, 0);
					ComponentName cn = intent.getComponent();

					info = getShortcutInfo(c, context, titleIndex, cursorIconInfo);
					if (info != null) {
						info.id = id;
						info.intent = intent;
						info.container = container;
						info.intent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
						if (info.promisedIntent != null) {
							info.promisedIntent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
						}
						items.add(info);
					} else {
						throw new RuntimeException("Unexpected null ShortcutInfo");
					}
					break;
				case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
				case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET:

					int appWidgetId = c.getInt(appWidgetIdIndex);
					serialNumber = c.getLong(profileIdIndex);
					String savedProvider = c.getString(appWidgetProviderIndex);
					id = c.getLong(idIndex);
					user = allUsers.get(serialNumber);

					final ComponentName component = ComponentName.unflattenFromString(savedProvider);

					final LauncherAppWidgetProviderInfo provider = LauncherModel.getProviderInfo(context,
							ComponentName.unflattenFromString(savedProvider), user);

					appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId, component);
					appWidgetInfo.id = id;
					appWidgetInfo.user = user;
					appWidgetInfo.container = container;
					items.add(appWidgetInfo);
					break;
				}
			}
		} catch (Exception e) {
			Launcher.addDumpLog(TAG, "Desktop items loading interrupted", e, true);

		} finally {
			if (c != null) {
				c.close();
			}
		}
		int len = removeApps.size();
		for(int i=0;i<len;i++){
			AppInfo info = removeApps.get(i);
			Intent intent = info.getIntent();
			String pn ;
			if(intent==null){
				ComponentName componentName = info.componentName;
				if(componentName==null){
					continue;
				}else{
					pn = componentName.getPackageName();
				}
			}else{
				pn = intent.getComponent().getPackageName();
			}
			if(pn==null){
				continue;
			}
			for (ItemInfo item : items) {
				if(item instanceof ShortcutInfo){
					ShortcutInfo shortcutinfo = (ShortcutInfo) item;
					String packageName = shortcutinfo.intent.getComponent().getPackageName();
					if(pn.equals(packageName)){
						removeIds.add(shortcutinfo.id);
						Log.v("lmjssjj", "rm double launcher uninstall pn icon according pn:"+pn);
						continue;
					}
				}else if(item instanceof LauncherAppWidgetInfo){
					LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
					String packageName = launcherAppWidgetInfo.providerName.getPackageName();
					if(pn.equals(packageName)){
						removeIds.add(launcherAppWidgetInfo.id);
						Log.v("lmjssjj", "rm double launcher uninstall pn widget according pn:"+pn);
						continue;
					}
				}
			}
		}
		if(!removeIds.isEmpty()){
			for (int i = 0; i < removeIds.size(); i++) {
				Uri deleteUri = LauncherSettings.Favorites.getContentUri(false, removeIds.get(i));
				Log.v("lmjssjj", "rm double launcher uninstall pn according id from db:"+removeIds.get(i));
				Log.v("lmjssjj", deleteUri.toString());
				contentResolver.delete(deleteUri, null, null);
			}
		}

	}

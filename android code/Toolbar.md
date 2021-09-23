# title

```
toolbar.setTitle()需要在调用setSupportActionBar(toolbar)方法之前设置
```

# 一般

```xml

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:titleTextColor="@android:color/white" />
```

```kotlin
 fun initToolbar() {
        toolbar?.title = "电量信息"
        toolbar.setTitleTextColor(Color.WHITE)
        val actionBar: ActionBar? = supportActionBar
        setSupportActionBar(toolbar)        
//            actionBar.setDisplayHomeAsUpEnabled(true) //标题前面按键控制返回键
//            actionBar.setDisplayShowTitleEnabled(false) //标题显示控制
        
    }
```

```java
protected void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.v("lmjssjj", "back");
                finish();
                break;
        }
        return true;
    }
```

# fragment使用控制menu

```java
//fragment
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
}

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sort = menu.findItem(R.id.sort);
        MenuItem showSp = menu.findItem(R.id.export_app_info);
        if (sort != null) sort.setVisible(false);
        if (showSp != null) showSp.setVisible(false);
}
```


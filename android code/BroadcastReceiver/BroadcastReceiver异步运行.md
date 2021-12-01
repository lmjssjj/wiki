异步处理

```java
	private static Handler mAsyncHandler;
    static {
        HandlerThread thr = new HandlerThread("async");
        thr.start();
        mAsyncHandler = new Handler(thr.getLooper());
    }
	@Override
    public void onReceive(final Context context, final Intent intent) {
        final PendingResult result = goAsync();
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                onReceiveAsync(context, intent);
                result.finish();
            }
        };
        mAsyncHandler.post(worker);
    }

    void onReceiveAsync(Context context, Intent intent) {}
```


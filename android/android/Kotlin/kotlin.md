```kotlin
interface InsertCallback {

    companion object{
        const val FAILED = 0;
        const val SUCCESS = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(FAILED,SUCCESS)
    annotation class InsertState{}

    fun callback(@InsertState state: Int, apnName: String);

}
```


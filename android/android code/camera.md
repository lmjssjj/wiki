```java
public static boolean isAutoFocusContinousPictureSupported(
        Camera.Parameters params, Context context) {
    boolean isAutoFocusContinousPictureSupported = false;
    if (context.getPackageManager().hasSystemFeature(
            PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
        isAutoFocusContinousPictureSupported = isCameraFeatureSupported(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                params.getSupportedFocusModes());
    }
    return isAutoFocusContinousPictureSupported;
}

private static boolean isCameraFeatureSupported(String value,
                                                List<String> supported) {
    return supported == null ? false : supported.contains(value);
}
```
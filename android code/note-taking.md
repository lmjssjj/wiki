```
截图录屏相关
MediaProjectionManager,MediaProjection,VirtualDisplay,MediaCodec
```

```java
//view 颜色过滤相关
Window window = activity.getWindow();
if (window == null) {
    return;
}
View view = window.getDecorView();
Paint paint = new Paint();
ColorMatrix cm = new ColorMatrix();
// 关键起作用的代码，Saturation，翻译成中文就是饱和度的意思。
// 官方文档说明：A value of 0 maps the color to gray-scale. 1 is identity.
// 原来如此，666
cm.setSaturation(0f);
paint.setColorFilter(new ColorMatrixColorFilter(cm));
view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);


// 遍历查找ImageView，对其设置逆矩阵
int childCount = parent.getChildCount();
for (int i = 0; i < childCount; i++) {
    final View childView = parent.getChildAt(i);
    if (childView instanceof ViewGroup) {
        takeOffColor((ViewGroup) childView);
    } else if (childView instanceof ImageView) {
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        childView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }
}
```


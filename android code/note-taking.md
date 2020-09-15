# 截屏录屏

```
截图录屏相关
MediaProjectionManager,MediaProjection,VirtualDisplay,MediaCodec
```

# 对view的颜色值过滤

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

# textview跑马灯

```
1、XML配置

 Textview 一定要用 android:singleLine="true"。因为这个方法过时了，就用  android:lines="1".怎么搞都没用。

<TextView
    android:id="@+id/title"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:ellipsize="marquee"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:marqueeRepeatLimit="marquee_forever"
    android:scrollHorizontally="true"
    android:lines="1"
    android:textSize="12dp" />
正确配置如下：
<TextView
    android:id="@+id/title"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:ellipsize="marquee"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:marqueeRepeatLimit="marquee_forever"
    android:singleLine="true"
    android:scrollHorizontally="true"
    android:textSize="12dp" />
2、在adapter 里面 TextView 设置    holder.titleTv.setSelected(true);
@Override
public void onBindViewHolder(MainListHolder holder, int position) {
    Uri uri = Uri.parse(Constant.IMG_BASEURL + mDatas.get(position).getMainPic());
    Log.i("MainListAdapter", uri.toString());
    holder.draweeView.setImageURI(uri);
    holder.titleTv.setText(mDatas.get(position).getTitle());
    holder.titleTv.setSelected(true);
    holder.cityDsstrictTv.setText(mDatas.get(position).getCityTitle() + " [" +  mDatas.get(position).getDistrictTitle()+"]");
    holder.priceTv.setText(mDatas.get(position).getPrice() + "");
}
```


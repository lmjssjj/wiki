//自定义view属性 用于属性动画
 private static final Property<View, Integer> VIEW_LAYOUT_HEIGHT_PROPERTY =
            new Property<View, Integer>(Integer.class, "height") {
                @Override
                public void set(View view, Integer height) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                            view.getLayoutParams();
                    params.height = height;
                    view.setLayoutParams(params);
                }

                @Override
                public Integer get(View view) {
                    return view.getLayoutParams().height;
                }
            };

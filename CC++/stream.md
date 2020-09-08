## ostream

```c++
ios_base::fmtflags initial;
initail = cout.setf(ios_base::fixed);//save initial formatting state
.
.
cout.setf(initial);//还原初始化状态

```



```c++
//设置各种格式化状态
//setf(ios_base::fixed) 将对象置于使用定点表示法的模式
//setf(ios_base::showpoint)将对象置于显示小数点的模式
setf();
```



```C++
//指定显示多少位小数
percision();//设置精度
```



```
//设置下一次输出操作使用的字段宽度
width();
```


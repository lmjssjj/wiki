# printf()

```
占位符：
  d 有符号10进制整数
  i 有符号10进制整数
  o 无符号8进制整数
  u 无符号10进制整数
  x 无符号的16进制数字，并以小写abcdef表示
  X 无符号的16进制数字，并以大写ABCDEF表示
  f 浮点数(%1.2f表明，四舍五入为两位小数输出)
  E/e 用科学表示格式的浮点数
  g 使用%f和%e表示中的总的位数表示最短的来表示浮点数 G 同g格式，但表示为指数
  c 单个字符
  s 字符串
  S wchar_t字符（宽字符）类型字符串
  % 显示百分号本身
  zd转换说明匹配sizeof的返回类型(strlen()同样适用)
```

![](image\printf.png)

![](image\printf2.png)

![](image\printf3.png)

# scanf()

```
scanf()把输入的字符串转换成整数、浮点数、字符或字符串
每次读取一个字符，跳过所有的空白字符，直至遇到第1个非空白字符才开始读取。
在遇到第1个空白(空格、制表符或换行符)时就不再读取输入

	假设scanf()根据一个%d转换说明读取一个整数。scanf()函数每次读取一个字符，跳过所有的空白字符，直至遇到第Ⅰ个非空白字符才开始读取。因为要读取整数，所以 scanf ()希望发现一个数字字符或者一个符号（+或-)。如果找到一个数字或符号，它便保存该字符，并读取下一个字符。如果下一个字符是数字，它便保存该数字并读取下一个字符。scanf()不断地读取和保存字符,直至遇到非数字字符。如果遇到一个非数字字符，它便认为读到了整数的末尾。然后, scanf()把非数字字符放回输入。这意味着程序在下一次读取输入时，首先读到的是上一次读取丢弃的非数字字符。最后，scanf()计算已读取数字（可能还有符号）相应的数值，并将计算后的值放入指定的变量中。

```

![](image\scanf1.png)

![](image\scanf2.png)

![](image\scanf2.1.png)

# getchar()

```c
只处理字符
从输入队列中返回下一个字符
char get_first (void){
	int ch;
	ch = getchar ( ) ;
	while (getchar () != ' ln' )
		continue;
	return ch;
}

int get_int (void){
	int input;
    char ch;
	while (scanf ("%d",&input) != 1){	
		while ((ch = getchar ())!= '/n')
			putchar (ch) ; //处理错误输出
		printf (" is not an integer . \nPlease enter an " );
    	printf ( "integer value,such as 25,-178,or 3: ");
    }
	return input;
}


```

# putchar()

```
只处理字符
打印它的参数
```

# getc()

```
int getc(FILE *stream)//从指定的流 stream 获取下一个字符（一个无符号字符），并把位置标识符往前移动。
eg:
	c = getc(stdin);
```

# putc()

```
int putc(int char, FILE *stream)//把参数 char 指定的字符（一个无符号字符）写入到指定的流 stream 中，并把位置标识符往前移动。
eg:
	fp = fopen("file.txt", "w");
	putc(ch, fp);
```

# puts()

```
puts ()函数只显示字符串，而且自动在显示的字符串末尾加上换行符。
```


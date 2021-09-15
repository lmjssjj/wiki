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
	读取字符串时，scanf ()和转换说明%s 只能读取一个单词。
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

# gets()

```
	gets ()函数简单易用，它读取整行输入，直至遇到换行符，然后丢弃换行符，储存其余字符，并在这些字符的末尾添加一个空字符使其成为一个C字符串。它经常和puts ( )函数配对使用，该函数用于显示字符串，并在末尾添加换行符。
	gets()函数只知道数组的开始处，并不知道数组中有多少个元素。
如果输入的字符串过长，会导致缓冲区溢出（buffer overflow)，即多余的字符超出了指定的目标空间。
```

# puts()

```
puts ()函数只显示字符串，而且自动在显示的字符串末尾加上换行符。
```

# fgets()

```
	fgets ()函数的第2个参数指明了读入字符的最大数量。如果该参数的值是n，那么fgets()将读入n-1个字符，或者读到遇到的第一个换行符为止。
	如果fgets ()读到一个换行符，会把它储存在字符串中。这点与gets ()不同，gets ()会丢弃换行符。
	fgets()函数的第3个参数指明要读入的文件。如果读入从键盘输入的数据，则以stdin(标准输入）作为参数，该标识符定义在stdio.h中。

	fgets (words,STLEN,stdin) ;


#include <stdio.h>
#define STLEN 10int main (void){
    char words [ STLEN];
    puts ( "Enter strings (empty line to quit) : ") ;
    while (fgets(words，STLEN,stdin) != NULL && words [0] != '\n ')
        fputs (words, stdout) ;
    puts ( "Done. " ) ;
    return 0;
}

#include <stdio.h>
#define STLEN 10
int main (void){
    char words [ STLEN];int i ;
    int i;
    puts ( "Enter strings (empty line to quit) : ");
    while (fgets (words,STLEN,stdin) != NULL && words [0] != '\n ' ){
        i = 0 ;
        while (words [i] != ' \n' & & words [ i]!= '\0')
        	i++;
        if (words [i] == '\n ' )
            words [i] = '\0 ';
            else //l如果word[i] == '\0'则执行这部分代码
        while (getchar ( ) != '\n ')
        	continue;
        puts (words) ;
    }
    puts ( "done " ) ;
    return 0 ;
}
```

# fputs()

```
	puts ()类似）配对使用，除非该函数不在字符串末尾添加换行符。fputs()函数的第2个参数指明它要写入的文件。如果要显示在计算机显示器上，应使用stdout(标准输出）作为该参数。
	
	fputs (words, stdout) ;

```

# gets_s()

```
gets_s (words,STLEN);
gets_s ()与fgets()的区别如下。
gets_s ()只从标准输入中读取数据，所以不需要第3个参数。如果gets_s ( )读到换行符，会丢弃它而不是储存它。
如果gets_s()读到最大字符数都没有读到换行符，会执行以下几步。首先把目标数组中的首字符设置为空字符，读取并丢弃随后的输入直至读到换行符或文件结尾，然后返回空指针。接着，调用依赖实现的“处理函数”(或你选择的其他函数)，可能会中止或退出程序。

```

# s_gets()  自定义

```
	如果字符串中出现换行符，就用空字符替换它;如果字符串中出现空字符，就丢弃该输入行的其余字符，然后返回与fgets ()相同的值。

```

```
char * s_gets (char * st, int n){
	char * ret_val;
	char * find;
	ret_val = fgets (st, n, stdin) ;
	if (ret_val){
		find = strchr(st, ' \n ' ) ;//查找换行符
		if( find)//如果地址不是NULL，
			*find = ' \0 ';//在此处放置一个空字符
		else
			while (getchar () != ' \n ' )
				continue;//处理输入行中剩余的字符
	}
	return ret_val;
}

```


## fopen()

```
stdio.h
fopen("打开文件的名称"，"打开文件的模式");
fopen()将返回文件指针( file pointer )，

FILE *f = fopen()
```

![](image\打开文件模式.png)

## fclose()

```
fclose(fp)函数关闭fp 指定的文件，必要时刷新缓冲区。对于较正式的程序，应该检查是否成功关闭文件。如果成功关闭，fclose ()函数返回0，否则返回EOF
```

![](image\标准文件指针.png)

```c
int ch;
FILE *fp;
unsiged long count = 0;
if((fp = fopen("name","r")) == NULL){
    return;
}
while((ch = getc(fp))!=EOF){
    putc(ch,stdout);
}
fclose(fp)
```

## fseek()

```
fseek ()的第Ⅰ个参数是FILE指针，指向待查找的文件，fopen ()应该已打开该文件。
fseek()的第2个参数是偏移量（.offset)。该参数表示从起始点开始要移动的距离。该参数必须是一个long类型的值，可以为正（前移)、负（后移）或0（保持不动
fseek()的第3个参数是模式，该参数确定起始点。根据ANSI标准，在stdio.h头文件中规定了几个表示模式的明示常量(manifest constant)
```

```
fseek(fp，0L,SEEK_SET);//定位至文件开始处
fseek(fp,10L，SEEK_SET); //定位至文件中的第10个字节
fseek(fp,2L,SEEK_CUR);//从文件当前位置前移2个字节
fseek(fp,0L，SEEK_END) ;//定位至文件结尾
fseek(fp,-10L，SEEK_END); //从文件结尾处回退10个字节

如果一切正常，fseek ()的返回值为0;如果出现错误（如试图移动的距离超出文件的范围)，其返回值为-1。
```

![](image\文件起点模式.png)

## ftell()

```
ftell()函数的返回类型是long，它返回的是当前的位置。
ftell()通过返回距文件开始处的字节数来确定文件的位置。

```

```
fseek(fp,OL，SEEK_END) ;
把当前位置设置为距文件末尾О字节偏移量。也就是说，该语句把当前位置设置在文件结尾。
下一条语句:
last = ftell (fp) ;
把从文件开始处到文件结尾的字节数赋给last。
然后是一个for循环:
for (count = lL; count <= last; count++){
fseek (fp,-count,SEEK_END); /* go backward*/ch = getc(fp) ;

```

```
fseek( )和ftell()潜在的问题是，它们都把文件大小限制在 long类型能表示的范围内。也许20亿字节看起来相当大，但是随着存储设备的容量迅猛增长，文件也越来越大。鉴于此，ANSIC新增了两个处理较大文件的新定位函数: fgetpos ()和 fsetpos()。这两个函数不使用long类型的值表示位置
```

## fgetpos()

```
fpos_t (代表file position type，文件定位类型)。fpos_t类型不是基本类型
int fgetpos (FILE * restrict stream，fpos_t * restrict pos);
```

## fsetpos()

```
int fsetpos (FILE*stream,const fpos_t *pos);
```

## ungetc()

```
int ungetc (int c,F工LE *fp)函数把c指定的字符放回输入流中。
```

## fflush()

```
int fflush ( FILE *fp) ;
调用fflush ()函数引起输出缓冲区中所有的未写入数据被发送到fp指定的输出文件。
```

## setvbuf()

```
setvbuf()函数的原型是:
int setvbuf(EILE * restrict fp,char * restrict buf, int mode,size_t size);
setvbuf()函数创建了一个供标准I/O函数替换使用的缓冲区。在打开文件后且未对流进行其他操作

指针 fp识别待处理的流，
buf 指向待使用的存储区。如果buf 的值不是NULL，则必须创建一个缓冲区。例如，声明一个内含1024个字符的数组，并传递该数组的地址。然而，如果把NULL作为buf的值，该函数会为自己分配一个缓冲区。
变量size告诉setvbuf()数组的大小(size_t是一种派生的整数类型).
mode的选择如下:_IOFBF表示完全缓冲(在缓冲区满时刷新);_IOLBF表示行缓冲（在缓冲区满时或写入一个换行符时);_IONBF 表示无缓冲。
如果操作成功，函数返回0，否则返回一个非零值。

```

# 二进制IO: fread ()和fwrite ( )

## fwrite ()

```
fwrite ()函数的原型如下:
size_t fwrite(const void * restrict ptr, size_t size,size_t nmemb,FILE * restrict fp)
fwrite ()函数把二进制数据写入文件。size_t是根据标准∈类型定义的类型，它是sizeof
符返回的类型，通常是unsigned int，但是实现可以远作使用共化午数据a的粉量。和其他函数一样，址。size表示待写入数据块的大小(以字节为单位)，nmemb表示待写入数据块的数量。和其他函数一样，
fp指定待写入的文件。例如，要保存一个大小为256字节的数据对象（如数组)，可以这样做:
char buffer [256];
fwrite (buffer, 256,1, fp);
以上调用把一块256字节的数据从buffer写入文件。

double earnings [10] ;
fwrite(earnings,sizeof(double) , 10, fp);
```

## fread ()

```
size_t fread ()函数的原型如下:
size_t fread(void * restrict ptr，size_t size，size_t nmemb,FILE * restrict fp)
fread ( )函数接受的参数和fwrite ()函数相同。
在fread ()函数中，ptr是待读取文件数据在内存中的地址，fp指定待读取的文件。
该函数用于读取被fwrite ()写入文件的数据。例如，要恢复上例中保存的内含10个double类型值的数组，可以这样做:
double earnings [ 10] ;
fread(earnings,sizeof (double), 10，fp) ;
该调用把10个double大小的值拷贝进earnings数组中。
fread ()函数返回成功读取项的数量。
正常情况下，该返回值就是nmemb，但如果出现读取错误或读到文件结尾，该返回值就会比nmemb 小。

```

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define BUFSIZE 4096
#define SLEN 81
void append(FILE* Source, FILE* dest); 
char* s_gets(char* st, int n);
int main(void) {
	FILE* fa, * fs; //fa指向目标文件，fs指向源文件
	int files = 0;// 附加的文件数量
	char file_app[SLEN]; // 目标文件名
	char file_src[SLEN]; // 源文件名
	int ch;

	puts("Enter name of destination file : "); 
	s_gets(file_app, SLEN);

	if ((fa = fopen(file_app, "a+")) == NULL) {
		fprintf(stderr, "Can 't open %s n", file_app);
		exit(EXIT_FAILURE);
	}

	if (setvbuf(fa, NULL, _IOFBF, BUFSIZE) != 0) {
		fputs("can't create utput buffer\n", stderr); 
		exit(EXIT_FAILURE);
	}
	puts("Enter name of first source file (empty line to quit): ");
	while (s_gets(file_src, SLEN) && file_src[0] != '\0 ') {
		if (strcmp(file_src, file_app) == 0) {
			fputs("can't append file to itself ln", stderr);
		}
		else if ((fs = fopen(file_src, "r")) == NULL) {
			fprintf(stderr, "Can't open %s \n", file_src);
		}
		else {
			if (setvbuf(fs, NULL, _IOFBF, BUFSIZE) != 0) {
				fputs("Can't create input buffer\n", stderr);
				continue;
			}
			append(fs, fa);
			if (ferror(fs) != 0)
				printf(stderr, "Error in reading file %s. \n",
					file_src);
			if (ferror(fa) != 0)
				fprintf(stderr, "Error in writing file %s.\n",
					file_app);
			fclose(fs);
			files++;
			printf("File %s appended . ln", file_src);
			puts("Next file ( empty line to quit) : ");
		}
	}
	printf("Done appending. %d files appended.\n", files); 
	rewind(fa);
	printf("%s contents : \n", file_app);
	while ((ch = getc(fa)) != EOF)
		putchar(ch);
	puts("Done displaying. "); fclose(fa);
	return 0;
}

void append(FILE* source, FILE* dest){
	size_t bytes;
	static char temp[BUFSIZE]; // 只分配一次
	while ((bytes = fread(temp, sizeof(char), BUFSIZE, source)) > 0)
		fwrite(temp, sizeof(char), bytes, dest);
}

char* s_gets(char* st, int n) {
	char* ret_val;
	char* find;
	ret_val = fgets(st, n, stdin);
	if (ret_val) {
		find = strchr(st, ' \n'); // 查找换行符
		if (find)//l如果地址不是NULL，
			*find = '\0';//在此处放置一个空字符
		else
			while (getchar() != '\n ')
				continue;
	}
	return ret_val;

}
```

```c
#include <stdio.h>
#include <stdlib.h>
#define ARSIZE 1000
int main() {
	double numbers[ARSIZE];
	double value;
	const char* file = "numbers.dat";
	int i;
	long pos;
	FILE* iofile;
	//创建一组 double类型的值
	for (i = 0; i < ARSIZE; i++)
		numbers[i] = 100.0 * i + 1.0 / (i + 1);//尝试打开文件
	if ((iofile = fopen(file, "wb")) == NULL) {
		fprintf(stderr, "Could not open %s for output.\n", file);
		exit(EXIT_FAILURE);
	}
	//以二进制格式把数组写入文件
	fwrite(numbers, sizeof(double), ARSIZE, iofile);
	fclose(iofile);

	if ((iofile = fopen(file, "rb")) == NULL) {
		fprintf(stderr,
			"could not open is for random access. \n", file);
		exit(EXIT_FAILURE);
	}
	//从文件中读取选定的内容
	printf("Enter an index in the range 0-8d.\n",ARSIZE - 1);
	while (scanf("%d", &i) == 1 && i >= 0 && i < ARSIZE) {
		pos = (long)i * sizeof(double); // 计算编移量
		fseek(iofile, pos, SEEK_SET); // 定位到此处
		fread(&value, sizeof(double), 1, iofile);
		printf("The value there is %f. \n", value);
		printf("Next index (out of range to quit) : \n");
		//完成
	}
	fclose(iofile);
	puts("Bye ! ");
	return 0;
}

```


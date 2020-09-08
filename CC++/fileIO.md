# File IO

## fstream

### ofstream

```c++
#include<fstream>
using namespace std;
char str[] = "strings";
double d = 10.02;
int i = 2;

ofstream outfile;

outfile.open("lmjssjj.txt");//文件不存在就创建，存在就覆盖
outfile.precision(2);
outfile.setf(ios_base::showpoint);
outfile << str << endl;
outfile << d << endl;
outfile << i << endl;
outfile.close();


#include <iomanip>
outfile << setw(6) << setfill('0') << i << endl;//格式化输出
```

### ifstream

```c++
#include<fstream>
using namespace std;

ifstream infile;

infile.open("lmjssjj.txt");
if(!infile.is_open()){
    cout << "can't open file"
}
double value;
double sum =0.0;
int count = 0;

infile >> value;
while(infile.good()){	//没有错误发生
    ++count;
    sum +=value;
    infile >> value
}

if(infile.eof){	//文件结尾
    count << "end of file reached."
}eles if(infile.fail()){	//类型不匹配
    count << "input termianteed by data mismatch";
}else{
    count << "input terminated for unkown reason.";
}
if(count==0){
    count << "no data in file"
}else{
    ...
}

```


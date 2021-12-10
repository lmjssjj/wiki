# 工具

```
javah：生成头文件

```

# 打印日志

```
导入头文件
	#include <android/log.h>
		int __android_log_print(int prio, const char* tag, const char* fmt, ...)
定义宏简化使用
	#define  LOG_TAG    "native-lmjssjj"
	#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE/*log级别*/, LOG_TAG/*log tag*/, __VA_ARGS__/*日志打印*/)
	#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

编译设置
	android studio gradle
		 defaultConfig {
                ndk {
                    ldLibs "log"
                }
          }
	
	android.mk
		文件增加LOCAL_LDLIBS += -llog
		
使用
	 LOGI("info\n");
```

# 参数

## JNIEnv

```
JNIEnv 类型代表了java环境 通过JNIEnv* 指针,就可以对java端的代码进行操作.
	创建java类的对象,调用java对象的方法
	获取java对象的属性 等等.
```

## jobject

```
jobject obj 就是当前方法所在的类代表的对象.
```

# Java 与 C/C++数据类型匹配

```c
/* Primitive types that match up with Java equivalents. */
typedef uint8_t  jboolean; /* unsigned 8 bits */
typedef int8_t   jbyte;    /* signed 8 bits */
typedef uint16_t jchar;    /* unsigned 16 bits */
typedef int16_t  jshort;   /* signed 16 bits */
typedef int32_t  jint;     /* signed 32 bits */
typedef int64_t  jlong;    /* signed 64 bits */
typedef float    jfloat;   /* 32-bit IEEE 754 */
typedef double   jdouble;  /* 64-bit IEEE 754 */

/* "cardinal indices and sizes" */
typedef jint     jsize;
```


# AspectJ

```
	AOP是Aspect Oriented Programming的缩写，即面向切面编程
	AspectJ面向切面编程的框架，是对 Java 的扩展，而且完全兼容 java 。它定义了 AOP 语法，有一个专门的编译器用来生成遵守 Java 字节码编码规范的 Class 文件， 还支持原生的 Java，只需要加上 AspectJ 提供的注解即可。
```

### Aspect

```
Aspect（切面）：是切入点和通知（引介）的结合。
```



### Advice(通知)

```
	Advice（通知/增强）：拦截到 Joinpoint 之后需要做的事情。

	Advice就是我们插入的代码(注入到class文件中的代码)以何种方式插入，有Before还有After、Around。
	
	Advice 类型有 before、after 和 around，分别表示在目标方法执行之前、执行后和完全替代目标方法执行的代码。 除了在方法中注入代码，也可能会对代码做其他修改，比如在一个class中增加字段或者接口。
```

### Joint point（连接点）

```
	Joinpoint（连接点）：指那些被拦截到的点。
	Join Points是AspectJ中的一个关键概念。Join Points可以看作是程序运行时的一个执行点，比如：一个函数的调用可以看作是个Join Points
	程序中可能作为代码注入目标的特定的点，例如一个方法调用或者方法入口。
```

| Join Points           | 说明                             | 实例                                                         |
| --------------------- | -------------------------------- | ------------------------------------------------------------ |
| method call           | 函数调用                         | 比如调用Log.e()，这是一个个Join Point                        |
| method execution      | 函数执行                         | 比如Log.e()的执行内部，是一处Join Points。注意这里是函数内部 |
| constructor call      | 构造函数调用                     | 和method call 类似                                           |
| constructor execution | 构造函数执行                     | 和method execution 类似                                      |
| field get             | 获取某个变量                     | 比如读取DemoActivity.debug成员                               |
| field set             | 设置某个变量                     | 比如设置DemoActivity.debug成员                               |
| pre-initialization    | Object在构造函数中做的一些工作。 |                                                              |
| initialization        | Object在构造函数中做的工作。     |                                                              |
| static initialization | 类初始化                         | 比如类的static{}                                             |
| handler               | 异常处理                         | 比如try catch 中，对应catch内的执行                          |
| advice execution      | 这个是AspectJ 的内容             |                                                              |



### Pointcut（切入点）

```
	一个程序会有多个Join Points，即使同一个函数，也还分为call和execution类型的Join Points，但并不是所有的Join Points都是我们关心的，Pointcuts就是提供一种使得开发者能够选择自己需要的JoinPoints的方法。告诉代码注入工具，在何处注入一段特定代码的表达式。
	例如，在哪些 joint points 应用一个特定的 Advice。切入点可以选择唯一一个，比如执行某一个方法，也可以有多个选择，比如，标记了一个定义成@DebguTrace 的自定义注解的所有方法。
```

## 集成

常规的Gradle 配置方式

### 1

```
项目根目录的build.gradle中添加
classpath 'org.aspectj:aspectjtools:1.9.6'
```

### 2

```
app的build.gradle中添加
```

```
dependencies {
    ...
    implementation 'org.aspectj:aspectjrt:1.9.6'
}

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger
final def variants = project.android.applicationVariants

variants.all { variant ->
	// 注意这里控制debug下生效，可以自行控制是否生效
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return
    }

    JavaCompile javaCompile = variant.javaCompileProvider.get()
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.9",
                         //class的输出目录，作为aspectJ的输入，如 -inpath, build\intermediates\classes\debug，切面定义文件可以在源文件里定义
                         "-inpath", javaCompile.destinationDir.toString(),                

  						 //依赖的jar包，切面定义文件可以在包含在第三方依赖中
                         "-aspectpath", javaCompile.classpath.asPath,
                       
                         //输出class的目录
                         "-d", javaCompile.destinationDir.toString(),
                         
                         //依赖的jar包
                         "-classpath", javaCompile.classpath.asPath,
                         
                         //-bootclasspath, Android\Sdk\platforms\android-25\android.jar
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true)
        new Main().run(args, handler)
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break
            }
        }
    }
}

```

在 module 使用的话一样需要添加配置代码（略有不同）：

```
dependencies {
	...
    implementation 'org.aspectj:aspectjrt:1.9.6'

}

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger

android.libraryVariants.all{ variant ->
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return
    }

    JavaCompile javaCompile = variant.javaCompileProvider.get()
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.9",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true)
        new Main().run(args, handler)
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break
            }
        }
    }
}

```

## Demo(方法运行时间)

### 自定义注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionTimeTrace {
    String value();
}
```

### 在需要计算时间的方法上添加注解

```java

@ExecutionTimeTrace("显示信息")
public void showInfo(View view) {
	Log.v();
}
```

### 定义切面类

```java
@Aspect // 定义切面类
public class ExecutionTimeTraceAspect {
	//定义切面的规则
    //1、就再原来的应用中那些注解的地方放到当前切面进行处理
    //execution（注解名   注解用的地方）
    @Pointcut("execution(@com.lmjssjj.demo.annotation.ExecutionTimeTrace *  *(..))")
    public void methodAnnottatedWithExecutionTimeTrace() {
    }


    //2、对进入切面的内容如何处理
    //@Before 在切入点之前运行
//    @After("methodAnnottatedWithExecutionTimeTrace()")
    //@Around 在切入点前后都运行
    @Around("methodAnnottatedWithExecutionTimeTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String value = methodSignature.getMethod().getAnnotation(ExecutionTimeTrace.class).value();

        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        SystemClock.sleep(new Random().nextInt(2000));
        long duration = System.currentTimeMillis() - begin;
        Log.d("alan", String.format("%s功能：%s类的%s方法执行了，用时%d ms",
                value, className, methodName, duration));
        return result;
    }
}
```

## 动态权限申请

### https://github.com/crazyqiang/Aopermission
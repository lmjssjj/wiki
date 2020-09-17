# Dispatcher[Servlet](http://c.biancheng.net/servlet/)

```xml
<!-- 部署 DispatcherServlet -->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 表示容器再启动时立即加载servlet -->
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!-- 处理所有URL -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```

# 视图解析器（ViewResolver）

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver" >
    <!--视图名称前缀-->
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <!--视图名称后缀-->
    <property name="suffix" value=".jsp"/>
</bean>
```

```java
//使用
public class LoginController implements Controller {
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		return new ModelAndView("register");//"/WEB-INF/jsp/register.jsp"
	}
}
```

# 注解

## Controller 注解类型

```xml
<!-- 使用扫描机制扫描控制器类，控制器类都在controller包及其子包下 -->
<context:component-scan base-package="controller" />
```

```java
/**
* “@Controller”表示 IndexController 的实例是一个控制器
* @Controller相当于@Controller(@Controller) 或@Controller(value="@Controller")
*/
@Controller
public class IndexController {
    // 处理请求的方法
}
```

## RequestMapping 注解类型

#### 方法级别注解

```java
/**
* “@Controller”表示 IndexController 的实例是一个控制器
* @Controller相当于@Controller(@Controller) 或@Controller(value="@Controller")
*/
@Controller
public class IndexController {
    //http://localhost:8080/springMVCDemo/index/login
    @RequestMapping(value = "/index/login")
    public String login() {
        /**
         * login代表逻辑视图名称，需要根据Spring MVC配置
         * 文件中internalResourceViewResolver的前缀和后缀找到对应的物理视图
         */
        return "login";
    }
    //http://localhost:8080/springMVCDemo/index/register
    @RequestMapping(value = "/index/register")
    public String register() {
        return "register";
    }
}
```

#### 类级别注解

```java
@Controller
@RequestMapping("/index")
public class IndexController {
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    @RequestMapping("/register")
    public String register() {
        return "register";
    }
}
```

## 编写请求处理方法

#### 请求处理方法中常出现的参数类型

```java
@Controller
@RequestMapping("/index")
public class IndexController {
    @RequestMapping("/login")
    public String login(HttpSession session,HttpServletRequest request) {
        session.setAttribute("skey", "session范围的值");
        session.setAttribute("rkey", "request范围的值");
        return "login";
    }
     @RequestMapping("/register")
    public String register(Model model) {
        /*Model 类型是一个包含 Map 的 Spring 框架类型*/
        /*在视图中可以使用EL表达式${success}取出model中的值*/
        model.addAttribute("success", "注册成功");
        return "register";
    }
}
```

#### 请求处理方法常见的返回类型

最常见的返回类型就是代表逻辑视图名称的 String 类型，例如前面教程中的请求处理方法。除了 String 类型以外，还有 ModelAndView、Model、View 以及其他任意的 [Java](http://c.biancheng.net/java/) 类型。

## Spring MVC获取参数的几种常见方式

```xml
<!-- 使用扫描机制扫描控制器类，控制器类都在controller包及其子包下 -->
    <context:component-scan base-package="controller" />
    <mvc:annotation-driven />
    <!-- annotation-driven用于简化开发的配置，注解DefaultAnnotationHandlerMapping和AnnotationMethodHandlerAdapter -->
    <!-- 使用resources过滤掉不需要dispatcherservlet的资源（即静态资源，例如css、js、html、images）。
        在使用resources时必须使用annotation-driven，否则resources元素会阻止任意控制器被调用 -->
    <!-- 允许css目录下的所有文件可见 -->
    <mvc:resources location="/css/" mapping="/css/**" />
    <!-- 允许html目录下的所有文件可见 -->
    <mvc:resources location="/html/" mapping="/html/**" />
    <!-- 允许css目录下的所有文件可见 -->
    <mvc:resources location="/images/" mapping="/images/**" />
    <!-- 完成视图的对应 -->
    <!-- 对转向页面的路径解析。prefix：前缀， suffix：后缀 -->
    <bean
        class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp" />
    </bean>
```



### 通过实体 Bean 接收请求参数

```java
//创建bean
public class UserForm {
    private String uname; // 与请求参数名称相同
    private String upass;
    private String reupass;
    // 省略getter和setter方法
}
```

```java
@Controller
@RequestMapping("/index")
public class IndexController {
    //跳到登录页登录
    @RequestMapping("/login")
    public String login() {
        return "login"; // 跳转到/WEB-INF/jsp下的login.jsp
    }
    //跳到注册页
    @RequestMapping("/register")
    public String register() {
        return "register";
    }
}

```



```java
@Controller
@RequestMapping("/user")
public class UserController {
    // 得到一个用来记录日志的对象，这样在打印信息的时候能够标记打印的是哪个类的信息
    private static final Log logger = LogFactory.getLog(UserController.class);
    /**
     * 处理登录 使用UserForm对象(实体Bean) user接收注册页面提交的请求参数
     */
    @RequestMapping("/login")
    public String login(UserForm user, HttpSession session, Model model) {
        if ("zhangsan".equals(user.getUname())
                && "123456".equals(user.getUpass())) {
            session.setAttribute("u", user);
            logger.info("成功");
            return "main"; // 登录成功，跳转到 main.jsp
        } else {
            logger.info("失败");
            model.addAttribute("messageError", "用户名或密码错误");
            return "login";
        }
    }
    /**
     * 处理注册 使用UserForm对象(实体Bean) user接收注册页面提交的请求参数
     */
    @RequestMapping("/register")
    public String register(UserForm user, Model model) {
        if ("zhangsan".equals(user.getUname())
                && "123456".equals(user.getUpass())) {
            logger.info("成功");
            return "login"; // 注册成功，跳转到 login.jsp
        } else {
            logger.info("失败");
            // 在register.jsp页面上可以使用EL表达式取出model的uname值
            model.addAttribute("uname", user.getUname());
            return "register"; // 返回 register.jsp
        }
    }
}
```

### 通过处理方法的形参接收请求参数

```java
@RequestMapping("/register")
/**
* 通过形参接收请求参数，形参名称与请求参数名称完全相同
*/
public String register(String uname,String upass,Model model) {
    if ("zhangsan".equals(uname)
            && "123456".equals(upass)) {
        logger.info("成功");
        return "login"; // 注册成功，跳转到 login.jsp
    } else {
        logger.info("失败");
        // 在register.jsp页面上可以使用EL表达式取出model的uname值
        model.addAttribute("uname", uname);
        return "register"; // 返回 register.jsp
    }
}
```

### 通过 Http[Servlet](http://c.biancheng.net/servlet/)Request 接收请求参数

```java
@RequestMapping("/register")
/**
* 通过HttpServletRequest接收请求参数
*/
public String register(HttpServletRequest request,Model model) {
    String uname = request.getParameter("uname");
    String upass = request.getParameter("upass");
    if ("zhangsan".equals(uname)
            && "123456".equals(upass)) {
        logger.info("成功");
        return "login"; // 注册成功，跳转到 login.jsp
    } else {
        logger.info("失败");
        // 在register.jsp页面上可以使用EL表达式取出model的uname值
        model.addAttribute("uname", uname);
        return "register"; // 返回 register.jsp
    }
}
```

### 通过 @PathVariable 接收 URL 中的请求参数

在访问“http://localhost：8080/springMVCDemo02/user/register/zhangsan/123456”路径时，上述代码自动将 URL 中的模板变量 {uname} 和 {upass} 绑定到通过 @PathVariable 注解的同名参数上，即 uname=zhangsan、upass=123456。

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/user")
    // 必须节method属性
    /**
     * 通过@PathVariable获取URL的参数
     */
    public String register(@PathVariable String uname,@PathVariable String upass,Model model) {
        if ("zhangsan".equals(uname)
                && "123456".equals(upass)) {
            logger.info("成功");
            return "login"; // 注册成功，跳转到 login.jsp
        } else {
            // 在register.jsp页面上可以使用EL表达式取出model的uname值
            model.addAttribute("uname", uname);
            return "register"; // 返回 register.jsp
        }
    }
}
```

### 通过 @RequestParam 接收请求参数

通过 @RequestParam 接收请求参数适用于 get 和 post 提交请求方式，可以将“通过实体 Bean 接收请求参数”部分控制器类 UserController 中 register 方法的代码修改如下：

```java
@RequestMapping("/register")
/**
* 通过@RequestParam接收请求参数
*/
public String register(@RequestParam String uname,
    @RequestParam String upass, Model model) {
    if ("zhangsan".equals(uname) && "123456".equals(upass)) {
        logger.info("成功");
        return "login"; // 注册成功，跳转到 login.jsp
    } else {
        // 在register.jsp页面上可以使用EL表达式取出model的uname值
        model.addAttribute("uname", uname);
        return "register"; // 返回 register.jsp
    }
}
```

通过 @RequestParam 接收请求参数与“通过处理方法的形参接收请求参数”部分的区别如下：当请求参数与接收参数名不一致时，“通过处理方法的形参接收请求参数”不会报 404 错误，而“通过 @RequestParam 接收请求参数”会报 404 错误。

### 通过 @ModelAttribute 接收请求参数

当 @ModelAttribute 注解放在处理方法的形参上时，用于将多个请求参数封装到一个实体对象，从而简化数据绑定流程，而且自动暴露为模型数据，在视图页面展示时使用。

而“通过实体 Bean 接收请求参数”中只是将多个请求参数封装到一个实体对象，并不能暴露为模型数据（需要使用 model.addAttribute 语句才能暴露为模型数据，数据绑定与模型数据展示后面教程中会讲解）。

通过 @ModelAttribute 注解接收请求参数适用于 get 和 post 提交请求方式，可以将“通过实体 Bean 接收请求参数”中控制器类 UserController 中 register 方法的代码修改如下

```java
@RequestMapping("/register")
public String register(@ModelAttribute("user") UserForm user) {
    if ("zhangsan".equals(uname) && "123456".equals(upass)) {
        logger.info("成功");
        return "login"; // 注册成功，跳转到 login.jsp
    } else {
        logger.info("失败");
        // 使用@ModelAttribute("user")与model.addAttribute("user",user)的功能相同
        //register.jsp页面上可以使用EL表达式${user.uname}取出ModelAttribute的uname值
        return "register"; // 返回 register.jsp
    }
}
```

# Spring MVC的转发与重定向

```java
@RequestMapping("/register")
public String register() {
    return "register";  //转发到/WEB-INF/jsp/register.jsp
}
```

```java
@Controller
@RequestMapping("/index")
public class IndexController {
    @RequestMapping("/login")
    public String login() {
        //转发到一个请求方法（同一个控制器类可以省略/index/）
        return "forward:/index/isLogin";
    }
    @RequestMapping("/isLogin")
    public String isLogin() {
        //重定向到一个请求方法
        return "redirect:/index/isRegister";
    }
    @RequestMapping("/isRegister")
    public String isRegister() {
        //转发到一个视图
        return "register";
    }
}
```

在 Spring MVC 框架中，不管是重定向或转发，都需要符合视图解析器的配置，如果直接转发到一个不需要 Dispatcher[Servlet](http://c.biancheng.net/servlet/) 的资源，例如：

```java
return "forward:/html/my.html";
```

则需要使用 mvc：resources 配置：

```xml
<mvc:resources location="/html/" mapping="/html/**" />
```

# Spring MVC应用@Autowired和@Service进行依赖注入

```java
package service;
import pojo.UserForm;
public interface UserService {
    boolean login(UserForm user);
    boolean register(UserForm user);
}
```

```java
//注解为一个服务
@Service
public class UserServiceImpl implements UserService {

    public boolean login(UserForm user) {
        if ("zhangsan".equals(user.getUname())
                && "123456".equals(user.getUpass())) {
            return true;
        }
        return false;
    }

    public boolean register(UserForm user) {
        if ("zhangsan".equals(user.getUname())
                && "123456".equals(user.getUpass())) {
            return true;
        }
        return false;
    }
}
```

```xml
<context:component-scan base-package="service" />
```

```java
@Controller
@RequestMapping("/user")
public class UserController {
    // 得到一个用来记录日志的对象，这样在打印信息的时候能够标记打印的是哪个类的信息
    private static final Log logger = LogFactory.getLog(UserController.class);
    // 将服务依赖注入到属性userService
    @Autowired
    public UserService userService;
    /**
     * 处理登录
     */
    @RequestMapping("/login")
    public String login(UserForm user, HttpSession session, Model model) {
        if (userService.login(user)) {
            session.setAttribute("u", user);
            logger.info("成功");
            return "main"; // 登录成功，跳转到 main.jsp
        } else {
            logger.info("失败");
            model.addAttribute("messageError", "用户名或密码错误");
            return "login";
        }
    }
    /**
     * 处理注册
     */
    @RequestMapping("/register")
    public String register(@ModelAttribute("user") UserForm user) {
        if (userService.register(user)) {
            logger.info("成功");
            return "login"; // 注册成功，跳转到 login.jsp
        } else {
            logger.info("失败");
            // 使用@ModelAttribute("user")与model.addAttribute("user",user)的功能相同
            // 在register.jsp页面上可以使用EL表达式${user.uname}取出ModelAttribute的uname值
            return "register"; // 返回register.jsp
        }
    }
}
```

# Spring MVC中@ModelAttribute注解的使用

#### 绑定请求参数到实体对象（表单的命令对象）

```java
@RequestMapping("/register")
public String register(@ModelAttribute("user") UserForm user) {
    if ("zhangsan".equals(uname) && "123456".equals(upass)) {
        logger.info("成功");
        return "login";
    } else {
        logger.info("失败");
        return "register";
}
```

在上述代码中“@ModelAttribute（"user"）UserForm user”语句的功能有两个：

- 将请求参数的输入封装到 user 对象中。
- 创建 UserForm 实例。


以“user”为键值存储在 Model 对象中，和“model.addAttribute（"user"，user）”语句的功能一样。如果没有指定键值，即“@ModelAttribute UserForm user”，那么在创建 UserForm 实例时以“userForm”为键值存储在 Model 对象中，和“model.addAtttribute（"userForm", user）”语句的功能一样。

#### 注解一个非请求处理方法

被 @ModelAttribute 注解的方法将在每次调用该控制器类的请求处理方法前被调用。这种特性可以用来控制登录权限，当然控制登录权限的方法有很多，例如拦截器、过滤器等。

使用该特性控制登录权限，创建 BaseController，代码如下所示：

```java
public class BaseController {
    @ModelAttribute
    public void isLogin(HttpSession session) throws Exception {
        if (session.getAttribute("user") == null) {
            throw new Exception("没有权限");
        }
    }
}
```

创建 ModelAttributeController ，代码如下所示：

```java
@RequestMapping("/admin")
public class ModelAttributeController {
    @RequestMapping("/add")
    public String add() {
        return "addSuccess";
    }
    @RequestMapping("/update")
    public String update() {
        return "updateSuccess";
    }
    @RequestMapping("/delete")
    public String delete() {
        return "deleteSuccess";
    }
}
```

在上述 ModelAttributeController 类中的 add、update、delete 请求处理方法执行时，首先执行父类 BaseController 中的 isLogin 方法判断登录权限，可以通过地址“http://localhost:8080/springMVCDemo/admin/add”测试登录权限。

# Spring MVC Converter（类型转换器）

将请求参数转换成值对象类中各属性对应的数据类型

### 内置的类型转换器

| 名称                           | 作用                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| StringToBooleanConverter       | String 到 boolean 类型转换                                   |
| ObjectToStringConverter        | Object 到 String 转换，调用 toString 方法转换                |
| StringToNumberConverterFactory | String 到数字转换（例如 Integer、Long 等）                   |
| NumberToNumberConverterFactory | 数字子类型（基本类型）到数字类型（包装类型）转换             |
| StringToCharacterConverter     | String 到 Character 转换，取字符串中的第一个字符             |
| NumberToCharacterConverter     | 数字子类型到 Character 转换                                  |
| CharacterToNumberFactory       | Character 到数字子类型转换                                   |
| StringToEnumConverterFactory   | String 到枚举类型转换，通过 Enum.valueOf 将字符串转换为需要的枚举类型 |
| EnumToStringConverter          | 枚举类型到 String 转换，返回枚举对象的 name 值               |
| StringToLocaleConverter        | String 到 java.util.Locale 转换                              |
| PropertiesToStringConverter    | java.util.Properties 到 String 转换，默认通过 ISO-8859-1 解码 |
| StringToPropertiesConverter    | String 到 java.util.Properties 转换，默认使用 ISO-8859-1 编码 |

### 集合、数组相关转换器

| 名称                            | 作用                                                         |
| ------------------------------- | ------------------------------------------------------------ |
| ArrayToCollectionConverter      | 任意数组到任意集合（List、Set）转换                          |
| CollectionToArrayConverter      | 任意集合到任意数组转换                                       |
| ArrayToArrayConverter           | 任意数组到任意数组转换                                       |
| CollectionToCollectionConverter | 集合之间的类型转换                                           |
| MapToMapConverter               | Map之间的类型转换                                            |
| ArrayToStringConverter          | 任意数组到 String 转换                                       |
| StringToArrayConverter          | 字符串到数组的转换，默认通过“，”分割，且去除字符串两边的空格（trim） |
| ArrayToObjectConverter          | 任意数组到 Object 的转换，如果目标类型和源类型兼容，直接返回源对象；否则返回数组的第一个元素并进行类型转换 |
| ObjectToArrayConverter          | Object 到单元素数组转换                                      |
| CollectionToStringConverter     | 任意集合（List、Set）到 String 转换                          |
| StringToCollectionConverter     | String 到集合（List、Set）转换，默认通过“，”分割，且去除字符串两边的空格（trim） |
| CollectionToObjectConverter     | 任意集合到任意 Object 的转换，如果目标类型和源类型兼容，直接返回源对象；否则返回集合的第一个元素并进行类型转换 |
| ObjectToCollectionConverter     | Object 到单元素集合的类型转换                                |

类型转换是在视图与控制器相互传递数据时发生的。Spring MVC 框架对于基本类型（例如 int、long、float、double、boolean 以及 char 等）已经做好了基本类型转换。

例如，对于 表单的提交请求，可以由以下处理方法来接收请求参数并处理：

表单提交三个数据：goodsname,goodsprice,goodsnumber;

```java

@Controller
public class Goodsontroller {
    @RequestMapping("/addGoods")
    public String add(String goodsname, double goodsprice, int goodsnumber) {
        double total = goodsprice * goodsnumber;
        System.out.println(total);
        return "success";
    }
}
```

### 自定义类型转换器

当 Spring MVC 框架内置的类型转换器不能满足需求时，开发者可以开发自己的类型转换器。

当输入“apple，10.58，200”时表示在程序中自动创建一个 new Goods，并将“apple”值自动赋给 goodsname 属性，将“10.58”值自动赋给 goodsprice 属性，将“200”值自动赋给 goodsnumber 属性。

```jsp
<form action="${pageContext.request.contextPath}/my/converter" method= "post">
    请输入商品信息（格式为apple, 10.58,200）:
    <input type="text" name="goods" /><br>
    <input type="submit" value="提交" />
</form>
```

如果想实现上述应用，需要做以下 5 件事：

- 创建实体类。
- 创建控制器类。
- 创建自定义类型转换器类。
- 注册类型转换器。
- 创建相关视图。


按照上述步骤采用自定义类型转换器完成需求。

#### 创建实体类

```java
public class GoodsModel {
    private String goodsname;
    private double goodsprice;
    private int goodsnumber;
    // 此处省略了setter和getter方法
}
```

#### 创建控制器类

```java
@Controller
@RequestMapping("/my")
public class ConverterController {
    @RequestMapping("/converter")
    /*
     * 使用@RequestParam
     * ("goods")接收请求参数，然后调用自定义类型转换器GoodsConverter将字符串值转换为GoodsModel的对象gm
     */
    public String myConverter(@RequestParam("goods") GoodsModel gm, Model model) {
        model.addAttribute("goods", gm);
        return "showGoods";
    }
}
```

#### 创建自定义类型转换器类

```java
public class GoodsConverter implements Converter<String, GoodsModel> {
    public GoodsModel convert(String source) {
        // 创建一个Goods实例
        GoodsModel goods = new GoodsModel();
        // 以“，”分隔
        String stringvalues[] = source.split(",");
        if (stringvalues != null && stringvalues.length == 3) {
            // 为Goods实例赋值
            goods.setGoodsname(stringvalues[0]);
            goods.setGoodsprice(Double.parseDouble(stringvalues[1]));
            goods.setGoodsnumber(Integer.parseInt(stringvalues[2]));
            return goods;
        } else {
            throw new IllegalArgumentException(String.format(
                    "类型转换失败， 需要格式'apple, 10.58,200 ',但格式是[% s ] ", source));
        }
    }
}
```

#### 注册类型转换器

 WEB-INF 目录下创建配置文件 springmvc-servlet.xml，并在配置文件中注册自定义类型转换器，配置文件代码如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:p="http://www.springframework.org/schema/p" xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <!-- 使用扫描机制扫描控制器类，控制器类都在controller包及其子包下 -->
    <context:component-scan base-package="controller" />
    <!--注册类型转换器GoodsConverter-->
    <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
        <property name="converters">
            <list>
                <bean class="converter.GoodsConverter"/>
            </list>
        </property>
    </bean>
    <bean
        class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
```

#### 创建相关视图

```html
<form action="${pageContext.request.contextPath}/my/converter" method= "post">
    请输入商品信息（格式为apple, 10.58,200）:
    <input type="text" name="goods" /><br>
    <input type="submit" value="提交" />
</form>
```

 /WEB-INF/jsp 目录下创建信息显示页面 showGoods.jsp，核心代码如下：

```html
<body>
    您创建的商品信息如下：
    <!-- 使用EL表达式取出model中的goods信息 -->
    商品名称为:${goods.goodsname }
    商品价格为:${goods.goodsprice }
    商品名称为:${goods.goodsnumber }
</body>
```

# Spring MVC Formatter（数据格式化）详解

[Spring MVC](http://c.biancheng.net/spring_mvc/) 框架的 Formatter<T> 与 Converter<S，T> 一样，也是一个可以将一种数据类型转换成另一种数据类型的接口。不同的是，Formatter<T> 的源数据类型必须是 String 类型，而 Converter<S，T> 的源数据类型是任意数据类型。

在 Web 应用中由 HTTP 发送的请求数据到控制器中都是以 String 类型获取，因此在 Web 应用中选择 Formatter<T> 比选择 Converter<S，T> 更加合理。

## 内置的格式化转换器

[Spring](http://c.biancheng.net/spring/) MVC 提供了几个内置的格式化转换器，具体如下。

- NumberFormatter：实现 Number 与 String 之间的解析与格式化。
- CurrencyFormatter：实现 Number 与 String 之间的解析与格式化（带货币符号）。
- PercentFormatter：实现 Number 与 String 之间的解析与格式化（带百分数符号）。
- DateFormatter：实现 Date 与 String 之间的解析与格式化。

## 自定义格式化转换器

自定义格式化转换器就是编写一个实现 org.springframework.format.Formatter 接口的 [Java](http://c.biancheng.net/java/) 类。该接口声明如下：

```java
public interface Formatter<T>
```

这里的 T 表示由字符串转换的目标数据类型。该接口有 parse 和 print 两个接口方法，自定义格式化转换器类必须覆盖它们。

```java
public T parse(String s,java.util.Locale locale)
public String print(T object,java.util.Locale locale)
```

parse 方法的功能是利用指定的 Locale 将一个 String 类型转换成目标类型，print 方法与之相反，用于返回目标对象的字符串表示。

下面通过具体应用 springMVCDemo04 讲解自定义格式化转换器的用法，springMVCDemo04 应用与 springMVCDemo03 应用具有相同的 JAR 包、web.xml。

应用的具体要求如下：

1）表单中输入信息来创建商品

2）控制器使用实体 bean 类 GoodsModelb 接收页面提交的请求参数，GoodsModelb 类的属性如下。

```java
private String goodsname;
private double goodsprice;
private int goodsnumber;
private Date goodsdate;
```

3）GoodsModelb 实体类接收请求参数时，商品名称、价格和数量使用内置的类型转换器完成转换；商品日期需要用自定义的格式化转换器完成。

4）用格式化转换器转换之后的数据显示在 showGoodsb.jsp 页面



- 创建实体类；
- 创建控制器类；
- 创建自定义格式化转换器类；
- 注册格式化转换器；
- 创建相关视图。


按照上述步骤采用自定义格式化转换器完成需求。



#### 1）创建实体类

```java
public class GoodsModel {
    private String goodsname;
    private double goodsprice;
    private int goodsnumber;
    private Date goodsdate;
    //省略setter和getter方法
}
```



#### 2）创建控制器类

```java
@Controller
public class FormatterController {
    @RequestMapping("/formatter")
    public String myConverter(GoodsModel gm, Model model) {
        model.addAttribute("goods", gm);
        return "showGoodsb";
    }
}
```



#### 3）创建自定义格式化转换器类

```java
public class MyFormatter implements Formatter<Date> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public String print(Date object, Locale arg1) {
        return dateFormat.format(object);
    }
    public Date parse(String source, Locale arg1) throws ParseException {
        return dateFormat.parse(source); // Formatter只能对字符串转换
    }
}
```



#### 4）注册格式化转换器

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:p="http://www.springframework.org/schema/p" xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <!-- 使用扫描机制扫描controller包 -->
    <context:component-scan base-package="controller" />
    <!--注册MyFormatter-->
    <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
        <property name="formatters">
            <list>
                <bean class="formatter.MyFormatter"/>
            </list>
        </property>
    </bean>
    <mvc:annotation-driven conversion-service="conversionService"/>
    <bean
        class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
```



#### 5）创建相关视图

```html
<form action="addGoods" method="post">
        <table border=1 bgcolor="lightblue" align="center">
            <tr>
                <td>商品名称：</td>
                <td><input class="textSize" type="text" name="goodsname" /></td>
            </tr>
            <tr>
                <td>商品价格：</td>
                <td><input class="textSize" type="text" name="goodsprice" /></td>
            </tr>
            <tr>
                <td>商品数量：</td>
                <td><input class="textSize" type="text" name="goodsnumber" /></td>
            </tr>
            <tr>
                <td>商品日期：</td>
                <td><input class="textSize" type="text" name="goodsdata" />（yyyy-MM-dd）</td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" value="提交" />
                </td>
            </tr>
        </tab1e>
    </form>
```

```html
<body>
    您创建的商品信息如下：
    <!-- 使用EL表达式取出Action类的属性goods的值 -->
    商品名称为：${goods.goodsname }<br/>
    商品价格为：${goods.goodsprice }<br/>
    商品名称为：${goods.goodsnumber }<br/>
    商品日期为：${goods.goodsdate}
</body>
```


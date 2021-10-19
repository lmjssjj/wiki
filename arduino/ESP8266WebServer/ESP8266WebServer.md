http://www.taichi-maker.com/homepage/iot-development/iot-dev-reference/esp8266-c-plus-plus-reference/esp8266webserver/

## 配置wifi

```c++
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

#ifndef STASSID
#define STASSID "your-ssid"
#define STAPSK  "your-password"
#endif

const char* ssid = STASSID;
const char* password = STAPSK;

//如果需要配置添加 静态地址、网关、子网掩码 begin
IPAddress local_IP(192, 168, 50, 102);
IPAddress gateway(192, 168, 50, 1);
IPAddress subnet(255, 255, 255, 0);
//end

void setup(void) {
  //初始化串口
  Serial.begin(115200);
  Serial.println("");

  //初始化网络
  //设置静态IP begin
  WiFi.config(local_IP, gateway, subnet);//设置静态IP
  //end
    
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
}
```

## 获取带访问参数

```
语法：
server.arg(Name)
server.arg(index)

参数
– Name
请求体中的参数名（参数类型: String）
– index
请求体中的参数序列号（参数类型: int）
返回值
指定参数的数值（类型：String）
```

```c++
//自定义返回请求体信息
void echo_args() {
  Serial.print("请求体参数a的值:"); Serial.println(server.arg("a"));
  Serial.print("请求体第2个参数的值:"); Serial.println(server.arg(2));
}
```

## API

### begin

服务器启动

```
server.begin()
```

### stop/close

停止服务器

```
server.stop()
server.close()
```

### on

可找到资源处理配置

```
语法
server.on(uri, uri_handler);
server.on(uri, method, uri_handler);

参数
– uri: HTTP请求客户端所请求的uri（参数类型:const String*）
– uri_handler: HTTP请求回调函数（参数类型:THandlerFunction）
– method: 此参数用于设置向客户端发送响应信息时所使用的HTTP方法。以下为可供选择的响应方法关键字。
     HTTP_ANY
     HTTP_GET
     HTTP_POST
     HTTP_PUT
     HTTP_PATCH
     HTTP_DELETE
     HTTP_OPTIONS
```

```c++
//初始化WebServer
server.on("/", homepage);
void homepage() {
  server.send(200, "text/plain", "test homepage!");
  Serial.println("用户访问了主页");
}
```

### onNotFound

未找到资源处理配置

```
语法
server.onNotFound(function)

参数
function – 处理无效地址请求的回调函数(类型: THandlerFunction)
```

```c++
server.onNotFound(handleNotFound);
// 设置处理404情况的函数'handleNotFound'
void handleNotFound() {                                       // 当浏览器请求的网络资源无法在服务器找到时，
  server.send(404, "text/plain", "404: Not found");   // NodeMCU将调用此函数。
}
```

### onFileUpload

文件上传处理配置

```
语法
server.onFileUpload(function)
参数
function
– 处理文件上传请求的回调函数(类型: THandlerFunction)
```

```

```

### addHandler

设置请求响应回调

```
语法
server.addHandler(requestHandler)

参数
requestHandler: RequestHandler对象

```


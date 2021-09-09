#### shell脚本之结构化命令if...then...fi

```shell
#1. if command;then command;fi    （如果if满足条件然后执行then后面的command）
#2.if command ... then ...else...fi     （如果if满足条件然后执行then后面的command，否则则执行else后面的command）
#3.if command ...then ...elif...then...多层elif...fi  （如果满足if判断条件则执行then后的command动作，如果if分别满足elif判断条件则执行elif的判断条件）

#3.if嵌套if
if command 

 then

　　command

else

　　if command

　　then

　　　　command

　　fi

fi
```

```shell
#最常用的，判断账户是否为root用户，如果不是root则退出
#!/bin/bash
if [ `whoami` != "root" ]
then
        echo "please sudo root user"
        exit
fi
```

```shell
#!/bin/bash
num1=10
if (( $num1 + 10 > 20 ))
then
    echo "1"
else
    echo "2"
fi
```

```shell

#if [ str1 = str2 ]　　　　　  当两个串有相同内容、长度时为真 
#if [ str1 != str2 ]　　　　　 当串str1和str2不等时为真 
#if [ -n str1 ]　　　　　　 当串的长度大于0时为真(串非空) 
#if [ -z str1 ]　　　　　　　 当串的长度为0时为真(空串) 
#if [ str1 ]　　　　　　　　 当串str1为非空时为真

#modified for begin
if [ -n "$nuu_build_number" ] ; then
  echo "ro.build.version=$nuu_build_number"
else
  echo "ro.build.version=$BUILD_NUMBER"
fi
# modified for end
```


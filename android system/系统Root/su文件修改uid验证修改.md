## `system/extras/su/su.cpp`

```cpp
int main(int argc, char** argv) {
    uid_t current_uid = getuid();
    if (current_uid != AID_ROOT && current_uid != AID_SHELL) error(1, 0, "not allowed");
    .
        .
        .
}
// 注释掉第83-84行
// uid_t current_uid = getuid();
// if (current_uid != AID_ROOT && current_uid != AID_SHELL) error(1, 0, "not allowed");
```

## `system/core/libcutils/fs_config.cpp`

```cpp
// the following files have enhanced capabilities and ARE included
// in user builds.
// 添加下面代码至212行处，注意标点符号不要漏掉
{ 06755, AID_ROOT,      AID_ROOT,      0, "system/bin/su" },
```


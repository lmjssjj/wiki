# MemoryFile匿名共享内存

```
frameworks/base/core/java/android/os/MemoryFile.java

在一个进程中创建一个共享内存。在Android应用层中，用MemoryFile描述一块共享内存，创建共享内存其实就是创建一MemoryFile对象。这一步非常简单，MemoryFile提供了相应的构造函数

public MemoryFile(String name, int length) throws IOException
eg:
mMemoryFile = new MemoryFile(null, MEMORY_SIZE);
mMemoryFile.writeBytes(buffer_write, 0, 0, MEMORY_SIZE);
buffer_read = new byte[buffer_write.length];
mMemoryFile.readBytes(buffer_read, 0, 0, MEMORY_SIZE);
```


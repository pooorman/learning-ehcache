## 使用堆外内存
```
// Unsafe f = Unsafe.getUnsafe();
Field f = Unsafe.class.getDeclaredField("theUnsafe");
f.setAccessible(true);
Unsafe us = (Unsafe) f.get(null);
```
申请内存

```
long us = f.allocateMemory(1024);
```
释放内存

```
us.reallocateMemory(addr, 1024);
```
从 java.nio 开始，ByteBuffer 等类可以申请堆外内存了，使用堆外内存需要在 JVM 启动参数中增加```-XX:MaxDirectMemorySize=512m```，否则会抛出异常：

```
java.lang.OutOfMemoryError: Direct buffer memory
```
千万要注意的是，如果你要使用direct buffer，一定不要加上DisableExplicitGC这个参数，因为这个参数会把你的System.gc()视作空语句，最后很容易导致OOM。另外，对于内存状况的 Debug 方式包括：

* jmap -heap
* jmap -histo:live
* 堆外内存使用率查看工具：[rednaxelafx](https://gist.github.com/rednaxelafx/1593521)

堆外内存泄露的问题定位通常比较麻烦，可以借助 [google-perftools](https://github.com/gperftools/gperftools) 这个工具，它可以输出不同方法申请堆外内存的数量。当然，如果你是64位系统，你需要先安装 [libunwind](http://download.savannah.gnu.org/releases/libunwind/) 库。

最后，JDK存在一些direct buffer的bug（比如[这个](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857566)和[这个](http://bugs.java.com/view_bug.do?bug_id=7112034)），可能引发OOM，所以也不妨升级JDK的版本看能否解决问题。
### Guava Cache 的 Off-Heap 讨论
[https://github.com/google/guava/issues/714](https://github.com/google/guava/issues/714)

Guava Cache 在2011年有一个讨论，是否要在 Cache 中增加 Off-Heap 实现，当时他们以增加 Guava's Cache 的复杂度为理由，没有实现这一部分功能，并且当时有另外的工具可以实现 [DirectMemory](https://github.com/raffaeleguidi/DirectMemory)。这个项目现在已经从 Apache 中退出了，其他的那几个也显得过重了。Guava Cache 提供了抽象接口，如果需要也可以自己实现使用 Off-Heap 的缓存方式。
### 引用资料
[http://www.raychase.net/1526](http://www.raychase.net/1526)
# IPC机制

## Android IPC

- **线程是CPU调度的最小单位**
- **Socket BInder ContentProvider**
- **Android中唯一的方法是个在Manifest给四大组件指定process属性,非常规还有Jni在native层开fork一个新的进程**

1. `android:process=":remote"`

​      这种方式是私有进程，其他应用组件不能跟他跑在同一个进程

2.  `android:process="com.zepeng.one.remote`

​      这种方式是全局进程，可以通过同一个shareUID和相同签名跑在同一个进程

- **都是由zygote进程创建**

- **多进程会造成以下几个问题**

1. 静态成员和单例模式失效
2. 线程同步机制失效
3. sharePreference可靠性下降，不支持并发读写
4. Application会多次创建，一个进程一个虚拟机一个Application

#### UID问题

- UID在应用安装的时候就分配好了，root权限可导出data/system/packages.list文件查看所有的应用包名以及对应的UID

- 普通应用的UID都是从10000开始，adb shell ps的u0_a134指的是user 0（主用户）下面的应用，ID是134，UID = 10000 + appId = 10000 + 134 = 10134，如果是工作用户（多用户）的话就是 UID = userID * 10000+(appId % 10000)，具体可以看frameworks\base\services\core\java\com\android\server\pm\Settings.java
- 应用双开，UID应该跟双开软件一样


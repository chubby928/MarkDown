# Android中的IPC方式

#### Bundle

- Bunder只支持基本数据类型，实现了Parcelable和Serializable接口的对象，和一些Android支持的特殊对象，如Bitmap已经实现了Parcelable,Intent.putExtras也用了bundle传递数据

#### 文件共享

- 文件不支持实时通信，并发读写也有问题，需要线程同步。sharePreferences会在内存中有缓存，所以多进程读写也是不可靠的，不建议在IPC中使用它

#### Messenger

- 底层用AIDL实现，用于message传递消息，一般服务端方法运行在主线程（看服务端的handler在什么线程中创建）。
- 客户端如果想回应服务端，需要在客户端创建一个handler再创建一个新的Messenger，并通过Message的replyTo参数传递给服务端 
- 自定义对象只能用message的data字段传输，而且当服务端和客户端处于不同进程时，服务端msg.getData().getParcelable("msg")  msg.data需要设置Classloader

#### ContentProvider

-  实现6个抽象方法 onCreate query update delete insert getType
- 除了onCreate在主线程，其余都在Binder线程池中
- query update delete insert存在并发访问，需要做好线程同步，但是只有一个SQLiteDatabase的话，可以保证线程安全。但用多个就不行

#### Socket

#### AIDL


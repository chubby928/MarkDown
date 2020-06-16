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

- 支持的数据类型：基本数据类型，String和CharSquence，实现了Parcelable的类，只支持ArrayList和HashMap，而且保证集合里面的元素也是必须被支持的，AIDL接口。

- AIDL中除了基本数据类型，其他类型参数必须指明方向in，out或者inout，这个在底层是有开销的，必须根据实际情况指明

  ```java
  // IBookAidlInterface.aidl
  package com.example.ipcdemo.aidl;
  
  import com.example.ipcdemo.aidl.Book;
  
  // Declare any non-default types here with import statements
  
  interface IBookAidlInterface {
      /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
       void addBook(in Book book);
       List<Book> getBookList();
  }
  ```

- AIDL的包结构客户端和服务端需要保持一致，因为客户端需要反序列化服务端中和AIDL相关的接口，如果包结构不相同就会反序列化不成功。 

- 当客户端想去监听服务端的行为时，需要给客户端注册一个监听，由于这个监听需要在服务端远程调用，因此这个接口需要是AIDL接口，另外在服务端需要用RemoteCallbackList去维护监听列表，因为当注册接口传递到服务端这里后，服务端的对象跟客户端对象不是同一个对象，无法通过普通的List.remove（Object）去进行注销，而RemoteCallbackList内部维护一个Map<IBinder,CallBack>,这个List利用多次跨进程传输AIDL接口，他们底层Binder不会改变，所以可以通过list的key去查询对应的监听。另外RemoteCallbackList是线程安全的。使用RemoteCallbackList要注意beginBroadcast和finishBroadcast要配对使用

- Binder的意外死亡我们可以通过两种方法重连，给Binder设置DeathRecipicent；在OnServiceDisconnected中重联，区别是DeathRecipicent的回调在客户端的binder线程池调用，Disconnect在主线程。

- AIDL的权限校验可以在onbind中验证调用者的权限或者在onTransaction中通过getUID和getPID获取包名验证等。

- 具体代码可看仓库内的IPCDemo


# Binder机制

- Binder可以理解为一种虚拟物理设备，驱动是/dev/binder；Binder是客户端和服务端进行通信的媒介；Binder是SereiceManager连接各种Manager和相应ManagerService的桥梁

- 如何手写AIDL接口编译出来的binder（AIDL只是为了方便我们生成binder接口）

  1. 编写一个继承IInterface接口，定义你调用的服务端的接口方法（方法抛出RemoteException）

  2. 方法个数的静态常量加DESCRIPTOR

     ```java
     static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);//onTransact方法内判断客户端远程调用了哪个方法
     static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
     private static final java.lang.String DESCRIPTOR = "com.example.ipcdemo.aidl.IBookAidlInterface"//唯一标识Binder，写参数的时候需要_data.writeInterfaceToken(DESCRIPTOR);  data.enforceInterface(descriptor);
     ```

  3. 写一个静态内部抽象类继承接口和Binder，再给这个抽象类写一个asInterface方法

     ```java
     public static com.example.ipcdemo.aidl.IBookAidlInterface asInterface(android.os.IBinder obj)
         {
           if ((obj==null)) {
             return null;
           }
           android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
           if (((iin!=null)&&(iin instanceof com.example.ipcdemo.aidl.IBookAidlInterface))) {
             return ((com.example.ipcdemo.aidl.IBookAidlInterface)iin);
           }
           return new com.example.ipcdemo.aidl.IBookAidlInterface.Stub.Proxy(obj);
         }
     ```

  4. 写在服务端的binder线程池中运行的onTransact方法

     ```java
     @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
         {
           java.lang.String descriptor = DESCRIPTOR;
           switch (code)
           {
             case INTERFACE_TRANSACTION:
             {
               reply.writeString(descriptor);
               return true;
             }
             case TRANSACTION_addBook:
             {
               data.enforceInterface(descriptor);
               com.example.ipcdemo.aidl.Book _arg0;
               if ((0!=data.readInt())) {
                 _arg0 = com.example.ipcdemo.aidl.Book.CREATOR.createFromParcel(data);
               }
               else {
                 _arg0 = null;
               }
               this.addBook(_arg0);
               reply.writeNoException();
               return true;
             }
             case TRANSACTION_getBookList:
             {
               data.enforceInterface(descriptor);
               java.util.List<com.example.ipcdemo.aidl.Book> _result = this.getBookList();
               reply.writeNoException();
               reply.writeTypedList(_result);
               return true;
             }
             default:
             {
               return super.onTransact(code, data, reply, flags);
             }
           }
         }
     ```

  5. 写给客户端调用的接口代理类

     ```java
     private static class Proxy implements com.example.ipcdemo.aidl.IBookAidlInterface
         {
           private android.os.IBinder mRemote;
           Proxy(android.os.IBinder remote)
           {
             mRemote = remote;
           }
           @Override public android.os.IBinder asBinder()
           {
             return mRemote;
           }
           public java.lang.String getInterfaceDescriptor()
           {
             return DESCRIPTOR;
           }
           /**
                * Demonstrates some basic types that you can use as parameters
                * and return values in AIDL.
                */
           @Override public void addBook(com.example.ipcdemo.aidl.Book book) throws android.os.RemoteException
           {
             android.os.Parcel _data = android.os.Parcel.obtain();
             android.os.Parcel _reply = android.os.Parcel.obtain();
             try {
               _data.writeInterfaceToken(DESCRIPTOR);
               if ((book!=null)) {
                 _data.writeInt(1);
                 book.writeToParcel(_data, 0);
               }
               else {
                 _data.writeInt(0);
               }
               boolean _status = mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
               if (!_status && getDefaultImpl() != null) {
                 getDefaultImpl().addBook(book);
                 return;
               }
               _reply.readException();
             }
             finally {
               _reply.recycle();
               _data.recycle();
             }
           }
           @Override public java.util.List<com.example.ipcdemo.aidl.Book> getBookList() throws android.os.RemoteException
           {
             android.os.Parcel _data = android.os.Parcel.obtain();
             android.os.Parcel _reply = android.os.Parcel.obtain();
             java.util.List<com.example.ipcdemo.aidl.Book> _result;
             try {
               _data.writeInterfaceToken(DESCRIPTOR);
               boolean _status = mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
               if (!_status && getDefaultImpl() != null) {
                 return getDefaultImpl().getBookList();
               }
               _reply.readException();
               _result = _reply.createTypedArrayList(com.example.ipcdemo.aidl.Book.CREATOR);
             }
             finally {
               _reply.recycle();
               _data.recycle();
             }
             return _result;
           }
           public static com.example.ipcdemo.aidl.IBookAidlInterface sDefaultImpl;
         }
     ```

- 如果服务端进程由于某种原因停止，我们通过给Binder设置死亡代理，当Binder死亡的时候收到通知，重新连接

```kotlin
private val mBookServiceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            mIBookAidlInterface = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if(service == null){
                return
            }
            mIBookAidlInterface = IBookAidlInterface.Stub.asInterface(service);
            mIBookAidlInterface?.asBinder()?.linkToDeath(mDeathRecipient,0)//设置死亡代理
        }
    }

    private val mDeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            if (mIBookAidlInterface == null){
                return
            }
            mIBookAidlInterface.asBinder().unlinkToDeath(mDeathRecipient,0)
            bindService(Intent(this@MainActivity,BookService::class.java),mBookServiceConnection,
                Context.BIND_AUTO_CREATE)//重连服务
        }
    }
```


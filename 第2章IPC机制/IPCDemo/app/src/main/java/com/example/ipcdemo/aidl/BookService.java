package com.example.ipcdemo.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.example.ipcdemo.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookService extends Service {
    private static final String TAG = "BookService";
    private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> listeners = new RemoteCallbackList<>();
    private AtomicBoolean isDestroy = new AtomicBoolean(false);

    private Binder mBinder = new IBookAidlInterface.Stub(){
        @Override
        public void addBook(Book book) throws RemoteException {
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){

            }
            Log.d(TAG, "addBook: "+book.getId()+" "+Thread.currentThread().getName());
            books.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return books;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.d(TAG, "registerListener: "+listener+" "+Thread.currentThread().getName());
            listeners.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new worker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        int check = checkCallingOrSelfPermission("com.example.ipcdemo.ACCESS_BOOK_SERVICE");
        if(check == PackageManager.PERMISSION_GRANTED){
            return mBinder;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy.set(true);
    }

    private class worker implements Runnable{
        @Override
        public void run() {
            while (!isDestroy.get()){
                books.add(new Book(10,"jack"));
                int count = listeners.beginBroadcast();
                for(int i = 0; i < count; i++){
                    try {
                        listeners.getRegisteredCallbackItem(i).onNewBookArrived(books.get(books.size() -1));
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }
}

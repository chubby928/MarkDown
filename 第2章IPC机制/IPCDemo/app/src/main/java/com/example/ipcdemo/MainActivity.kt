package com.example.ipcdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.ipcdemo.aidl.Book
import com.example.ipcdemo.aidl.BookService
import com.example.ipcdemo.aidl.IBookAidlInterface
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }
    private var mIBookAidlInterface : IBookAidlInterface? = null
    private val mIOnNewBookArrivedListener = object : IOnNewBookArrivedListener.Stub(){
        override fun onNewBookArrived(newBook: Book?) {
            Log.d(TAG,"i know ${newBook?.id} book arrived ")
        }
    }

    private val mBookServiceConnection :ServiceConnection= object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            mIBookAidlInterface = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if(service == null){
                return
            }
            mIBookAidlInterface = IBookAidlInterface.Stub.asInterface(service);
            mIBookAidlInterface?.asBinder()?.linkToDeath(mDeathRecipient,0)
            Log.d(TAG,"$mIOnNewBookArrivedListener")
            mIBookAidlInterface?.addBook(Book(13,"ff"));

            mIBookAidlInterface?.registerListener(mIOnNewBookArrivedListener);
        }
    }

    private val mDeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            if (mIBookAidlInterface == null){
                return
            }
            mIBookAidlInterface?.asBinder()?.unlinkToDeath(this,0)
            bindService(Intent(this@MainActivity,BookService::class.java),mBookServiceConnection,
                Context.BIND_AUTO_CREATE)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener{
            bindService(Intent(this,BookService::class.java),mBookServiceConnection,
                Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        mIBookAidlInterface?.run {
            asBinder()?.run {
                if (isBinderAlive){
                    mIBookAidlInterface?.unregisterListener(mIOnNewBookArrivedListener)

                }
            }
        }

        unbindService(mBookServiceConnection)
        super.onDestroy()
    }
}

package com.example.corouttt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        GlobalScope.launch {
//            println("Cortiness1 ${Thread.currentThread().name}")
//        }
//        thread{
//            println("Cortiness2 ${Thread.currentThread().name}")
//        }
        GlobalScope.launch (Dispatchers.Main){
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        classicIoCode1(true) {
            uiCode1()
        }
    }

    private fun classicIoCode1(uiThread: Boolean , block:()-> Unit){
        thread {
            println("Coroutines  classic io1 ${Thread.currentThread().name}")
            runOnUiThread(block)
        }
    }

    private suspend fun ioCode1(){
//        withContext(Dispatchers.IO){
//            println("Coroutines io1 ${Thread.currentThread().name}")
//        }
    }

    private suspend fun ioCode2(){
        withContext(Dispatchers.IO){
            println("Coroutines io2 ${Thread.currentThread().name}")
        }
    }
    private suspend fun ioCode3(){
        withContext(Dispatchers.IO){
            println("Coroutines io3 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode1(){
        println("Coroutines ui1 ${Thread.currentThread().name}")
    }

    private fun uiCode2(){
        println("Coroutines ui2 ${Thread.currentThread().name}")

    }

    private fun uiCode3(){
        println("Coroutines ui3 ${Thread.currentThread().name}")

    }
}
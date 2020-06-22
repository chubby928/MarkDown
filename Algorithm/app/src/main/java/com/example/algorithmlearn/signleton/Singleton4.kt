package com.example.algorithmlearn.signleton

//懒汉式
class Singleton4 private constructor(){
    companion object{
        val instance :Singleton4 by lazy{
            Singleton4()
        }
    }
}
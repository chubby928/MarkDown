package com.example.ipcdemo

import java.io.Serializable

class User : Serializable {
    companion object{
        const val serialVersionUID = 4929921994037219917L
    }

    private val id = 5;

    private val name = "jack"
}
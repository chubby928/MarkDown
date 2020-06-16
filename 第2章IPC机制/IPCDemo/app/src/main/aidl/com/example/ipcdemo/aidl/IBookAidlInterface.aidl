// IBookAidlInterface.aidl
package com.example.ipcdemo.aidl;

import com.example.ipcdemo.aidl.Book;
import com.example.ipcdemo.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void addBook(in Book book);
     List<Book> getBookList();
     void registerListener(IOnNewBookArrivedListener listener);
     void unregisterListener(IOnNewBookArrivedListener listener);
}

package com.example.ipcdemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private int id;
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public static Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel parcel){
        id = parcel.readInt();
        name = parcel.readString();
    }

    public Book(int id , String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
}

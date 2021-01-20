package com.example.application1;

import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Type;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

@Entity(tableName = "contacts")
public class Contact implements Parcelable {
    //public String zName;
    //public String zPhone;

    @PrimaryKey(autoGenerate = true)
    public int contactId;

    @ColumnInfo(name = "name")
    public String zName;

    @ColumnInfo(name = "phone")
    public String zPhone;

    @ColumnInfo(name = "datetime")
    public String datetime;

    public Contact() {

    }

    public int getContactId() {
        return contactId;
    }

    public String getName() {
        return zName;
    }

    public String getPhone() {
        return zPhone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setName(String name) {
        this.zName = name;
    }

    public void setPhone(String phone) {
        this.zPhone = phone;
    }

    protected Contact(Parcel in) {
        contactId = in.readInt();
        zName = in.readString();
        zPhone = in.readString();
        datetime = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contactId);
        dest.writeString(zName);
        dest.writeString(zPhone);
        dest.writeString(datetime);
    }
}

package org.yuur.d2i;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;

public class Revenue implements Parcelable{


    private String tPosition;
    private String payStatus;
    private String title;
    private String amount;

    public Revenue(String tPosition, String payStatus, String title, String amount){
        this.tPosition = tPosition;
        this.payStatus = payStatus;
        this.title = title;
        this.amount = amount;
    }

    protected Revenue(Parcel in) {
        tPosition = in.readString();
        payStatus = in.readString();
        title = in.readString();
        amount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tPosition);
        dest.writeString(payStatus);
        dest.writeString(title);
        dest.writeString(amount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Revenue> CREATOR = new Creator<Revenue>() {
        @Override
        public Revenue createFromParcel(Parcel in) {
            return new Revenue(in);
        }

        @Override
        public Revenue[] newArray(int size) {
            return new Revenue[size];
        }
    };

    public String getPayStatus() {
        return payStatus;
    }

    public String getTPosition() {
        return tPosition;
    }

    public void setTPosition(String tPosition) {
        this.tPosition = tPosition;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}


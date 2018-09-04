package org.yuur.d2i;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable{

    private String tPosition;
    private String payStatus;
    private String title;
    private String amount;

    public Expense(String tPosition,String payStatus, String title, String amount){
        this.tPosition = tPosition;
        this.payStatus = payStatus;
        this.title = title;
        this.amount = amount;
    }

    protected Expense(Parcel in) {
        tPosition = in.readString();
        payStatus = in.readString();
        title = in.readString();
        amount = in.readString();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
    public String getTPosition() {
        return tPosition;
    }

    public void setTPosition(String tPosition) {
        this.tPosition = tPosition;
    }
    public String getPayStatus() {
        return payStatus;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tPosition);
        parcel.writeString(payStatus);
        parcel.writeString(title);
        parcel.writeString(amount);
    }
}


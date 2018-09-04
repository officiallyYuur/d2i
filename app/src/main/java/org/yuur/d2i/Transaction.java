package org.yuur.d2i;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;

//
public class Transaction implements Parcelable {
    private GregorianCalendar transactionDate;
    int transactionYear;
    int transactionMonth;
    int transactionDay;
    private String transactionType;
    private String transactionTitle;
    private int transactionAmount;
    String transactionPayState;

    public Transaction(){

    }
    public Transaction(GregorianCalendar transactionDate, String transactionType, String transactionTitle, int transactionAmount, String transactionPayState){
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transactionTitle = transactionTitle;
        this.transactionAmount = transactionAmount;
        this.transactionPayState = transactionPayState;
    }

    protected Transaction(Parcel in) {
        transactionYear = in.readInt();
        transactionMonth = in.readInt();
        transactionDay = in.readInt();
        transactionDate =  new GregorianCalendar(transactionYear, transactionMonth, transactionDay);
        transactionType = in.readString();
        transactionTitle = in.readString();
        transactionAmount = in.readInt();
        transactionPayState = in.readString();

    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public GregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(GregorianCalendar transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionTitle() {
        return transactionTitle;
    }

    public void setTransactionTitle(String transactionTitle) {
        this.transactionTitle = transactionTitle;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionPayState() {
        return transactionPayState;
    }

    public void setTransactionPayState(String transactionPayState) {
        this.transactionPayState = transactionPayState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(transactionDate.get(Calendar.YEAR));
        parcel.writeInt(transactionDate.get(Calendar.MONTH));
        parcel.writeInt(transactionDate.get(Calendar.DAY_OF_MONTH));
        parcel.writeString(transactionType);
        parcel.writeString(transactionTitle);
        parcel.writeInt(transactionAmount);
        parcel.writeString(transactionPayState);
    }
}

package org.yuur.d2i;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyBalance implements Parcelable{

    private GregorianCalendar balanceDate;
    private int balance;

    public DailyBalance(GregorianCalendar balanceDate, int balance){
        this.balanceDate = balanceDate;
        this.balance = balance;
    }

    protected DailyBalance(Parcel in) {
        int transactionYear = in.readInt();
        int transactionMonth = in.readInt();
        int transactionDay = in.readInt();
        balanceDate =  new GregorianCalendar(transactionYear, transactionMonth, transactionDay);
        balance = in.readInt();
    }

    public static final Creator<DailyBalance> CREATOR = new Creator<DailyBalance>() {
        @Override
        public DailyBalance createFromParcel(Parcel in) {
            return new DailyBalance(in);
        }

        @Override
        public DailyBalance[] newArray(int size) {
            return new DailyBalance[size];
        }
    };

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public GregorianCalendar getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(GregorianCalendar date) {
        this.balanceDate = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(balanceDate.get(Calendar.YEAR));
        parcel.writeInt(balanceDate.get(Calendar.MONTH));
        parcel.writeInt(balanceDate.get(Calendar.DAY_OF_MONTH));
        parcel.writeInt(balance);
    }
}

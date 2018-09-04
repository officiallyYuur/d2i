package org.yuur.d2i;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity {

    private int mTodayDay;
    private int mTodayMonth;
    private int mTodayYear;
    private GregorianCalendar newEvent;
    private GregorianCalendar mDatePicker;
    public ArrayList<DailyBalance> mDailyBalances;
    public ArrayList<Revenue> mRevenues = new ArrayList<>();
    public ArrayList<Expense> mExpenses = new ArrayList<>();
    private ArrayList<Transaction> mTransactions = new ArrayList<>();
    final CharSequence[] items = {"One Time Event ", "Repeats Daily ", "Repeats Weekly", "Repeats Monthly", "Repeats Yearly"};
    String frequency;
    Gson gson = new Gson();


    // *** ON CREATE *** //
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *** INITIALIZE DATA FOR THE HUD *** //
        // Initialize Today's Date
        GregorianCalendar mToday = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        mTodayDay = mToday.get(Calendar.DAY_OF_MONTH) +1;
        mTodayMonth = mToday.get(Calendar.MONTH);
        mTodayYear = mToday.get(Calendar.YEAR);

        //Initialize Hud with empty arrays
        if (savedInstanceState != null) {
            mTransactions = savedInstanceState.getParcelableArrayList("transactions");
            mDailyBalances = savedInstanceState.getParcelableArrayList("dailyBalances");
            int mDatePickeryear = Integer.valueOf(savedInstanceState.getString("mDatePickerYear"));
            int mDatePickermonth = Integer.valueOf(savedInstanceState.getString("mDatePickerMonth"));
            int mDatePickerday = Integer.valueOf(savedInstanceState.getString("mDatePickerDay"));
            mDatePicker = new GregorianCalendar(mDatePickeryear, mDatePickermonth, mDatePickerday);

        } else {
            mTransactions = new ArrayList<>();
                readTransactionsFromFile(this);
                Type transactionListType = new TypeToken<ArrayList<Transaction>>(){}.getType();
                ArrayList<Transaction> TransactionArray = gson.fromJson(readTransactionsFromFile(this), transactionListType);
                if (TransactionArray != null){
                    mTransactions = TransactionArray;
                } else {
                    mTransactions.add(new Transaction(mToday, "revenue", "Sample Income", 0, "due"));
                    mTransactions.add(new Transaction(mToday, "expense", "Sample bill", 0, "due"));
                }

            mDailyBalances = new ArrayList<>();
                readBalanceFromFile(this);
                Type balanceListType = new TypeToken<ArrayList<DailyBalance>>(){}.getType();
                ArrayList<DailyBalance> BalanceArray = gson.fromJson(readBalanceFromFile(this), balanceListType);
                if (BalanceArray != null) {
                    mDailyBalances = BalanceArray;
                } else {
                    mDailyBalances.add(new DailyBalance(mToday, 0));
                }
                int x = 0;
                int y = 0;
                int z ;
                if((mDailyBalances.get(0).getBalanceDate()).before(mToday))  {
                    for (int i=0; i < mTransactions.size(); i++) {
                        if((mTransactions.get(i).getTransactionDate().before(mToday)) && !(mTransactions.get(i).getTransactionDate().before(mDailyBalances.get(0).getBalanceDate()))){
                            if (mTransactions.get(i).getTransactionType().equals("revenue")){
                                x = x + mTransactions.get(i).getTransactionAmount();
                            }
                            if (mTransactions.get(i).getTransactionType().equals("expense")){
                                y = y + mTransactions.get(i).getTransactionAmount();
                            }
                        }
                    }
                    z = mDailyBalances.get(0).getBalance() + x - y;
                    mDailyBalances.add(new DailyBalance(mToday, z));
                    mDailyBalances.remove(0);
                }
            Log.v("TAG", " OnCreate: mDailyBalances.get(0).getAmount = " + mDailyBalances.get(0).getBalance());
            String gBalance;
            gBalance = gson.toJson(mDailyBalances);
            writeBalanceToFile(gBalance, this);
            Log.v("TAG", " OnCreate: gBalance = " + gBalance);
            Log.v("TAG", " OnCreate: mDailyBalances = " + mDailyBalances);
            mDailyBalances.add(new DailyBalance(mToday, -20));

            String gTransactions;
                List<Integer> arr = new ArrayList<>();
                for (int t = 0; t < mTransactions.size(); t++) {
                    if (mTransactions.get(t).getTransactionDate().before(mToday))  {
                        arr.add(t);
                    }
                }
                Collections.sort(arr, Collections.reverseOrder());
                for (int t = 0; t < arr.size(); t++) {
                    int w = arr.get(t);
                    mTransactions.remove(w);
                }
                gTransactions = gson.toJson(mTransactions);
                writeTransactionsToFile(gTransactions, this);

            if (readDatePickerFromFile(this) != null) {
                readDatePickerFromFile(this);
                DatePicker gDatePicker = gson.fromJson(readDatePickerFromFile(this), DatePicker.class);

                int mDatePickeryear = gDatePicker.getYear();
                int mDatePickermonth = gDatePicker.getMonth();
                int mDatePickerday = gDatePicker.getDayOfMonth();
                mDatePicker = new GregorianCalendar(mDatePickeryear, mDatePickermonth, mDatePickerday);
                Log.v("TAG", "OnCreate: mDatePicker = " + mDatePicker);
            } else {
                mDatePicker = mToday;
            Log.v("TAG", "OnCreate: mDatePicker = " + mDatePicker);
            }
        }

        //Initialize views from the top dowN
        setContentView(R.layout.activity_main);
        Log.v("TAG", "OnCreate: activity_main layout setView ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setStatusBarColor(getResources().getColor(R.color.Black));
        Log.v("TAG", "OnCreate: orientation = portrait ");
        Log.v("TAG", "OnCreate: updateTransactions(): method Called ");
        updateTransactions();
        Log.v("TAG", "OnCreate: refreshInfo(): method Called ");
        refreshInfo();

        Log.v("TAG", "OnCreate: detailedTitles: titles set ");
        final TextView detailedRevenueTitle = findViewById(R.id.detailedRevenueTitle);
        int a = mDatePicker.get(Calendar.MONTH) + 1;
            detailedRevenueTitle.setText("Income for " + a + "/" + mToday.get(Calendar.DAY_OF_MONTH) + "/" + mToday.get(Calendar.YEAR) + "");
        final TextView detailedExpenseTitle = findViewById(R.id.detailedExpensesTitle);
            detailedExpenseTitle.setText("Expenses for " + a + "/" + mToday.get(Calendar.DAY_OF_MONTH) + "/" + mToday.get(Calendar.YEAR) + "");

        MaterialCalendarView widget = findViewById(R.id.calendarView2);
        Log.v("TAG", "OnCreate: MaterialCalendarView: View Initialized");
        List<CalendarDay> dotCalendarDays = new ArrayList<>();
        for(int i=0; i < mTransactions.size(); i++) {
            GregorianCalendar dotDate  = mTransactions.get(i).getTransactionDate();
            dotCalendarDays.add(i, CalendarDay.from(dotDate.getTime()));
            }
        widget.addDecorator(new MySelectorDecorator(this));

        widget.addDecorator(new EventDecorator(getResources().getColor(R.color.YellowA400), dotCalendarDays));
        widget.setSelectedDate(mDatePicker.getTime());
        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //oneDayDecorator.setDate(date.getDate());
              //  widget.invalidateDecorators();
                widget.addDecorator(new MySelectorDecorator(MainActivity.this));
                //If you change a decorate, you need to invalidate decorators
                mDatePicker = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDay());

                Log.v("TAG", "CalendarView: onSelectedDayChange: mDatePicker Changed to = " + mDatePicker);
                String gDatePicker = gson.toJson(mDatePicker);
                Log.v("TAG", "CalendarView: onSelectedDayChange: gDatePicker updated to = " + gDatePicker);
                writeDatePickerToFile(gDatePicker, getApplicationContext());
                Log.v("TAG", "CalendarView: onSelectedDayChange: RefreshInfo Method Called");
                refreshInfo();
                int a = mDatePicker.get(Calendar.MONTH) + 1;
                detailedRevenueTitle.setText("Revenues for " + a + "/" + mDatePicker.get(Calendar.DAY_OF_MONTH) + "/" + mDatePicker.get(Calendar.YEAR) + "");
                detailedExpenseTitle.setText("Expenses for " + a  + "/" + mDatePicker.get(Calendar.DAY_OF_MONTH) + "/" + mDatePicker.get(Calendar.YEAR) + "");
            }
        });



        // *** FabPlus *** //
        FloatingActionButton fabPlus = findViewById(R.id.fabPlus);
        fabPlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // widget.invalidateDecorators();
               // widget.addDecorator(new EventDecorator(Color.GREEN, revCalendarDays));
                return true;
            }
        });
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TAG", "FabPlus: FabPlus Clicked");
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder revenueDialog = new AlertDialog.Builder(v.getContext());
                // 2. Chain together various setter methods to set the dialog characteristics
                // 2.1 Title
                revenueDialog.setTitle("Add Income for " + (mDatePicker.get(Calendar.MONTH) + 1) + "/" + mDatePicker.get(Calendar.DAY_OF_MONTH) + "/" + mDatePicker.get(Calendar.YEAR) + "");
                Log.v("TAG", "FabPlus : RevDialog : mDatePicker = " + mDatePicker);
                // 2.2 Body
                View view = getLayoutInflater().inflate(R.layout.dialog_revenue, null);
                final EditText newRevenueAmount = view.findViewById(R.id.etRevenue);
                final EditText newRevenueTitle = view.findViewById(R.id.tvNewTitle);
                revenueDialog.setView(view);
                revenueDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                frequency = "once";
                                break;
                            case 1:
                                frequency = "daily";
                                break;
                            case 2:
                                frequency = "weekly";
                                break;
                            case 3:
                                frequency = "monthly";
                                break;
                            case 4:
                                frequency = "yearly";
                                break;
                        }
                    }
                });
                // Set Positive Button for FabPlus
                revenueDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface revenueDialog, int id) {
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked");
                            newEvent = new GregorianCalendar(mDatePicker.get(Calendar.YEAR), mDatePicker.get(Calendar.MONTH), mDatePicker.get(Calendar.DAY_OF_MONTH));
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : newEvent = " + newEvent);
                        try {
                        int newAmt = Integer.valueOf(newRevenueAmount.getText().toString());
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : newAmt = " + newAmt);
                        String newTitle = newRevenueTitle.getText().toString();
                        if (newTitle.equals("")) {
                            newTitle = "Discretionary";
                            Toast.makeText(MainActivity.this, "Title set to \"Discretionary\" by default",
                                    Toast.LENGTH_LONG).show();
                        }
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : newTitle = " + newTitle);

                            switch (frequency) {
                                case "once":
                                    try {
                                        mTransactions.add(new Transaction(newEvent, "revenue", newTitle, newAmt, "due"));
                                        Toast.makeText(MainActivity.this, "Income Added Successfully",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        Toast.makeText(MainActivity.this, "Error",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "daily":
                                    try {
                                        for (int i = 0; i <= 365; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "revenue", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.DAY_OF_MONTH, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Daily Income Added Successfully for 365 days",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        revenueDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "weekly":
                                    try {
                                        for (int i = 0; i <= 52; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "revenue", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.DAY_OF_MONTH, 7);
                                        }
                                        Toast.makeText(MainActivity.this, "Weekly Income Added Successfully for 52 weeks",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        revenueDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "monthly":
                                    try {
                                        for (int i = 0; i <= 12; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "revenue", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.MONTH, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Monthly Income Added Successfully for 12 months",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        revenueDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "yearly":
                                    try {
                                        for (int i = 0; i <= 1; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "revenue", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.YEAR, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Yearly Income Added Successfully for 1 year",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        revenueDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                        } catch (NullPointerException e) {
                            revenueDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Incomplete Data Entered",
                                    Toast.LENGTH_LONG).show();
                            Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : NullPointerException" );
                        } catch (NumberFormatException e) {
                            revenueDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Income Amount not Entered",
                                    Toast.LENGTH_LONG).show();
                            Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : NumberFormatException" );
                        }

                        frequency = null;
                        revenueDialog.dismiss();
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : revenueDialog dismissed" );

                        String gTransactions = gson.toJson(mTransactions);
                        writeTransactionsToFile(gTransactions, MainActivity.this);
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : Json: gTransactions written to file");
                        Log.v("TAG", "FabPlus : RevDialog : Positive Button Clicked : Json: gTransactions = " + gTransactions);

                        refreshInfo();

                    }
                });
                //Set Negative Button for FabPlus
                revenueDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface revenueDialog, int id) {
                        // 2.3.1 User cancelled the dialog
                        frequency = null;
                        revenueDialog.dismiss();
                        Log.v("TAG", "FabPlus : RevDialog : Negative Button Clicked : revenueDialog dismissed" );

                    }
                });
                //Get the AlertDialog from create()
                revenueDialog.show();

                Log.v("TAG", "FabPlus : RevDialog : : revenueDialog created" );

            }
        });


        // *** FabMinus *** //
        FloatingActionButton fabMinus = findViewById(R.id.fabMinus);
        fabMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //widget.invalidateDecorators();
                /*int k =0;
                for(int i=0; i < mRevenues.size(); i++) {
                    if (Objects.equals(mTransactions.get(parseInt(mRevenues.get(i).getTPosition())).getTransactionType(), "expense")) {
                        expCalendarDays.add(k, CalendarDay.from(mTransactions.get(k).getTransactionDate().getTime()));
                        k++;
                        Log.v("TAG", "MaterialCalendarView: k = " + k + ", expCalendarDays = " + expCalendarDays.get(k));
                    }
                }
                Log.v("TAG", "MaterialCalendarView: k = " + k + ", expCalendarDays = " + expCalendarDays);
                widget.addDecorator(new EventDecorator(Color.RED, expCalendarDays)); */
                return true;
            }
        });
        fabMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Log.v("TAG", "FabMinus: FabMinus Clicked");
                AlertDialog.Builder expDialog = new AlertDialog.Builder(w.getContext());
                expDialog.setView(R.layout.dialog_expense);
                // 2. Chain together various setter methods to set the dialog characteristics
                // 2.1 Title
                expDialog.setTitle("Add Expense for " + (mDatePicker.get(Calendar.MONTH) + 1) + "/" + mDatePicker.get(Calendar.DAY_OF_MONTH) + "/" + mDatePicker.get(Calendar.YEAR) + "");
                Log.v("TAG", "FabMinus : ExpDialog : mDatePicker = " + mDatePicker);
                // 2.2 Body
                View view = getLayoutInflater().inflate(R.layout.dialog_expense, null);
                final EditText newExpenseAmount = view.findViewById(R.id.etExpense);
                final EditText newExpenseTitle = view.findViewById(R.id.tvNewTitle);
                expDialog.setView(view);
                expDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                frequency = "once";
                                break;
                            case 1:
                                frequency = "daily";
                                break;
                            case 2:
                                frequency = "weekly";
                                break;
                            case 3:
                                frequency = "monthly";
                                break;
                            case 4:
                                frequency = "yearly";
                                break;
                        }
                    }
                });
                // 2.3 Buttons
                expDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    // 2.3.1 User clicked OK button
                    public void onClick(DialogInterface expDialog, int id) {
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked");
                        newEvent = new GregorianCalendar(mDatePicker.get(Calendar.YEAR), mDatePicker.get(Calendar.MONTH), mDatePicker.get(Calendar.DAY_OF_MONTH));
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : newEvent = " + newEvent);
                        int newAmt = Integer.valueOf(newExpenseAmount.getText().toString());
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : newAmt = " + newAmt);
                        String newTitle = newExpenseTitle.getText().toString();
                        if (newTitle.equals("")) {
                            newTitle = "Discretionary";
                            Toast.makeText(MainActivity.this, "Title set to Discretionary by default",
                                    Toast.LENGTH_LONG).show();
                            Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : newTitle = " + newTitle);
                        }
                        try {
                            switch (frequency) {
                                case "once":
                                    try {
                                        mTransactions.add(new Transaction(newEvent, "expense", newTitle, newAmt, "due"));
                                        Toast.makeText(MainActivity.this, "One Time Expense Added Successfully",
                                                Toast.LENGTH_LONG).show();
                                    } catch (NumberFormatException e) {
                                        expDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Expense not Entered",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    }
                                    break;
                                case "daily":
                                    try {
                                        for (int i = 0; i <= 365; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "expense", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.DAY_OF_MONTH, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Daily Expense Added Successfully for 365 days",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        expDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Expense not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "weekly":
                                    try {
                                        for (int i = 0; i <= 52; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "expense", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.DAY_OF_MONTH, 7);
                                        }
                                        Toast.makeText(MainActivity.this, "Weekly Expense Added Successfully for 52 weeks",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        expDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "monthly":
                                    try {
                                        for (int i = 0; i <= 12; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "expense", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.MONTH, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Monthly Expense Added Successfully for 12 months",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        expDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case "yearly":
                                    try {
                                        for (int i = 0; i <= 1; i++) {
                                            GregorianCalendar newDailyEvent = new GregorianCalendar(newEvent.get(Calendar.YEAR), newEvent.get(Calendar.MONTH), newEvent.get(Calendar.DAY_OF_MONTH));
                                            mTransactions.add(new Transaction(newDailyEvent, "expense", newTitle, newAmt, "due"));
                                            newEvent.add(Calendar.YEAR, 1);
                                        }
                                        Toast.makeText(MainActivity.this, "Expense Added Successfully for 1 year",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : frequency = " + frequency);
                                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : mTransactions = " + mTransactions);
                                    } catch (NumberFormatException e) {
                                        expDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Title not Entered",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                        } catch (NullPointerException e) {
                            expDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Incomplete Data: Nothing Entered",
                                    Toast.LENGTH_LONG).show();
                            Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : NullPointerException" );
                        }
                        // Dialog is dismissed and the view is updated
                        frequency = null;
                        expDialog.dismiss();
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : expDialog dismissed" );

                        String gTransactions = gson.toJson(mTransactions);
                        writeTransactionsToFile(gTransactions, MainActivity.this);
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : Json: gTransactions written to file");
                        Log.v("TAG", "FabMinus : ExpDialog : Positive Button Clicked : Json: gTransactions = " + gTransactions);

                        refreshInfo();
                    }
                });
                expDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface expDialog, int id) {
                        // 2.3.1 User cancelled the dialog
                        frequency = null;
                        expDialog.dismiss();
                        Log.v("TAG", "FabMinus : ExpDialog : Negative Button Clicked : expDialog dismissed" );
                    }
                });
                // 3. Get the AlertDialog from create()
                expDialog.show();
                Log.v("TAG", "FabMinus : ExpDialog : expDialog created" );
            }
        });
    }

    public void refreshInfo() {
        Log.v("refreshInfo", "Method Accessed");
        String hudEndingBalance;
        String hudRevAmt;
        String hudExpAmt;
        int endingBalance;
        String mPayState;
        String mRevenueTitle;
        int mRevenueAmount;
        String mExpenseTitle;
        int mExpenseAmount;
        /*  Checks for a null mDailyBalance List.
            If mDailyBalances is empty then a value of zero is set to be displayed.
            If mDailyBalances is not empty then it searches mDailyBalances for todaysBalance,
               and gets it ready to be displayed.   */
        String hudtodaysBalance;
        int todaysBalance;
        if (mDailyBalances == null || mDailyBalances.isEmpty()) {
            Log.v("refreshInfo", "mDailyBalances == null");
            todaysBalance = 0;
            hudtodaysBalance = "$0";
            Log.v("refreshInfo", "todaysBalance = " + hudtodaysBalance);
        } else {
            Log.v("refreshInfo", "mDailyBalances != null");
                    todaysBalance = mDailyBalances.get(0).getBalance();
                    if (todaysBalance >= 0) {
                        hudtodaysBalance = "$" + todaysBalance;
                    } else {
                        hudtodaysBalance = "-$" + abs(todaysBalance);
                    }
                    Log.v("refreshInfo", "todaysBalance = " + hudtodaysBalance);
            }
        /*  Checks for a null mTransactions table.
            If mTransactions is empty, then values of zero for revenue, expenses, and ending
                balance is set to be displayed.
            If mTransactions is not empty, then it searches mTransactions for revenue, expenses,
                and ending balance is set to be displayed.  */
        if (mTransactions == null || mTransactions.isEmpty()) {
            Log.v("refreshInfo", "mTransactions == null ");
            hudRevAmt = "$0";
            hudExpAmt = "$0";
            hudEndingBalance = "$0";
            mRevenues = new ArrayList<>();
            mExpenses = new ArrayList<>();
            Log.v("refreshInfo", "mTransactions all = $" + 0);
        } else {
            Log.v("refreshInfo", "mTransactions != null ");
            mRevenues = new ArrayList<>();
            mExpenses = new ArrayList<>();
            int x = 0;
            int y = 0;
            Log.v("refreshInfo", "Loop: mTransactions calculating revenues, expenses, ending balance, mRevenues, & mExpenses ");
            for (int i = 0; i < mTransactions.size(); i++) {
                if (Objects.equals(mTransactions.get(i).getTransactionType(), "revenue") && !(mTransactions.get(i).getTransactionDate().after(mDatePicker))) {
                    x = x + mTransactions.get(i).getTransactionAmount();
                    if ((Objects.equals(mTransactions.get(i).getTransactionType(), "revenue")) && ((mTransactions.get(i).getTransactionDate()).equals(mDatePicker))) {
                        String mtransPosition = String.valueOf(i);
                        mPayState = mTransactions.get(i).getTransactionPayState();
                        //Log.v("refresh details", "refresh details inner loop accessed" + mPayState);
                        mRevenueTitle = mTransactions.get(i).getTransactionTitle();
                        mRevenueAmount = mTransactions.get(i).getTransactionAmount();
                        mRevenues.add(new Revenue(mtransPosition, mPayState, mRevenueTitle, "" + mRevenueAmount));
                    }
                } else {
                    if (Objects.equals(mTransactions.get(i).getTransactionType(), "expense") && !(mTransactions.get(i).getTransactionDate().after(mDatePicker))) {
                        y = y + mTransactions.get(i).getTransactionAmount();
                        if ((Objects.equals(mTransactions.get(i).getTransactionType(), "expense")) && ((mTransactions.get(i).getTransactionDate()).equals(mDatePicker))) {
                            String mtransPosition = String.valueOf(i);
                            mPayState = mTransactions.get(i).getTransactionPayState();
                            //Log.v("refresh details", "refresh details inner loop accessed" + mPayState);
                            mExpenseTitle = mTransactions.get(i).getTransactionTitle();
                            mExpenseAmount = mTransactions.get(i).getTransactionAmount();
                            mExpenses.add(new Expense(mtransPosition, mPayState, mExpenseTitle, "" + mExpenseAmount));
                        }
                    }
                }
            }
            hudRevAmt = "$" + x + "";
            hudExpAmt = "$" + y + "";
            endingBalance = todaysBalance + x - y;
            if (endingBalance >= 0) {
                hudEndingBalance = "$" + endingBalance;
            } else {
                hudEndingBalance = "-$" + abs(endingBalance);
            }

            Log.v("refreshInfo", "Loop: mTransactions calculating revenues = " + hudRevAmt + ", expenses = " + hudExpAmt + ", & ending balance = " + hudEndingBalance);
        }

        //This Bundle is sent to the HUD
        Bundle hudBundle = new Bundle();
        hudBundle.putString("startBalance", hudtodaysBalance);
        hudBundle.putInt("todayYear", mTodayYear);
        hudBundle.putInt("todayMonth", mTodayMonth);
        hudBundle.putInt("todayDay", mTodayDay);
        hudBundle.putString("datePicker", "" + (mDatePicker.get(Calendar.MONTH) +1) + "/" + mDatePicker.get(Calendar.DAY_OF_MONTH) + "/" + mDatePicker.get(Calendar.YEAR) + "");
        hudBundle.putString("revenue", hudRevAmt);
        hudBundle.putString("expense", hudExpAmt);
        hudBundle.putString("endBalance", hudEndingBalance);
        FragmentHud hudFragBundle = new FragmentHud();
        hudFragBundle.setArguments(hudBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.hudFragMain, hudFragBundle).commit();
        Log.v("refreshInfo", "hudBundle sent to Hud Fragment: hudBundle = " + hudBundle);

        //This Bundle is sent to the Revenues Recycler
        Bundle revBundle = new Bundle();
        revBundle.putParcelableArrayList("revenues", mRevenues);
        revBundle.putParcelableArrayList("transactions", mTransactions);
        FragmentDetailRevenues myRevenueObj = new FragmentDetailRevenues();
        myRevenueObj.setArguments(revBundle);
        //Pass the bundle to the FragmentHud
        getSupportFragmentManager().beginTransaction().replace(R.id.RevenueView, myRevenueObj).commit();
        Log.v("refreshInfo", "revBundle sent to Revenue Fragment: myRevenueObj = " + myRevenueObj);

        Bundle expBundle = new Bundle();
        expBundle.putParcelableArrayList("expenses", mExpenses);
        FragmentDetailExpenses myExpenseObj = new FragmentDetailExpenses();
        myExpenseObj.setArguments(expBundle);
        //Pass the bundle to the FragmentHud
        getSupportFragmentManager().beginTransaction().replace(R.id.ExpenseView, myExpenseObj).commit();
        Log.v("refreshInfo", "expBundle sent to Expense Fragment: myExpenseObj = " + myExpenseObj);

        /*
        String gTransactions = gson.toJson(mTransactions);
        writeTransactionsToFile(gTransactions, this);
        Log.v("refreshInfo", "Json: gTransactions written to file");
        Log.v("refreshInfo", "Json: gTransactions = " + gTransactions);
        */
    }

    private void updateTransactions() {
        try {
            String gTransactions;
            List<Integer> arr;
            Log.v("TAG", "updateTransactions: method accessed");
            //RECEIVE DATA VIA INTENT
            Intent intent = getIntent();
            int tPosition = Integer.valueOf(intent.getStringExtra("tPosition"));
            String transmit = intent.getStringExtra("transmit");
            String editorTitle = intent.getStringExtra("editorTitle");
            int editorAmount = intent.getIntExtra("editorAmount", 0);
            Log.v("TAG", "updateTransactions: Intent Data recieved : intent = " + intent + ", tPosition = " + tPosition + ", transmit = " + transmit + ", editorTitle = " + editorTitle + ", editorAmount = " + editorAmount);

            switch (transmit) {
                case "paidRevenue":
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    mTransactions.get(tPosition).setTransactionPayState("paid");
                    Log.v("TAG", "updateTransactions: transaction at (" + tPosition + ") set to" + mTransactions.get(tPosition).getTransactionPayState());
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "updateTransactions: Json: gTransactions written to file");
                    Log.v("TAG", "updateTransactions: Json: gTransactions = " + gTransactions);
                    refreshInfo();
                    break;
                case "deleteRevenue":
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    Log.v("TAG", "updateTransactions: transaction  at (" + tPosition + "):  " + mTransactions.get(tPosition).getTransactionTitle() + " was deleted");
                    mTransactions.remove(tPosition);
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "Json: gTransactions written to file");
                    Log.v("TAG", "Json: gTransactions = " + gTransactions);
                    refreshInfo();
                    break;
                case "deleteAllRevenue":
                    arr = new ArrayList<>();
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    for (int t = 0; t < mTransactions.size(); t++) {

                        Log.v("TAG", "updateTransactions: Loop : refresh details conditional result " + " " + ((Objects.equals(mTransactions.get(t).getTransactionType(), "revenue")) + " " + !(mTransactions.get(t).getTransactionDate().before(mDatePicker)) + " " + (((mTransactions.get(t).getTransactionTitle()).equals(editorTitle)) + " " + ((mTransactions.get(t).getTransactionAmount()) == (editorAmount)))));
                        if ((Objects.equals(mTransactions.get(t).getTransactionType(), "revenue")) && !(mTransactions.get(t).getTransactionDate().before(mDatePicker)) && ((mTransactions.get(t).getTransactionTitle()).equals(editorTitle)) && (((mTransactions.get(t).getTransactionAmount()) == (editorAmount)))) {
                            arr.add(t);
                            Log.v("TAG", "updateTransactions: arr  at (" + arr + "):  " );
                        }
                    }
                    Collections.sort(arr, Collections.reverseOrder());
                    Log.v("TAG", "updateTransactions: arr  at (" + arr + "):  " );
                    for (int t = 0; t < arr.size(); t++) {
                        Log.v("TAG", "updateTransactions: arr.get(t)  at (" + arr.get(t) + "):  " );
                        int x = arr.get(t);
                        mTransactions.remove(x);
                        }
                    Log.v("TAG", "updateTransactions: arr.get(t) mTransactions new (" + mTransactions + "):  " );
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "updateTransactions: Json: gTransactions written to file");
                    Log.v("TAG", "updateTransactions: Json: gTransactions = " + gTransactions);
                    refreshInfo();
                    break;
                case "paidExpense":
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    mTransactions.get(tPosition).setTransactionPayState("paid");
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "updateTransactions: Json: gTransactions written to file");
                    Log.v("TAG", "updateTransactions: Json: gTransactions = " + gTransactions);
                    refreshInfo();

                    break;
                case "deleteExpense":
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    Log.v("TAG", "updateTransactions: transaction  at (" + tPosition + "):  " + mTransactions.get(tPosition).getTransactionTitle() + " was deleted");
                    mTransactions.remove(tPosition);
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "updateTransactions: Json: gTransactions written to file");
                    Log.v("TAG", "updateTransactions: Json: gTransactions = " + gTransactions);
                    refreshInfo();
                    break;
                case "deleteAllExpense":
                    arr = new ArrayList<>();
                    Log.v("TAG", "updateTransactions: transmit = " + transmit);
                    for (int t = 0; t < mTransactions.size(); t++) {
                        Log.v("TAG", "updateTransactions: Loop : refresh details conditional result " + " " + ((Objects.equals(mTransactions.get(t).getTransactionType(), "expense")) + " " + !(mTransactions.get(t).getTransactionDate().before(mDatePicker)) + " " + (((mTransactions.get(t).getTransactionTitle()).equals(editorTitle)) + " " + ((mTransactions.get(t).getTransactionAmount()) == (editorAmount)))));
                        if ((Objects.equals(mTransactions.get(t).getTransactionType(), "expense")) && !(mTransactions.get(t).getTransactionDate().before(mDatePicker)) && ((mTransactions.get(t).getTransactionTitle()).equals(editorTitle)) && (((mTransactions.get(t).getTransactionAmount()) == (editorAmount)))) {
                            arr.add(t);
                            Log.v("TAG", "updateTransactions: arr  at (" + arr + "):  " );
                        }
                    }
                    Collections.sort(arr, Collections.reverseOrder());
                    Log.v("TAG", "updateTransactions: arr  at (" + arr + "):  " );
                    for (int t = 0; t < arr.size(); t++) {
                        Log.v("TAG", "updateTransactions: arr.get(t)  at (" + arr.get(t) + "):  " );
                        int x = arr.get(t);
                        mTransactions.remove(x);
                    }
                    Log.v("TAG", "updateTransactions: arr.get(t) mTransactions new (" + mTransactions + "):  " );
                    gTransactions = gson.toJson(mTransactions);
                    writeTransactionsToFile(gTransactions, this);
                    Log.v("TAG", "updateTransactions: Json: gTransactions written to file");
                    Log.v("TAG", "updateTransactions: Json: gTransactions = " + gTransactions);
                    refreshInfo();
                    break;

            }
        } catch (NullPointerException e) {
            Log.v("updateTransactions", "updateTransactions: NullPointerException Triggered");
        } catch (NumberFormatException e) {
            Log.v("updateTransactions", "updateTransactions: NumberFormatException Triggered ");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            Log.v("onSaveInstanceState", "Method Triggered");
            outState.putParcelableArrayList("transactions", mTransactions);
            outState.putParcelableArrayList("dailyBalances", mDailyBalances);
            outState.putInt("mDatePickerYear", mDatePicker.get(Calendar.YEAR));
            outState.putInt("mDatePickerMonth", mDatePicker.get(Calendar.MONTH));
            outState.putInt("mDatePickerDay", mDatePicker.get(Calendar.DAY_OF_MONTH));
        } catch (IndexOutOfBoundsException e) {
            Log.v("onSaveInstanceState", "IndexOutOfBoundsException Triggered");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v("onRestoreInstanceState", "Method Triggered");
        super.onRestoreInstanceState(savedInstanceState);
        mTransactions = savedInstanceState.getParcelableArrayList("transactions");
        mDailyBalances = savedInstanceState.getParcelableArrayList("dailyBalances");
    }

    private void writeTransactionsToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("configure.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v("writeToFile", "method triggered " + outputStreamWriter);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readTransactionsFromFile(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("configure.txt");
            Log.v("readFromFile", "method triggered " + inputStream);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeDatePickerToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("DatePicker.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v("writeToFile", "method triggered " + outputStreamWriter);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readDatePickerFromFile(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("DatePicker.txt");
            Log.v("readFromFile", "method triggered " + inputStream);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeBalanceToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("Balance.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v("writeToFile", "method triggered " + outputStreamWriter);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readBalanceFromFile(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("Balance.txt");
            Log.v("readFromFile", "method triggered " + inputStream);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());

        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}

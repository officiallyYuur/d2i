package org.yuur.d2i;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.yuur.d2i.R;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class FragmentHud extends Fragment {
    //Debugger tool
    //private static final String TAG = "outActivity";
    //Log.v(TAG, ""+myObj.getArguments().getString("startBalance")+"");

    private static TextView tvStartBalance;
    private static TextView tvStartBalanceAmt;
    private static TextView tvRevenue;
    private static TextView tvRevenueAmt;
    private static TextView tvExpense;
    private static TextView tvExpenseAmt;
    private static TextView tvEndBalance;
    private static TextView tvEndBalanceAmt;

    public String startBalance;
    public String revenue;
    public String expense;
    public String endBalance;
    String datePicker;
    int  todayDay;
    int  todayMonth;
    int  todayYear;

    public FragmentHud() {
        // Required empty public constructor
    }

    //Executes Before the View is Inflated
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Checks for a null bundle, if the bundle isn't null, it updates the layout text views.
         */
        if (getArguments() != null) {
            startBalance = getArguments().getString("startBalance");
            revenue = getArguments().getString("revenue");
            expense = getArguments().getString("expense");
            endBalance = getArguments().getString("endBalance");
            todayDay  = getArguments().getInt("todayDay");
            todayMonth = getArguments().getInt("todayMonth");
            todayYear = getArguments().getInt("todayYear");
            datePicker = getArguments().getString("datePicker");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.fragment_hud, container, false);

        //initialize TextViews
        tvStartBalance =  view.findViewById(R.id.tvStartBalance);
        tvStartBalanceAmt =  view.findViewById(R.id.tvStartBalanceAmt);
        tvRevenue =  view.findViewById(R.id.tvRevenue);
        tvRevenueAmt =  view.findViewById(R.id.tvRevenueAmt);
        tvExpense =  view.findViewById(R.id.tvExpense);
        tvExpenseAmt =  view.findViewById(R.id.tvExpenseAmt);
        tvEndBalance =  view.findViewById(R.id.tvEndBalance);
        tvEndBalanceAmt =  view.findViewById(R.id.tvEndBalanceAmt);


        //update TextViews
        tvStartBalance.setText("Today's Starting Balance");
        tvStartBalanceAmt.setText(startBalance);
        Log.v("TAG", " startBalance = " + startBalance);

        tvRevenue.setText("Revenues Summary");
        tvRevenueAmt.setText("+ " +revenue);
        tvExpense.setText("Expenses Summary");
        tvExpenseAmt.setText("- " + expense);
        tvEndBalance.setText("Final Balance \n on " + datePicker);
        tvEndBalanceAmt.setText(endBalance);
        return view;
    }
}



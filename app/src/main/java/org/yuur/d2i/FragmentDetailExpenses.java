package org.yuur.d2i;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

public class FragmentDetailExpenses extends Fragment {
    private List<Expense> mItemList;


    public FragmentDetailExpenses(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_detail_expenses, container, false);
        // RecyclerView
        RecyclerView rv =  rootview.findViewById(R.id.rcyExpenses);
        rv.setHasFixedSize(true);
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        //pass (mItemsList array, the fragments context) to the recycler adapter
        RvaExpenses adapter = new RvaExpenses(Getdata(), this);
        rv.setAdapter(adapter);
        //pass this view to the MenuTab
        return rootview;
    }

    public List<Expense>  Getdata() {
        List<Expense> mItemList = new ArrayList<>();
        try {
            mItemList = getArguments().getParcelableArrayList("expenses");
            //Log.v("revenues message", "" + getArguments().getParcelableArrayList("expenses"));
        } catch(NullPointerException e){

        }
        return mItemList;
    }
}


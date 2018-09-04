package org.yuur.d2i;

import android.content.Intent;
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
import org.yuur.d2i.R;

public class FragmentDetailRevenues extends Fragment {
    private List<Revenue> mItemList;
    List<Transaction> rTransactions;



    public FragmentDetailRevenues(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_detail_revenues, container, false);
        // RecyclerView
        RecyclerView rv =  rootview.findViewById(R.id.rcyRevenues);
        rv.setHasFixedSize(true);
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        //pass (mItemsList array, the fragments context) to the recycler adapter
        RvaRevenues adapter = new RvaRevenues(Getdata(), this);
        rv.setAdapter(adapter);
        //pass this view to the MenuTab
        return rootview;
    }

    public List<Revenue>  Getdata() {
        List<Revenue> mItemList = new ArrayList<>();
        //List<Transaction> rTransactions = new ArrayList<>();
        try {
            mItemList = getArguments().getParcelableArrayList("revenues");
            //Log.v("revenues message", "" + getArguments().getParcelableArrayList("revenues"));
        } catch(NullPointerException e){

        }

        //rTransactions = getArguments().getParcelableArrayList("transactions");
        return mItemList;
    }

}


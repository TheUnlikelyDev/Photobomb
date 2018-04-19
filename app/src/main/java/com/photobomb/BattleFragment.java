package com.photobomb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class BattleFragment extends Fragment {


   static BattleFragment newInstance(int position){
       BattleFragment fragment = new BattleFragment();
       Bundle args = new Bundle();

       args.putInt("KEY_POSITION",position);
       fragment.setArguments(args);

       return fragment;

   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


       View result = inflater.inflate(R.layout.battle,container,false);

        return result;
    }

}

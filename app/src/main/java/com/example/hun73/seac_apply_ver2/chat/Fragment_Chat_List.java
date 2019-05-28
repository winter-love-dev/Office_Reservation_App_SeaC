package com.example.hun73.seac_apply_ver2.chat;


import android.content.Context;
import android.os.Bundle;
//import android.app.Fragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hun73.seac_apply_ver2.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class Fragment_Chat_List extends Fragment
{
    private static final String TAG = "Fragment_Chat_List: ";
    private View View_Chat_List;
    private Context Context_Chat_List;

    String getId;

    public Fragment_Chat_List()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Chat_List = inflater.inflate(R.layout.fragment_fragment_chat_list, container, false);

        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        return View_Chat_List;
    }


}

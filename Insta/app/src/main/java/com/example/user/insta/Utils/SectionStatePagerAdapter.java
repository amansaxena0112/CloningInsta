package com.example.user.insta.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by USER on 11/10/2017.
 */

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter{

    private final List<Fragment> mfragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentName = new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mfragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mfragmentList.add(fragment);
        mFragments.put(fragment, mfragmentList.size()-1);
        mFragmentNumbers.put(fragmentName, mfragmentList.size()-1);
        mFragmentName.put(mfragmentList.size()-1, fragmentName);
    }
    public  Integer getFragmentNumber(String fragmentName){
        if(mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        }else {
            return null;
        }
    }
    public  Integer getFragmentNumber(Fragment fragment){
        if(mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        }else {
            return null;
        }
    }
    public  String getFragmentName(Integer fragmentNumber){
        if(mFragmentName.containsKey(fragmentNumber)){
            return mFragmentName.get(fragmentNumber);
        }else {
            return null;
        }
    }

}

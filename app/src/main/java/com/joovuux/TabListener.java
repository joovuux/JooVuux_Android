package com.joovuux;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

import ua.net.lsoft.joovuux.R;

public class TabListener implements ActionBar.TabListener {

    private Fragment fragment;

    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.mainFrame, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}
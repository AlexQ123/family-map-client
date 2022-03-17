package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.family_map_app.R;
import com.example.family_map_app.activity.fragment.LoginFragment;
import com.example.family_map_app.serverdata.DataCache;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null || (DataCache.getInstance().getAuthToken() == null)) {
            fragment = createLoginFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentFrameLayout, fragment).commit();
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
}
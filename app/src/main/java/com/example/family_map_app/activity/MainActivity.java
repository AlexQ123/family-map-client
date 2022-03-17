package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.family_map_app.R;
import com.example.family_map_app.activity.fragment.LoginFragment;
import com.example.family_map_app.activity.fragment.MapFragment;
import com.example.family_map_app.serverdata.DataCache;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

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
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifySignIn() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, fragment)
                .commit();
    }

}
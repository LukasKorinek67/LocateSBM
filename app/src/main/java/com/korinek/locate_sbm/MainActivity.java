package com.korinek.locate_sbm;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.korinek.locate_sbm.databinding.ActivityMainBinding;
import com.korinek.locate_sbm.utils.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // starting splash screen
        SplashScreen.installSplashScreen(this);

        // theme load and set
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        int selectedTheme = sharedPreferencesHelper.getTheme();
        setTheme(selectedTheme);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
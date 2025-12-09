package com.example.todo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.todo.fragments.CalendarFragment;
import com.example.todo.fragments.HomeFragment;
import com.example.todo.fragments.KnowledgeFragment;
import com.example.todo.fragments.MessagesFragment;
import com.example.todo.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_container);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sprawdź czy użytkownik jest zalogowany
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initUI();
    }

    private void initUI() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        // Ustaw domyślny fragment (Home)
        loadFragment(new HomeFragment());
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_calendar) {
                fragment = new CalendarFragment();
            } else if (itemId == R.id.nav_messages) {
                fragment = new MessagesFragment();
            } else if (itemId == R.id.nav_knowledge) {
                fragment = new KnowledgeFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // Metoda do przełączania zakładek z innych fragmentów (np. z HomeFragment)
    public void switchToTab(int tabId) {
        bottomNavigation.setSelectedItemId(tabId);
    }
}

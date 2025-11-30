package com.example.todo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.todo.viewmodel.TaskViewModel;

import java.util.List;

//ekran apk
public class  SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        MenuItem searchItem = menu.findItem(R.id.action_search_main);
        settingsItem.setVisible(false);
        searchItem.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//wyswietlanie i zarzadznie ust
    public static class SettingsFragment extends PreferenceFragmentCompat {

        private TaskViewModel viewModel;
        private List<String> categoryList;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            viewModel = new ViewModelProvider(this, new TaskViewModelFactory(requireActivity().getApplication())).get(TaskViewModel.class);

            viewModel.getCategoryList().observe(this, categories -> {
                categoryList = categories;
            });

            ListPreference categoryPreference = findPreference("category_list");
            categoryPreference.setOnPreferenceClickListener(preference -> {
                categoryPreference.setEntries(categoryList.toArray(new CharSequence[0]));
                categoryPreference.setEntryValues(categoryList.toArray(new CharSequence[0]));
                return true;
            });

            categoryPreference.setSummaryProvider(preference -> {
                if (categoryPreference.getValue() == null) {
                    return "none selected";
                } else {
                    return categoryPreference.getValue();
                }
            });
        }
    }
}
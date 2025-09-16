package com.example.listycity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    ListView cityList;
    ArrayAdapter<String> cityAdapter;
    ArrayList<String> dataList;

    Button addButton, deleteButton;
    int selectedPosition = -1; // track selected city

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        cityList = findViewById(R.id.city_list);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);

        // Seed data
        String[] cities = {"Edmonton", "Toronto", "Vancouver", "Calgary", "Montreal"};
        dataList = new ArrayList<>(Arrays.asList(cities));

        // Adapter: use custom row layout (content.xml)
        cityAdapter = new ArrayAdapter<>(this, R.layout.content, R.id.content_view, dataList);
        cityList.setAdapter(cityAdapter);

        // Handle row taps: remember selected city, enable delete
        cityList.setOnItemClickListener(this::onItemClick);

        // Add city
        addButton.setOnClickListener(v -> showAddCityDialog());

        // Delete city
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition >= 0 && selectedPosition < dataList.size()) {
                String removed = dataList.remove(selectedPosition);
                cityAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Deleted: " + removed, Toast.LENGTH_SHORT).show();

                // Reset selection
                selectedPosition = -1;
                deleteButton.setEnabled(false);
                cityList.clearChoices();
            } else {
                Toast.makeText(this, "Select a city first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCityDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter city name");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        new AlertDialog.Builder(this)
                .setTitle("Add City")
                .setView(input)
                .setPositiveButton("CONFIRM", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "City name cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Prevent duplicates (case-insensitive)
                    for (String s : dataList) {
                        if (s.equalsIgnoreCase(name)) {
                            Toast.makeText(this, "City already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    dataList.add(name);
                    cityAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Added: " + name, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        deleteButton.setEnabled(true);
        cityList.setItemChecked(position, true);
    }
}

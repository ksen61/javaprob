package com.example.treker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddHabitActivity extends AppCompatActivity {
    private EditText habitNameInput;
    private Spinner durationSpinner;
    private GridView iconGridView;
    private String selectedIcon = "";
    private FirebaseFirestore db;
    private List<Integer> iconList = Arrays.asList(
            R.drawable.priv, R.drawable.priv, R.drawable.priv,
            R.drawable.priv, R.drawable.priv, R.drawable.priv
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        habitNameInput = findViewById(R.id.habitNameInput);
        durationSpinner = findViewById(R.id.durationSpinner);
        iconGridView = findViewById(R.id.iconGridView);
        Button saveHabitButton = findViewById(R.id.saveHabitButton);
        db = FirebaseFirestore.getInstance();

        // Устанавливаем иконки в GridView
        iconGridView.setAdapter(new IconAdapter(this, iconList));
        iconGridView.setOnItemClickListener((parent, view, position, id) -> {
            selectedIcon = getResources().getResourceEntryName(iconList.get(position));
            Toast.makeText(this, "Выбрана иконка", Toast.LENGTH_SHORT).show();
        });

        saveHabitButton.setOnClickListener(v -> saveHabitToFirestore());
    }

    private void saveHabitToFirestore() {
        String habitName = habitNameInput.getText().toString().trim();
        String duration = durationSpinner.getSelectedItem().toString();

        if (habitName.isEmpty() || selectedIcon.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> habit = new HashMap<>();
        habit.put("name", habitName);
        habit.put("iconResId", selectedIcon);
        habit.put("duration", duration);
        habit.put("isCompleted", false);

        db.collection("habits").add(habit).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Привычка добавлена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddHabitActivity.this, UserActivity.class));
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}

package com.example.treker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;
    private FirebaseFirestore db;
    private boolean isAddButtonVisible = false;
    private View addHabitButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        habitList = new ArrayList<>();
        habitAdapter = new HabitAdapter(this, habitList);
        recyclerView.setAdapter(habitAdapter);

        addHabitButton = findViewById(R.id.addHabitButton);
        settingsButton = findViewById(R.id.settingsButton);

        loadHabitsFromFirestore();

        settingsButton.setOnClickListener(v -> toggleAddHabitButton());
        addHabitButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });

    }

    private void loadHabitsFromFirestore() {
        db.collection("habits").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                habitList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    String name = document.getString("name");
                    String iconResId = document.getString("iconResId");
                    boolean isCompleted = document.getBoolean("isCompleted") != null && document.getBoolean("isCompleted");

                    habitList.add(new Habit(id, name, iconResId, isCompleted));
                }
                habitAdapter.notifyDataSetChanged();
            }
        });
    }

    private void toggleAddHabitButton() {
        if (isAddButtonVisible) {
            fadeOut(addHabitButton);
        } else {
            fadeIn(addHabitButton);
        }
        isAddButtonVisible = !isAddButtonVisible;
    }

    private void fadeIn(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(300);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeIn);
    }

    private void fadeOut(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(300);
        view.startAnimation(fadeOut);
        view.setVisibility(View.GONE);
    }
}

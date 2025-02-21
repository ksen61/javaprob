package com.example.treker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habitList;
    private Context context;
    private FirebaseFirestore db;

    public HabitAdapter(Context context, List<Habit> habitList) {
        this.context = context;
        this.habitList = habitList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitName.setText(habit.getName());

        // Загружаем иконку по названию ресурса
        int iconResId = context.getResources().getIdentifier(habit.getIconResId(), "drawable", context.getPackageName());
        holder.habitIcon.setImageResource(iconResId);

        // Отображаем галочку, если привычка выполнена
        holder.habitCompleted.setVisibility(habit.isCompleted() ? View.VISIBLE : View.GONE);

        // Долгий клик - подтверждение выполнения
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Отметить выполнение")
                    .setMessage("Хотите отметить день как выполненный?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        habit.setCompleted(true);
                        notifyItemChanged(position);
                        updateHabitInFirestore(habit);
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        ImageView habitIcon, habitCompleted;
        TextView habitName;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitIcon = itemView.findViewById(R.id.habitIcon);
            habitName = itemView.findViewById(R.id.habitName);
            habitCompleted = itemView.findViewById(R.id.habitCompleted);
        }
    }

    // Метод обновления статуса в Firestore
    private void updateHabitInFirestore(Habit habit) {
        db.collection("habits").document(habit.getId())
                .update("isCompleted", true);
    }
}

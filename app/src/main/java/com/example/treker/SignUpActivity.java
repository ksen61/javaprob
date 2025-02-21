package com.example.treker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameInput, surnameInput, emailInput, passwordInput, phoneInput;
    private Button signUpButton;
    private TextView signInText; // Добавляем ссылку на TextView для входа
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Инициализация FirebaseAuth и Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Инициализация элементов UI
        nameInput = findViewById(R.id.nameInput);
        surnameInput = findViewById(R.id.surnameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneInput);
        signUpButton = findViewById(R.id.signUpButton);
        signInText = findViewById(R.id.signInText); // Инициализируем TextView для входа

        // Кнопка регистрации
        signUpButton.setOnClickListener(v -> registerUser());

        // Устанавливаем TextWatcher на поле для телефона
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Если поле пустое и начинается не с 8, добавляем 8
                if (editable.length() == 0 || editable.charAt(0) != '8') {
                    phoneInput.setText("8" + editable.toString());
                    phoneInput.setSelection(phoneInput.getText().length()); // Устанавливаем курсор в конец
                }
            }
        });

        // Обработчик клика по TextView для входа
        signInText.setOnClickListener(v -> {
            // Переходим на экран входа (SignInActivity)
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String surname = surnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Проверка имени и фамилии (только буквы)
        if (name.isEmpty() || !name.matches("[а-яА-Я]+")) {
            Toast.makeText(this, "Введите корректное имя", Toast.LENGTH_SHORT).show();
            return;
        }
        if (surname.isEmpty() || !surname.matches("[а-яА-Я]+")) {
            Toast.makeText(this, "Введите корректную фамилию", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка email
        if (email.isEmpty() || !isValidEmail(email)) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка пароля
        if (password.isEmpty() || password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            Toast.makeText(this, "Пароль должен быть не менее 8 символов и содержать латинские буквы и цифры", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка телефона
        if (!phone.isEmpty() && (!phone.startsWith("8") || phone.length() != 11 || !phone.matches("\\d+"))) {
            Toast.makeText(this, "Телефон должен начинаться с 8 и содержать 11 цифр", Toast.LENGTH_SHORT).show();
            return;
        }

        // Регистрируем пользователя в Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Получаем текущего пользователя
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Создаем объект с дополнительными данными, включая роль "user"
                            UserProfile userProfile = new UserProfile(name, surname, email, phone, "user");

                            // Сохраняем данные в Firestore
                            db.collection("users").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                                        // Переход на главный экран
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Ошибка сохранения данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Метод для проверки email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }
}

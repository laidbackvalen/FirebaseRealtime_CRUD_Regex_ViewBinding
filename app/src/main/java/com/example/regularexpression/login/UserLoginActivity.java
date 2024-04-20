package com.example.regularexpression.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regularexpression.CRUD.CreateUserDataActivity;
import com.example.regularexpression.R;
import com.example.regularexpression.databinding.ActivityUserLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserLoginActivity extends AppCompatActivity {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private ActivityUserLoginBinding binding; // Generated binding class
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //viewBinding
        binding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.registrationUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, UserRegistrationActivity.class));
            }
        });
        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = binding.txtInpEdTxtEmail.getText().toString().trim();
                String password = binding.txtInpEdTxtPassword.getText().toString().trim();
                if (!emailConditions(email)) { //!emailConditions(email) would evaluate to false
                    // Do something if the email conditions are NOT met
                    binding.txtInpEdTxtEmail.setError("Wrongly added email");
                } else if (emailConditions(email)) {
                    if (!passwordValidator(password)) {
                        binding.txtInpEdTxtPassword.setError("Password must:\n" +
                                "- Be at least 8 characters long\n" +
                                "- Contain at least one uppercase letter\n" +
                                "- Contain at least one lowercase letter\n" +
                                "- Contain at least one digit\n" +
                                "- Contain at least one special character (@$!%*?&)");
                    } else {
                        loginUser(email, password);
                    }
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null;
                    if (firebaseUser.isEmailVerified()) {
                        Intent intent =new Intent(UserLoginActivity.this, CreateUserDataActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserLoginActivity.this, "Check if you have verified Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserLoginActivity.this, "Login Failed!"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean passwordValidator(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
//        boolean matchValue = matcher.matches();
        return matcher.matches();
    }

    private boolean emailConditions(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
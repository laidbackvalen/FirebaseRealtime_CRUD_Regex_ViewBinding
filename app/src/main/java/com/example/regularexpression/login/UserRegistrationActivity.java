package com.example.regularexpression.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regularexpression.R;
import com.example.regularexpression.databinding.ActivityUserRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationActivity extends AppCompatActivity {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private ActivityUserRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.txtInpEdTxtEmailRegistration.getText().toString().trim();
                String password = binding.txtInpEdTxtPasswordRegistration.getText().toString().trim();
                if (!emailConditions(email)) { //!emailConditions(email) would evaluate to false
                    // Do something if the email conditions are NOT met
                    binding.txtInpEdTxtEmailRegistration.setError("Wrongly added email");
                } else if (emailConditions(email)) {
                    if (!passwordValidator(password)) {
                        binding.txtInpEdTxtPasswordRegistration.setError("Password must:\n" +
                                "- Be at least 8 characters long\n" +
                                "- Contain at least one uppercase letter\n" +
                                "- Contain at least one lowercase letter\n" +
                                "- Contain at least one digit\n" +
                                "- Contain at least one special character (@$!%*?&)");
                    } else {
                        createUser(email, password);
                        finish();
                    }
                }
            }
        });
    }

    private void createUser(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserRegistrationActivity.this, "An Email verification link has been sent to your Email Address", Toast.LENGTH_SHORT).show();
                            Toast.makeText(UserRegistrationActivity.this, "Click on it to verify and login", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserRegistrationActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserRegistrationActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();

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
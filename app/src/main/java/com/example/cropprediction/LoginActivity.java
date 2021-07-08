package com.example.cropprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private TextInputLayout email;
    private TextInputLayout password;
    private CardView loginButton;
    private FirebaseAuth firebaseAuth;
    private String TAG = "Login Activity";
    private TextView register;
    private String emailValue;
    private String passwordValue;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    private TextView cardText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appbar_color));
        }
        setContentView(R.layout.activity_login);


        init();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmailEmpty() | !isPasswordEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    cardText.setVisibility(View.GONE);

                    userLogin(new LoggedInCallback() {
                        @Override
                        public void onSignedIn(Intent intent) {
                            startActivity(intent);
                        }
                    });
                }

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                ResetPasswordDialog resetPasswordDialog=new ResetPasswordDialog(LoginActivity.this,firebaseAuth);
                resetPasswordDialog.startLoadingDialog();
            }
        });

    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_buttton);
        register = findViewById(R.id.sign_up);
        forgotPassword=findViewById(R.id.forgot_password);
        progressBar=findViewById(R.id.progressBar);
        cardText=findViewById(R.id.cardText);
        cardText.setVisibility(View.VISIBLE);
    }
    private void fetchValues(){
        emailValue=email.getEditText().getText().toString();
        passwordValue=password.getEditText().getText().toString();

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            //logic
            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Logged in Succesfully "+firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
                Map<String,Boolean> map=new HashMap<>();
                map.put("isEmailVerified",true);
                firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).set(map, SetOptions.merge());
            }
        }
    }

    private boolean isEmailEmpty() {
        if (email.getEditText().getText().toString().equals("")) {
            email.setError("Email is required");
            return true;
        }
        return false;
    }

    private boolean isPasswordEmpty() {
        if (password.getEditText().getText().toString().equals("")) {
            password.setError("Password is required");
            return true;
        }
        return false;
    }
    private void userLogin(LoggedInCallback loggedInCallback){
        fetchValues();
        firebaseAuth.signInWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                    Map<String,Boolean> map=new HashMap<>();
                    map.put("isEmailVerified",true);
                    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).set(map, SetOptions.merge());

                    Toast.makeText(LoginActivity.this, "Logged in Succesfully", Toast.LENGTH_SHORT).show();
                    System.out.println("logged in");
                    loggedInCallback.onSignedIn(intent);
                }
                else{
                    System.out.println("couldn't logg in");
                    Toast.makeText(LoginActivity.this, "Oops !! Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface LoggedInCallback{
        void onSignedIn(Intent intent);
    }
}
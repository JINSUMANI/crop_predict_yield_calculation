package com.example.cropprediction;

import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    final String TAG=".SignupActivity";

    TextInputLayout username,email,password,phone;
    CardView signup_button;
    String usernameValue,emailValue,passwordValue,phoneValue;
    FirebaseUser user;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appbar_color));
        }
        init();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchValues();

                if(!isEmailEmpty() | !isUsernameEmpty() | !isPasswordEmpty()  | !isPhoneEmpty()){
                    registerUser(new SignUpCallback() {
                        @Override
                        public void OnSignupCompleted(FirebaseUser firebaseUser) {
                            sendEmailVerification(firebaseUser, new VerificationEmailCallback() {
                                @Override
                                public void onEmailSent() {
                                    System.out.println("eMAIL SENT SUCCESFULLY");
                                }
                            });
                        }
                    });

                }
            }
        });

    }

    private void init() {

        username=findViewById(R.id.username_signup);
        email=findViewById(R.id.email_signup);
        password=findViewById(R.id.password_signup);
        phone=findViewById(R.id.phone_signup);
        signup_button=findViewById(R.id.signup_buttton);



    }
    private void fetchValues(){
        usernameValue=username.getEditText().getText().toString().trim();
        emailValue=email.getEditText().getText().toString().trim();
        passwordValue=password.getEditText().getText().toString().trim();
        phoneValue=phone.getEditText().getText().toString().trim();
    }

    private boolean isUsernameEmpty(){
        if(usernameValue.equals("")){
            username.setError("Username is required");
            return true;
        }
        return false;
    }

    private boolean isEmailEmpty(){
        if(emailValue.equals("")){
            email.setError("Email is required");
            return true;
        }
        return false;
    }
    private boolean isPasswordEmpty(){
        if(passwordValue.equals("")) {
            password.setError("Password is required");
            return true;
        }
        return false;
    }

    private boolean isPhoneEmpty(){
        if(phoneValue.equals("")) {
            password.setError("Phone number is required");
            return true;
        }
        return false;

    }

    private void registerUser(SignUpCallback signUpCallback){
        EmailDialog emailDialog=new EmailDialog(SignupActivity.this);
        firebaseAuth= FirebaseAuth.getInstance();
        System.out.println("print inside register user");
        Toast.makeText(this, "inside register user", Toast.LENGTH_SHORT).show();
        emailDialog.startLoadingDialog();
        firebaseAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    emailDialog.stopLoadingDialog();
                    user=firebaseAuth.getCurrentUser();
                    Toast.makeText(SignupActivity.this, "inside task success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignupActivity.this, "Ooops !! Looks like "+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Account creation failed "+task.getException());
                    System.out.println("account creation failed");
                }
                signUpCallback.OnSignupCompleted(user);
            }
        });

    }
    public void sendEmailVerification(FirebaseUser firebaseUser, VerificationEmailCallback verificationEmailCallback){
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                EmailDialog emailDialog1=new EmailDialog(SignupActivity.this);
                emailDialog1.changeLayoutToEmailIntent();
                Toast.makeText(SignupActivity.this, "An Email is sent to you , Click on the link to verify it and start using", Toast.LENGTH_SHORT).show();
                FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                UserModel user=new UserModel(usernameValue,emailValue,phoneValue);
                firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);
                Log.d(TAG,"Email is sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("inside task failed");
                Toast.makeText(SignupActivity.this, "Something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.getMessage());
            }
        });
        verificationEmailCallback.onEmailSent();
    }
    public interface SignUpCallback{
        void OnSignupCompleted(FirebaseUser firebaseUser);
    }
    public interface VerificationEmailCallback{
        void onEmailSent();
    }
}
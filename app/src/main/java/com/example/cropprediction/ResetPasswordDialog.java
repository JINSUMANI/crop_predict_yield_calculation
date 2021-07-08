package com.example.cropprediction;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordDialog {
    Activity activity;
    AlertDialog alertDialog;
    FirebaseAuth firebaseAuth;
    ResetPasswordDialog(Activity activity, FirebaseAuth firebaseAuth){
        this.activity=activity;
        this.firebaseAuth=firebaseAuth;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password_layout,null);
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog=builder.create();
        alertDialog.show();
        CardView reset=view.findViewById(R.id.reset_password);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout email=view.findViewById(R.id.emailreset);
                String emailValue=email.getEditText().getText().toString();
                firebaseAuth.sendPasswordResetEmail(emailValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity, "An email has sent your id for resetting your password", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Oops !! Something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

}

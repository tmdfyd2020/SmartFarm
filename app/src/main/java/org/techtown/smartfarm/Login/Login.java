package org.techtown.smartfarm.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.techtown.smartfarm.Main.MainActivity;
import org.techtown.smartfarm.R;

public class Login extends AppCompatActivity {

    EditText login_Email, login_Password;
    Button loginBtn;
    TextView createAcc;
    ProgressDialog customProgressDialog;

    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_Email = findViewById(R.id.login_email);
        login_Password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_loginBtn);
        createAcc = findViewById(R.id.register_signUp);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fAuth = FirebaseAuth.getInstance();

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = login_Email.getText().toString();
                String mPassword = login_Password.getText().toString();

                if(mEmail.isEmpty() || mPassword.isEmpty()){
                    Toast.makeText(Login.this, "모든 요소를 입력해주십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }

                customProgressDialog.show();

                fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "로그인에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "로그인에 실패하였습니다. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}

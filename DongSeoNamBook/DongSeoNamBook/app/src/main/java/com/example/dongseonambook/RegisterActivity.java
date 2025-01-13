package com.example.dongseonambook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email,password,pwdCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        pwdCheck=findViewById(R.id.passwordCheck);
        Button finBtn=findViewById(R.id.finishBtn);
        finBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail=email.getText().toString().trim();
                String strPwd=password.getText().toString().trim();
                String strPwdCheck=pwdCheck.getText().toString().trim();

                if (strEmail.isEmpty()) {
                    email.setError("이메일을 입력해주세요");
                    email.requestFocus();
                    return;
                }
                if(!strEmail.contains("@")) {
                    email.setError("이메일의 형식이 아닙니다.");
                    email.requestFocus();
                    return;
                }
                if (strPwd.isEmpty()) {
                    password.setError("비밀번호를 입력해주세요");
                    password.requestFocus();
                    return;
                }
                if (strPwd.length() < 6) {
                    password.setError("비밀번호는 최소 6자리 이상이어야 합니다");
                    password.requestFocus();
                    return;
                }
                if (!(strPwd.equals(strPwdCheck))) {
                    pwdCheck.setError("입력한 비밀번호가 일치하지 않습니다.");
                    pwdCheck.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 계정 생성 성공
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid(); // Firebase UID
                                    }
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Exception exception = task.getException();
                                    if(exception instanceof FirebaseAuthUserCollisionException){
                                        email.setError("중복된 이메일입니다!");
                                        email.requestFocus();
                                    }
                                }
                            }
                        });

            }
        });

    }
}
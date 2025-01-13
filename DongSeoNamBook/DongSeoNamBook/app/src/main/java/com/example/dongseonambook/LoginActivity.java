package com.example.dongseonambook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase 인증 초기화
        mAuth = FirebaseAuth.getInstance();

        // 레이아웃 연결
        Button reg = findViewById(R.id.regi);
        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.loginBtn);

        // 회원가입 버튼 클릭 리스너
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭 리스너
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString().trim(); // 동적으로 값 가져오기
                String strPwd = pwd.getText().toString().trim();

                // 입력 값 검증
                if (strEmail.isEmpty()) {
                    email.setError("이메일을 입력해주세요.");
                    email.requestFocus();
                    return;
                }

                if (strPwd.isEmpty()) {
                    pwd.setError("비밀번호를 입력해주세요.");
                    pwd.requestFocus();
                    return;
                }

                // Firebase 로그인
                mAuth.signInWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 로그인 성공
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 종료
                                } else {
                                    // 로그인 실패 처리
                                    Exception exception = task.getException();

                                    // 이메일이 존재하지 않을 경우
                                    if (exception instanceof FirebaseAuthInvalidUserException) {
                                        email.setError("이메일이 존재하지 않습니다.");
                                        email.requestFocus();
                                    }
                                    // 비밀번호가 틀릴 경우
                                    else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        pwd.setError("패스워드가 틀렸습니다.");
                                        pwd.requestFocus();
                                    } else {
                                        // 기타 오류 처리
                                        Toast.makeText(LoginActivity.this, "로그인 실패: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}
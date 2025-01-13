package com.example.dongseonambook;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String strFeel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendDataButton = findViewById(R.id.feelBtns);
        sendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialog() {
        // 다이얼로그 생성
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);

        // 다이얼로그 내부 뷰 참조
        EditText dialogInput = dialog.findViewById(R.id.dialogInput);
        Button dialogButton = dialog.findViewById(R.id.dialogButton);

        // 확인 버튼 클릭 이벤트
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = dialogInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    strFeel = inputText; // 입력받은 값 저장
                    dialog.dismiss();
                    sendFeelToFastAPI(strFeel); // FastAPI로 데이터 전송
                } else {
                    Toast.makeText(MainActivity.this, "값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void sendFeelToFastAPI(String feel) {
        // Retrofit 클라이언트 생성
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // 데이터 객체 생성
        FeelData data = new FeelData(feel);

        // POST 요청
        Call<Void> call = apiService.sendFeel(data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "데이터 전송 성공!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "데이터 전송 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

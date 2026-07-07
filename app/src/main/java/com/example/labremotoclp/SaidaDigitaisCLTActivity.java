package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaidaDigitaisCLTActivity extends AppCompatActivity {
    
    private ImageView[] imageViews;
    private TextView[] textViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saida_digitais_clp);
        setTitle("SAÍDAS DIGITAIS DO CLP");
        
        imageViews = new ImageView[]{
            findViewById(R.id.imageView1), findViewById(R.id.imageView2), 
            findViewById(R.id.imageView3), findViewById(R.id.imageView4), 
            findViewById(R.id.imageView5), findViewById(R.id.imageView6), 
            findViewById(R.id.imageView7), findViewById(R.id.imageView8), 
            findViewById(R.id.imageView9), findViewById(R.id.imageView10)
        };

        textViews = new TextView[]{
            findViewById(R.id.textView1), findViewById(R.id.textView2),
            findViewById(R.id.textView3), findViewById(R.id.textView4),
            findViewById(R.id.textView5), findViewById(R.id.textView6),
            findViewById(R.id.textView7), findViewById(R.id.textView8),
            findViewById(R.id.textView9), findViewById(R.id.textView10)
        };

        // Map labels to actual ESP32 Output Pins
        String[] outputKeys = Interface.getOutputKeys();
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setText("PIN " + outputKeys[i].replace("Output_", ""));
        }
        
        lerDadosCLP();
    }

    public void ler(View view) {
        lerDadosCLP();
    }

    private void lerDadosCLP() {
        Call<String> call = RetrofitClient.getApiService().lerSaidaDigitais();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body());
                        atualizarStatus(json);
                    } catch (Exception e) {
                        Log.e("LOG", "Erro JSON", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha ao ler dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarStatus(JSONObject json) {
        String[] outputKeys = Interface.getOutputKeys();
        for (int i = 0; i < imageViews.length; i++) {
            int status = json.optInt(outputKeys[i], 0);
            if (status == 1) {
                imageViews[i].setImageResource(android.R.drawable.presence_online);
            } else {
                imageViews[i].setImageResource(android.R.drawable.presence_busy);
            }
        }
    }
}

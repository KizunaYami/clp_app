package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntradaDigitaisCLPActivity extends AppCompatActivity {

    private ImageView[] imageViews;
    private TextView tvWifi, tvHeap;
    private TextView[] tvLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using the layout with ImageViews for monitoring inputs
        setContentView(R.layout.activity_entradas_digitais_clp);
        setTitle("ENTRADAS (SENSORES)");

        imageViews = getImageViews();
        tvLabels = getTextViews();
        tvWifi = findViewById(R.id.tvWifi);
        tvHeap = findViewById(R.id.tvHeap);
        
        // Update labels to show actual ESP32 pins
        String[] keys = Interface.getInputKeys();
        for (int i = 0; i < tvLabels.length; i++) {
            tvLabels[i].setText("PIN " + keys[i].replace("Input_", ""));
        }

        lerDadosCLP();
    }

    private void lerDadosCLP() {
        Call<String> call = RetrofitClient.getApiService().lerDadosClp();
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
                Toast.makeText(getApplicationContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarStatus(JSONObject json) {
        String[] keys = Interface.getInputKeys();
        for (int i = 0; i < imageViews.length; i++) {
            int status = json.optInt(keys[i], 0);
            if (status == 1) {
                imageViews[i].setImageResource(android.R.drawable.presence_online);
            } else {
                imageViews[i].setImageResource(android.R.drawable.presence_busy);
            }
        }

        // Diagnostics moved to this screen
        int wifi = json.optInt("wifi", 0);
        int heap = json.optInt("heap", 0);
        if (tvWifi != null) tvWifi.setText(String.format(Locale.getDefault(), "%d%%", wifi));
        if (tvHeap != null) tvHeap.setText(String.format(Locale.getDefault(), "%d KB", heap / 1024));
    }

    private ImageView[] getImageViews() {
        return new ImageView[]{
                findViewById(R.id.imageView1), findViewById(R.id.imageView2),
                findViewById(R.id.imageView3), findViewById(R.id.imageView4),
                findViewById(R.id.imageView5), findViewById(R.id.imageView6),
                findViewById(R.id.imageView7), findViewById(R.id.imageView8),
                findViewById(R.id.imageView9), findViewById(R.id.imageView10)
        };
    }

    private TextView[] getTextViews() {
        return new TextView[]{
                findViewById(R.id.textView1), findViewById(R.id.textView2),
                findViewById(R.id.textView3), findViewById(R.id.textView4),
                findViewById(R.id.textView5), findViewById(R.id.textView6),
                findViewById(R.id.textView7), findViewById(R.id.textView8),
                findViewById(R.id.textView9), findViewById(R.id.textView10)
        };
    }
    
    public void ler(View view) {
        lerDadosCLP();
    }
}

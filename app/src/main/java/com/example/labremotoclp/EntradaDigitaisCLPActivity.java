package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntradaDigitaisCLPActivity extends AppCompatActivity {

    private MaterialSwitch[] switches;
    private TextView tvWifi, tvHeap;
    private TextView[] tvInputLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_digitais_clp);
        setTitle("ENTRADAS E DIAGNÓSTICO");

        switches = getSwitchs();
        tvInputLabels = getTextViews();
        tvWifi = findViewById(R.id.tvWifi);
        tvHeap = findViewById(R.id.tvHeap);

        // Update labels to show actual ESP32 pins
        String[] keys = Interface.getInputKeys();
        for (int i = 0; i < tvInputLabels.length; i++) {
            tvInputLabels[i].setText("PIN " + keys[i].replace("Input_", ""));
        }

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
                        atualizarInterfaces(json);
                    } catch (Exception e) {
                        Log.e("LOG", "Erro JSON", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha na sincronização", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarInterfaces(JSONObject json) {
        // Inputs mapping
        String[] keys = Interface.getInputKeys();
        for (int i = 0; i < keys.length; i++) {
            if (json.has(keys[i])) {
                int status = json.optInt(keys[i], 0);
                switches[i].setChecked(status == 1);
            }
        }

        // Diagnostics
        int wifi = json.optInt("wifi", 0);
        int heap = json.optInt("heap", 0);
        
        tvWifi.setText(String.format(Locale.getDefault(), "%d%%", wifi));
        tvHeap.setText(String.format(Locale.getDefault(), "%d KB", heap / 1024));
    }

    private MaterialSwitch[] getSwitchs() {
        return new MaterialSwitch[]{
                findViewById(R.id.switch1), findViewById(R.id.switch2),
                findViewById(R.id.switch3), findViewById(R.id.switch4),
                findViewById(R.id.switch5), findViewById(R.id.switch6),
                findViewById(R.id.switch7), findViewById(R.id.switch8),
                findViewById(R.id.switch9), findViewById(R.id.switch10)
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

    public void escrever(View view) {
        try {
            JSONObject json = new JSONObject();
            String[] outputKeys = Interface.getOutputKeys();
            for (int i = 0; i < switches.length; i++) {
                json.put(outputKeys[i], switches[i].isChecked() ? 1 : 0);
            }

            Call<String> call = RetrofitClient.getApiService().escreverInterfaces(json.toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("LOG", "Falha no POST", t);
                }
            });
        } catch (Exception e) {
            Log.e("LOG", "Erro ao preparar comando", e);
        }
    }
}

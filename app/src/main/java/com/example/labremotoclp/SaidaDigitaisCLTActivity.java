package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaidaDigitaisCLTActivity extends AppCompatActivity {
    
    private MaterialSwitch[] switches;
    private TextView[] tvLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using layout with Switches for controlling outputs
        setContentView(R.layout.activity_saidas_digitais_clp);
        setTitle("SAÍDAS (RELÉS)");

        switches = getSwitchs();
        tvLabels = getTextViews();

        // Map labels to actual ESP32 Output Pins
        String[] keys = Interface.getOutputKeys();
        for (int i = 0; i < tvLabels.length; i++) {
            tvLabels[i].setText("PIN " + keys[i].replace("Output_", ""));
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
                        atualizarInterfaces(json);
                    } catch (Exception e) {
                        Log.e("LOG", "Erro JSON", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Erro de rede", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarInterfaces(JSONObject json) {
        String[] keys = Interface.getOutputKeys();
        for (int i = 0; i < keys.length; i++) {
            if (json.has(keys[i])) {
                int status = json.optInt(keys[i], 0);
                switches[i].setChecked(status == 1);
            }
        }
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
                        Toast.makeText(getApplicationContext(), "Saídas atualizadas", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("LOG", "Erro POST", t);
                }
            });
        } catch (Exception e) {
            Log.e("LOG", "Erro ao preparar JSON", e);
        }
    }
}

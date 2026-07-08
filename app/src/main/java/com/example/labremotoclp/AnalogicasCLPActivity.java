package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalogicasCLPActivity extends AppCompatActivity {
    
    private SeekBar seekBarDAC0, seekBarDAC1, seekBarFreq;
    private TextView tvTempValue, tvCurrentValue;
    private TextView tvInvStatus, tvInvFreq, tvInvRPM, tvFreqLabel;
    private TextView tvDAC0Label, tvDAC1Label;
    
    private String acaoPendente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analogicas_clp);
        setTitle("TELEMETRIA E CONTROLE");

        tvTempValue = findViewById(R.id.tvTempValue);
        tvCurrentValue = findViewById(R.id.tvCurrentValue);
        tvInvStatus = findViewById(R.id.tvInvStatus);
        tvInvFreq = findViewById(R.id.tvInvFreq);
        tvInvRPM = findViewById(R.id.tvInvRPM);
        
        tvDAC0Label = findViewById(R.id.tvDAC0);
        tvDAC1Label = findViewById(R.id.tvDAC1);
        seekBarDAC0 = findViewById(R.id.seekBarDAC0);
        seekBarDAC1 = findViewById(R.id.seekBarDAC1);
        
        tvFreqLabel = findViewById(R.id.tvFreqLabel);
        seekBarFreq = findViewById(R.id.seekBarFreq);

        seekBarDAC0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volts = (progress / 4095.0f) * 10.0f;
                tvDAC0Label.setText(String.format(Locale.getDefault(), "Canal 0: %.1fV (%d)", volts, progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarDAC1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volts = (progress / 4095.0f) * 10.0f;
                tvDAC1Label.setText(String.format(Locale.getDefault(), "Canal 1: %.1fV (%d)", volts, progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        seekBarFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFreqLabel.setText("Referência: " + progress + " Hz");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

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
                Toast.makeText(getApplicationContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarInterfaces(JSONObject json) {
        double temp = json.optDouble("Temp_A0", 0.0);
        double current = json.optDouble("CLP_A1", 0.0);
        tvTempValue.setText(String.format(Locale.getDefault(), "Temp: %.1f °C", temp));
        tvCurrentValue.setText(String.format(Locale.getDefault(), "Corrente: %.1f mA", current));

        String sentido = json.optString("inv_s", "---");
        double freq = json.optDouble("inv_f", 0.0);
        int rpm = json.optInt("inv_r", 0);
        tvInvStatus.setText("Sentido: " + sentido);
        tvInvFreq.setText(String.format(Locale.getDefault(), "Frequência: %.1f Hz", freq));
        tvInvRPM.setText("RPM: " + rpm);
    }

    public void comandoInversor(View view) {
        int id = view.getId();
        if (id == R.id.btnLigar) acaoPendente = "ligar";
        else if (id == R.id.btnDesligar) acaoPendente = "desligar";
        else if (id == R.id.btnAvanco) acaoPendente = "avanco";
        else if (id == R.id.btnReverso) acaoPendente = "reverso";
        
        Toast.makeText(this, "Ação selecionada: " + acaoPendente, Toast.LENGTH_SHORT).show();
    }

    public void escrever(View view) {
        try {
            JSONObject json = new JSONObject();
            
            // DAC
            json.put("DAC_Ch0", seekBarDAC0.getProgress());
            json.put("DAC_Ch1", seekBarDAC1.getProgress());
            Toast.makeText(this, "Enviando: " + seekBarDAC0.getProgress(), Toast.LENGTH_SHORT).show();
            Log.d("LOG", "Progress DAC0: " + seekBarDAC0.getProgress());
            
            // Inversor
            if (acaoPendente != null) {
                json.put("acao", acaoPendente);
                acaoPendente = null; // Limpa após preparar o envio
            }
            json.put("freq", seekBarFreq.getProgress());

            Call<String> call = RetrofitClient.getApiService().escreverInterfaces(json.toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Comandos enviados!", Toast.LENGTH_SHORT).show();
                        lerDadosCLP(); // Atualiza a tela após enviar
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("LOG", "Erro POST", t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

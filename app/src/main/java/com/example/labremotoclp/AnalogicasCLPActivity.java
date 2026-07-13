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
    private TextView tvTempA0, tvTempA1, tvTempA2, tvTempA3;
    private TextView tvInvStatus, tvInvStatusNome, tvInvFreq, tvInvRPM, tvFreqLabel;
    private TextView tvDAC0Label, tvDAC1Label;
    
    private String acaoPendente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analogicas_clp);
        setTitle("TELEMETRIA E CONTROLE");

        tvTempA0 = findViewById(R.id.tvTempA0);
        tvTempA1 = findViewById(R.id.tvTempA1);
        tvTempA2 = findViewById(R.id.tvTempA2);
        tvTempA3 = findViewById(R.id.tvTempA3);
        tvInvStatus = findViewById(R.id.tvInvStatus);
        tvInvStatusNome = findViewById(R.id.tvInvStatusNome);
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
        double t0 = json.optDouble("Temp_A0", 0.0);
        double t1 = json.optDouble("Temp_A1", 0.0);
        double t2 = json.optDouble("Temp_A2", 0.0);
        double t3 = json.optDouble("Temp_A3", 0.0);
        
        tvTempA0.setText(String.format(Locale.getDefault(), "Sensor A0: %.1f °C", t0));
        tvTempA1.setText(String.format(Locale.getDefault(), "Sensor A1: %.1f °C", t1));
        tvTempA2.setText(String.format(Locale.getDefault(), "Sensor A2: %.1f °C", t2));
        tvTempA3.setText(String.format(Locale.getDefault(), "Sensor A3: %.1f °C", t3));

        String sentido = json.optString("inv_s", "---");
        String estadoNome = json.optString("inv_w_nome", "---");
        double freq = json.optDouble("inv_f", 0.0);
        int rpm = json.optInt("inv_r", 0);
        
        tvInvStatus.setText("Sentido: " + sentido);
        tvInvStatusNome.setText("Estado: " + estadoNome);
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
            json.put("DAC_Ch0", (seekBarDAC0.getProgress() / 4095.0f) * 10.0f);
            json.put("DAC_Ch1", (seekBarDAC1.getProgress() / 4095.0f) * 10.0f);
            
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

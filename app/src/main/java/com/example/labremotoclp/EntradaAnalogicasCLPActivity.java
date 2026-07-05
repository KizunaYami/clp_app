package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntradaAnalogicasCLPActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ENTRADAS ANALÓGICAS DO CLP");
        setContentView(R.layout.activity_entrada_analogicas_clp);
        SeekBar seekBarA0 = findViewById(R.id.seekBar);
        SeekBar seekBarA1 = findViewById(R.id.seekBar2);
        seekBarA1.setMax(255);
        seekBarA0.setMax(255);
        lerDadosCLP();
    }

    private void lerDadosCLP() {
        Call<String> call = RetrofitClient.getApiService().lerSaidaDigitais();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String textoResultado = response.body();
                    textoResultado = textoResultado.replace("{", "");
                    textoResultado = textoResultado.replace("}", "");

                    Log.d("LOG", "dados lidos da clp: " + textoResultado);
                    atualizarInterfaces(textoResultado);
                } else {
                    Log.e("LOG", "Código de erro do servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha ao buscar dados da CLP", Toast.LENGTH_SHORT).show();
                Log.e("LOG", "Falha na requisição", t);
            }
        });
    }

    private void atualizarInterfaces(String textoResultado) {
        SeekBar seekBarA0 = findViewById(R.id.seekBar);
        SeekBar seekBarA1 = findViewById(R.id.seekBar2);
        String[] dadosInterfaces = textoResultado.split(",");

        for (int i = 0; i < dadosInterfaces.length; i++) {
            String[] dadoInterface = dadosInterfaces[i].split(":");

            if (dadoInterface[0].contains("status")) {
                continue;
            }

            int statusInterface = Integer.parseInt(dadoInterface[1]);
            if (dadoInterface[0].contains("A0")) {
                seekBarA0.setProgress(statusInterface);
            }
            if (dadoInterface[0].contains("A1")) {
                seekBarA1.setProgress(statusInterface);
            }
        }
        Toast.makeText(getApplicationContext(), "Dados lidos da CLP", Toast.LENGTH_SHORT).show();
    }

    public void escrever(View view) {
        SeekBar seekBarA0 = findViewById(R.id.seekBar);
        SeekBar seekBarA1 = findViewById(R.id.seekBar2);
        int valueA0 = seekBarA0.getProgress();
        int valueA1 = seekBarA1.getProgress();

        Interface.atualizarInterface("A0", valueA0);
        Interface.atualizarInterface("A1", valueA1);

        Log.i("LOG", "dados para enviar servidor: " + Interface.converteParaJson());
        Call<String> call = RetrofitClient.getApiService().escreverInterfaces(Interface.converteParaJson());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String respostaServidor = response.body();
                    Toast.makeText(getApplicationContext(), "Dados gravados na CLP", Toast.LENGTH_SHORT).show();
                    Log.d("LOG", "O servidor respondeu: " + respostaServidor);
                } else {
                    Log.e("LOG", "Erro no servidor. Código HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("LOG", "Falha na conexão de rede", t);
            }
        });
    }
}

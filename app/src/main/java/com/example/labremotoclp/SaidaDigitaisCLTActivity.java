package com.example.labremotoclp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaidaDigitaisCLTActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SAÍDAS DIGITAIS DO CLP");
        setContentView(R.layout.activity_saida_digitais_clp);
        lerDadosCLP();
    }

    public void ler(View view) {
        lerDadosCLP();
    }

    private void atualizarInterfaces(String dados) {
        ImageView[] imageViews = {findViewById(R.id.imageView1), findViewById(R.id.imageView2), findViewById(R.id.imageView3), findViewById(R.id.imageView4), findViewById(R.id.imageView5), findViewById(R.id.imageView6), findViewById(R.id.imageView7), findViewById(R.id.imageView8), findViewById(R.id.imageView9), findViewById(R.id.imageView10)};
        String[] dadosInterfaces = dados.split(",");

        for (int i = 0; i < imageViews.length; i++) {
            String[] dadoInterface = dadosInterfaces[i].split(":");
            int statusInterface = Integer.parseInt(dadoInterface[1]);
            ImageView imageView = imageViews[i];

            if (Interface.STATUS_ATIVADO == statusInterface) {
                imageView.setImageResource(android.R.drawable.presence_online);
            } else if (Interface.STATUS_DESABILITADO == statusInterface) {
                imageView.setImageResource(android.R.drawable.presence_busy);
            } else {
                Log.i("LOG", "Status desconhecido");
            }
        }
        Toast.makeText(getApplicationContext(), "Dados lidos da CLP", Toast.LENGTH_SHORT).show();
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
}
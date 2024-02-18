package com.example.proyecto;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsuario;
    private EditText editTextContraseña;
    private Button buttonIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsuario = findViewById(R.id.editTextText);
        editTextContraseña = findViewById(R.id.editTextTextPassword);
        buttonIngresar = findViewById(R.id.button);

        buttonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = editTextUsuario.getText().toString().trim();
                String contraseña = editTextContraseña.getText().toString().trim();
                String contraseñaEncriptada = MD5.encrypt(contraseña);


                String url = "http://192.168.101.8/WS/webapi.php?op=validar&usu=" + usuario + "&cla=" + contraseñaEncriptada;
                new ValidarLoginTask().execute(url);
            }
        });
    }

    private class ValidarLoginTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... urls) {
            String resultado = "";
            try {
                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();

                Response response = client.newCall(request).execute();
                resultado = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            try {
                JSONArray jsonArray = new JSONArray(resultado);
                String rol = jsonArray.getString(0);
                String resultadoAutenticacion = jsonArray.getString(1);

                if (resultadoAutenticacion.equals("0") && rol.equals("Paciente")) {

                    Intent intent = new Intent(MainActivity.this, Paciente.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        }


    }
}

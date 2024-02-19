package com.example.proyecto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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

    private static final String CHANNEL_ID="canal";

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


                String url = "http://192.168.101.6/WS/webapi.php?op=validar&usu=" + usuario + "&cla=" + contraseñaEncriptada;
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
                    // Generar la notificación antes de iniciar la actividad Paciente
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        generarNoticacionCanal();
                    } else {
                        generarNoticacionSinCanal();
                    }

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generarNoticacionCanal(){
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"NEW", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        generarNoticacionSinCanal();
    }


    public void generarNoticacionSinCanal() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (manager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.descarga)
                    .setContentTitle("CITAS")
                    .setContentText("Notificación Básica")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Acceso correcto a mirar citas"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            manager.notify(0, builder.build());
        }
    }


}

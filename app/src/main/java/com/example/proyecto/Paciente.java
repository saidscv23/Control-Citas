package com.example.proyecto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Bundle;

public class Paciente extends AppCompatActivity {

    private EditText txtCedula;
    private TextView ingresosTextView;
    private Button btnConsultar , btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);


        txtCedula = findViewById(R.id.txtingreso);
        ingresosTextView = findViewById(R.id.ingresos);
        btnConsultar = findViewById(R.id.button);
        btnSalir = findViewById(R.id.brn_salir);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cedula = txtCedula.getText().toString().trim();

                new ConsultarRegistroTask().execute("http://192.168.101.6/WS/webapi.php?op=tabla&ced=" + cedula);
            }

        });


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión y regresar a MainActivity
                Intent intent = new Intent(Paciente.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private class ConsultarRegistroTask extends AsyncTask<String, Void, String> {

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


            StringBuilder formattedResult = new StringBuilder();
            try {
                JSONArray jsonArray = new JSONArray(resultado);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String nombreCompletoPaciente = jsonObject.getString("Nombre_Completo_Paciente");
                    String cedulaPaciente = jsonObject.getString("Cedula_Paciente");
                    String nombreCompletoMedico = jsonObject.getString("Nombre_Completo_Medico");
                    String fechaCita = jsonObject.getString("Fecha_Cita");
                    String horaCita = jsonObject.getString("Hora_Cita");
                    String fechaRegistro = jsonObject.getString("Fecha_Registro");

                    // Construir el formato deseado
                    String registroFormateado = "Nombre del Paciente: " + nombreCompletoPaciente + "\n" +
                            "Cédula del Paciente: " + cedulaPaciente + "\n" +
                            "Nombre del Médico: " + nombreCompletoMedico + "\n" +
                            "Fecha de la Cita: " + fechaCita + "\n" +
                            "Hora de la Cita: " + horaCita + "\n" +
                            "Fecha de Registro: " + fechaRegistro + "\n\n";
                    formattedResult.append(registroFormateado);
                }

                ingresosTextView.setText(formattedResult.toString());
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
}
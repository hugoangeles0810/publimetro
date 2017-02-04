package pe.joedayz.publimetro.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Ciudad;

public class SeleccionaUbicacionActivity extends Activity {

    private static String TAG = SeleccionaUbicacionActivity.class.getSimpleName();

    private String urlJsonArry = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getUbicacion";

    private Spinner cboCiudad;  //null

    private ProgressDialog progressDialog;

    private String jsonResponse;

    private List<Ciudad> ciudadList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_ubicacion);

        Ciudad ciudad = new Ciudad();
        ciudad.setCodigo("0");
        ciudad.setDescripcion(getString(R.string.selecciona_tu_localidad));

        ciudadList.add(ciudad);

        cboCiudad = (Spinner) findViewById(R.id.cboCiudad);  // ---> spinner en el layout



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.por_favor_espere));
        progressDialog.setCancelable(false);

        makeJsonRequest(); //aqui se llena la lista de ciudades en ciudadList

        ArrayAdapter<Ciudad> adapter = new ArrayAdapter<Ciudad>(this, R.layout.spinner_item, ciudadList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_popup);

        cboCiudad.setAdapter(adapter);

        cboDistrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Ciudad ciudadSeleccionada = (Ciudad) parent.getItemAtPosition(position);

                if (!ciudadSeleccionada.getDescripcion().equals(getString(R.string.selecciona_tu_localidad))) {

                    Intent irDashboarActivity = new Intent(SeleccionaUbicacionActivity.this, DashboardActivity.class);
                    irDashboarActivity.putExtra("ciudadSeleccionada", ciudadSeleccionada);
                    startActivity(irDashboarActivity);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void makeJsonRequest() {

        showPDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonArry, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                try {

                    JSONObject lista = response.getJSONObject("lista");
                    JSONArray ciudades = lista.getJSONArray("ciudad");

                    jsonResponse = "";

                    for (int i = 0; i < ciudades.length(); i++) {
                        JSONObject ciudadJSON = (JSONObject) ciudades
                                .get(i);
                        Ciudad ciudad = new Ciudad();
                        ciudad.setCodigo(ciudadJSON.getString("codigo"));
                        ciudad.setDescripcion(ciudadJSON.getString("descripcion"));

                        JSONObject map = ciudadJSON.getJSONObject("map");
                        ciudad.setLatitude(map.getString("latitude"));
                        ciudad.setLongitud(map.getString("longitude"));
                        ciudad.setZoom(map.getString("zoom"));

                        ciudadList.add(ciudad);

                        jsonResponse += "ciudad: " + ciudad + "\n\n";
                    }



                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidePDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

                hidePDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hidePDialog();
    }

    private void showPDialog(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hidePDialog() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


}

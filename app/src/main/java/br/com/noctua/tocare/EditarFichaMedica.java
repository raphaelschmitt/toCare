package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class EditarFichaMedica extends AppCompatActivity {

    //Internet Connection
    String url_acesso = "https://tocaredev.azurewebsites.net/api/paciente/editar";
    private Map<String, String> params;
    private ProgressDialog progressDialog;
    private RequestQueue rq;

    private Toolbar mToolbar;
    private Button btSalvar;

    private Switch swPlanoDeSaude, swDeficiencia;
    private TextView swStatusPlanoDeSaude, swStatusDeficiencia, tvDescricaoDeficiencia;
    private EditText etDescricaoGeral, etDescricaoDeficiencia, etAltura, etPeso;

    private String tipoSanguineo;
    private String[] tiposSanguineos= new String[]{"Tipo Sanguíneo", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    Spinner spTipoSanguineo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ficha_medica);

        //internet
        rq = Volley.newRequestQueue(EditarFichaMedica.this);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.editar_ficha_medica);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_forward_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        swPlanoDeSaude = (Switch) findViewById(R.id.swPlanoDeSaude);
        swDeficiencia = (Switch) findViewById(R.id.swDeficiencia);
        swStatusPlanoDeSaude = (TextView) findViewById(R.id.swStatusPlanoDeSaude);
        swStatusDeficiencia = (TextView) findViewById(R.id.swStatusDeficiencia);
        tvDescricaoDeficiencia = (TextView) findViewById(R.id.tvDescricaoDeficiencia);

        etDescricaoGeral = (EditText) findViewById(R.id.etDescricaoGeral);
        etDescricaoDeficiencia = (EditText) findViewById(R.id.etDescricaoDeficiencia);
        etAltura = (EditText) findViewById(R.id.etAltura);
        etPeso = (EditText) findViewById(R.id.etPeso);

        //set the switch to ON
        swPlanoDeSaude.setChecked(false);
        //attach a listener to check for changes in state
        swPlanoDeSaude.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    swStatusPlanoDeSaude.setText("Sim");
                }else{
                    swStatusPlanoDeSaude.setText("Não");
                }
            }
        });
        //check the current state before we display the screen
        if(swPlanoDeSaude.isChecked()){
            swStatusPlanoDeSaude.setText("Sim");
        }
        else {
            swStatusPlanoDeSaude.setText("Não");
        }

        //set the switch to ON
        swDeficiencia.setChecked(false);
        //attach a listener to check for changes in state
        swDeficiencia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    swStatusDeficiencia.setText("Sim");
                    tvDescricaoDeficiencia.setVisibility(View.VISIBLE);
                    etDescricaoDeficiencia.setVisibility(View.VISIBLE);
                }else{
                    swStatusDeficiencia.setText("Não");
                    tvDescricaoDeficiencia.setVisibility(View.GONE);
                    etDescricaoDeficiencia.setVisibility(View.GONE);
                }
            }
        });
        //check the current state before we display the screen
        if(swDeficiencia.isChecked()){
            swStatusDeficiencia.setText("Sim");
            tvDescricaoDeficiencia.setVisibility(View.VISIBLE);
            etDescricaoDeficiencia.setVisibility(View.VISIBLE);
        } else {
            swStatusDeficiencia.setText("Não");
            tvDescricaoDeficiencia.setVisibility(View.GONE);
            etDescricaoDeficiencia.setVisibility(View.GONE);
        }

        btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarDados();
            }
        });

        ArrayAdapter<String> adapterTipoSanguineo = new ArrayAdapter<String>(this, R.layout.spinner_item, tiposSanguineos);
        //opicional
        adapterTipoSanguineo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTipoSanguineo = (Spinner)findViewById(R.id.spinnerTipoSanguineo);
        spTipoSanguineo.setAdapter(adapterTipoSanguineo);

        spTipoSanguineo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSanguineo = (String) spTipoSanguineo.getSelectedItem();
                if(tipoSanguineo.equals("Tipo Sanguíneo")){
                    tipoSanguineo = "x";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        montarTela();

    }

    public void montarTela(){
        if(HomePaciente.paciente.getPlanoDeSaude()){
            swPlanoDeSaude.setChecked(true);
        } else {
            swPlanoDeSaude.setChecked(false);
        }
        if(HomePaciente.paciente.getDeficienciaFisica()){
            swDeficiencia.setChecked(true);
            tvDescricaoDeficiencia.setVisibility(View.VISIBLE);
            etDescricaoDeficiencia.setVisibility(View.VISIBLE);
        } else {
            swDeficiencia.setChecked(false);
            tvDescricaoDeficiencia.setVisibility(View.GONE);
            etDescricaoDeficiencia.setVisibility(View.GONE);
        }

        if(!HomePaciente.paciente.getDescricaoGeral().isEmpty()){
            etDescricaoGeral.setText(HomePaciente.paciente.getDescricaoGeral());
        }

        if(!HomePaciente.paciente.getDescricaoDeficiencia().isEmpty()){
            etDescricaoDeficiencia.setText(HomePaciente.paciente.getDescricaoDeficiencia());
        }

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setMaximumIntegerDigits(2);
        format.setRoundingMode(RoundingMode.HALF_UP);

        if(!format.format(HomePaciente.paciente.getAltura()).equals("0,00")){
            etAltura.setText(HomePaciente.paciente.getAltura()+"");
        }

        if(!format.format(HomePaciente.paciente.getPeso()).equals("0,00")){
            etPeso.setText(HomePaciente.paciente.getPeso()+"");
        }

        if(HomePaciente.paciente != null){
            if(HomePaciente.paciente.getTipoSanguineo().length() > 0 && HomePaciente.paciente.getTipoSanguineo() != null){
                spTipoSanguineo.setSelection(getIndexSpinner(spTipoSanguineo, HomePaciente.paciente.getTipoSanguineo()));
            }
        }
    }

    public void editarDados() {
        progressDialog = new ProgressDialog(EditarFichaMedica.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando...");
        progressDialog.show();

        HomePaciente.paciente.setPlanoDeSaude(swPlanoDeSaude.isActivated());
        HomePaciente.paciente.setDeficienciaFisica(swDeficiencia.isActivated());

        if(swPlanoDeSaude.isChecked()){
            HomePaciente.paciente.setPlanoDeSaude(true);
        } else {
            HomePaciente.paciente.setPlanoDeSaude(false);
        }
        if(swDeficiencia.isChecked()){
            HomePaciente.paciente.setDeficienciaFisica(true);
            HomePaciente.paciente.setDescricaoDeficiencia(etDescricaoDeficiencia.getText().toString());
        } else {
            HomePaciente.paciente.setDeficienciaFisica(false);
            HomePaciente.paciente.setDescricaoDeficiencia("Não possui");
        }
        HomePaciente.paciente.setDescricaoGeral(etDescricaoGeral.getText().toString());
        if(!tipoSanguineo.equals("x")){
            HomePaciente.paciente.setTipoSanguineo(tipoSanguineo);
        } else {
            HomePaciente.paciente.setTipoSanguineo("Não informado.");
        }
        if(!etAltura.getText().toString().isEmpty()){
            HomePaciente.paciente.setAltura(Double.parseDouble(etAltura.getText().toString()));
        } else {
            HomePaciente.paciente.setAltura(Double.parseDouble("0.00"));
        }

        if(!etPeso.getText().toString().isEmpty()){
            HomePaciente.paciente.setPeso(Double.parseDouble(etPeso.getText().toString()));
        } else {
            HomePaciente.paciente.setPeso(Double.parseDouble("0.00"));
        }


        //@requestfields id_paciente,bl_planodesaude,st_descricaogeral,
        // bl_deficienciafisica, st_descricaodeficienciafisica,nu_altura,st_tiposanguineo,nu_peso

        params = new HashMap<String, String>();
        params.put("id_paciente", HomePaciente.paciente.getIdPaciente()+"");
        if(HomePaciente.paciente.getPlanoDeSaude()){
            params.put("bl_planodesaude", ""+ 1);
        } else {
            params.put("bl_planodesaude", ""+ 0);
        }
        params.put("st_descricaogeral", ""+HomePaciente.paciente.getDescricaoGeral());
        if(HomePaciente.paciente.getDeficienciaFisica()){
            params.put("bl_deficienciafisica", ""+1);
        } else {
            params.put("bl_deficienciafisica", ""+0);
        }
        params.put("st_descricaodeficienciafisica", ""+HomePaciente.paciente.getDescricaoDeficiencia());
        params.put("nu_altura", ""+HomePaciente.paciente.getAltura());
        params.put("st_tiposanguineo", ""+HomePaciente.paciente.getTipoSanguineo());
        params.put("nu_peso", ""+HomePaciente.paciente.getPeso());

        callByStringRequest(null);
    }

    public void callByStringRequest(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(!response.isEmpty()) {

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (jsonObject != null) {
                                AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaMedica.this);
                                mesg.setMessage(jsonObject.optString("mensagem"));
                                mesg.setTitle("");
                                mesg.setCancelable(true);
                                mesg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Intent i = new Intent(getApplicationContext(), FichaMedica.class);
                                        i.putExtra("origem", "editarFichaMedica");
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                        finish();
                                    }
                                });
                                mesg.show();
                            } else {
                                AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaMedica.this);
                                mesg.setMessage("Houve algum erro, tente novamente mais tarde!");
                                mesg.setTitle("");
                                mesg.setCancelable(true);
                                mesg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                mesg.show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;

                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(error.getMessage());
                } catch (JSONException e) {                    e.printStackTrace();                } catch (Exception e){                    e.printStackTrace();                }

                if(jsonObject != null){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaMedica.this);
                    if(!jsonObject.optString("error").isEmpty()){
                        mesg.setMessage(jsonObject.optString("error"));
                    } else if(!jsonObject.optString("mensagem").isEmpty()) {
                        mesg.setMessage(jsonObject.optString("mensagem") + " Motivo: "+jsonObject.optString("motivo"));
                    } else {
                        mesg.setMessage("Houve algum erro! Tente novamente mais tarde!");
                    }
                    mesg.setTitle("");
                    mesg.setCancelable(true);
                    mesg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mesg.show();
                } else {
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaMedica.this);
                    mesg.setMessage("Houve algum erro, tente novamente mais tarde!");
                    mesg.setTitle("");
                    mesg.setCancelable(true);
                    mesg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mesg.show();
                }

            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                //header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "Bearer "+LoginPaciente.ACCESS_TOKEN);
                return header;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }

            @Override
            public Priority getPriority(){
                return (Priority.NORMAL);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 10000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag("tag");
        rq.add(stringRequest);

    }

    @Override
    public void onStop(){
        super.onStop();

        rq.cancelAll("tag");

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), FichaMedica.class);
        i.putExtra("origem", "editarFichaMedica");
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            // Start the Signup activity
            Intent i = new Intent(getApplicationContext(), FichaMedica.class);
            i.putExtra("origem", "editarFichaMedica");
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getIndexSpinner(Spinner spinner, String myString){
        String aux="";

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            aux = spinner.getItemAtPosition(i).toString();
            if (aux.equals(myString)){
                index = i;
            }
        }
        return index;
    }

}

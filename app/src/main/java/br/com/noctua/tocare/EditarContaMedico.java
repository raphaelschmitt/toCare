package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Objetos.MaskUtil;

public class EditarContaMedico extends AppCompatActivity {

    private Toolbar mToolbar;

    //Internet Connection
    String url_acesso = "https://tocaredev.azurewebsites.net/api/pessoa/editar";
    private Map<String, String> params;
    private ProgressDialog progressDialog;
    private RequestQueue rq;

    Button btSalvar;

    //Spinners Arrays
    private String[] sexos = new String[]{"Gênero", "Masculino", "Feminino"};

    private Spinner spSexo;

    private String dia = "x", mes = "x", ano = "x", sexo = "x";

    private EditText name;
    private EditText sobrenome;
    private EditText paisDeOrigem;
    private EditText cro_crm;
    private EditText email;

    private TextView tvDataNascimento;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private boolean foiSalvo = false;

    private int diaU = 0, mesU = 0, anoU = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_conta_medico);

        //internet
        rq = Volley.newRequestQueue(EditarContaMedico.this);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.editar_conta);
        //mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText)findViewById(R.id.et_nome);
        sobrenome = (EditText)findViewById(R.id.et_sobrenome);
        cro_crm = (EditText)findViewById(R.id.et_cro_crm);
        paisDeOrigem = (EditText) findViewById(R.id.et_nacionalidade);
        email = (EditText)findViewById(R.id.et_email);

        btSalvar = (Button)findViewById(R.id.btSalvar);

        tvDataNascimento = (TextView) findViewById(R.id.tvDataNascimentoSpinner);

        tvDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                if(!HomeMedicos.medico.getDataNascimento().isEmpty() && HomeMedicos.medico.getDataNascimento() != null){
                    dia = Integer.parseInt(""+HomeMedicos.medico.getDataNascimento().charAt(0)+HomeMedicos.medico.getDataNascimento().charAt(1));
                    mes = Integer.parseInt(""+HomeMedicos.medico.getDataNascimento().charAt(3)+HomeMedicos.medico.getDataNascimento().charAt(4));
                    --mes;
                    ano = Integer.parseInt(""+HomeMedicos.medico.getDataNascimento().charAt(6)+HomeMedicos.medico.getDataNascimento().charAt(7)+HomeMedicos.medico.getDataNascimento().charAt(8)+HomeMedicos.medico.getDataNascimento().charAt(9));
                }

                if(diaU != 0 && mesU != 0 && anoU != 0){
                    ano = anoU;
                    dia = diaU;
                    mes = --mesU;
                }

                int theme;
                if (Build.VERSION.SDK_INT < 23) theme = AlertDialog.THEME_HOLO_LIGHT;
                else theme = android.R.style.Theme_Holo_Dialog;

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarContaMedico.this,
                        theme,
                        dateSetListener, ano, mes, dia);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anoX, int mesX, int diaX) {
                mesX++;

                diaU = diaX;
                mesU = mesX;
                anoU = anoX;

                dia = ""+diaX;
                if(mesX<10){
                    mes = "0"+mesX;
                } else {
                    mes = ""+mesX;
                }
                ano = ""+anoX;
                String data = dia+"/"+mes+"/"+ano;
                tvDataNascimento.setText(data);
            }
        };

        ArrayAdapter<String> adapterSexo = new ArrayAdapter<String>(this, R.layout.spinner_item, sexos);
        //opicional
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSexo = (Spinner)findViewById(R.id.spinnerSexo);
        spSexo.setAdapter(adapterSexo);

        spSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexo = (String) spSexo.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if(name.getText().toString().isEmpty()){
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                    mesg.setMessage("O nome não pode ser vazio!");
                                    mesg.setTitle("Atenção");
                                    mesg.setNeutralButton("Ok", null);
                                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    name.setError("O nome não pode ser vazio!");
                                    progressDialog.dismiss();
                                } else if(sobrenome.getText().toString().isEmpty()){
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                    mesg.setMessage("O nome não pode ser vazio!");
                                    mesg.setTitle("Atenção");
                                    mesg.setNeutralButton("Ok", null);
                                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    sobrenome.setError("O sobrenome não pode ser vazio!");
                                    progressDialog.dismiss();
                                } else if(sexo.equals("Gênero")){
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                    mesg.setMessage("É necessário escolher um gênero!");
                                    mesg.setTitle("Atenção");
                                    mesg.setNeutralButton("Ok", null);
                                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    progressDialog.dismiss();
                                } else if(cro_crm.getText().toString().isEmpty()){
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                    mesg.setMessage("Insira um CRO/CRM!");
                                    mesg.setTitle("Atenção");
                                    mesg.setNeutralButton("Ok", null);
                                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    cro_crm.setError("Insira um CRO/CRM!");
                                    progressDialog.dismiss();
                                } else {
                                    editarDados();
                                }
                            }
                        }, 100);
            }
        });

        montarTela();

    }

    public void editarDados() {
        progressDialog = new ProgressDialog(EditarContaMedico.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando...");
        progressDialog.show();

        HomeMedicos.medico.setNome(name.getText().toString());
        HomeMedicos.medico.setSobrenome(sobrenome.getText().toString());
        HomeMedicos.medico.setDataNascimento(tvDataNascimento.getText().toString());
        HomeMedicos.medico.setGenero(sexo);

        //@requestFields id_pessoa, st_cpf, st_nome, st_email, st_sobrenome, st_senha, st_genero, st_datanascimento
        params = new HashMap<String, String>();
        params.put("id_pessoa", HomeMedicos.medico.getIdPessoa()+"");
        params.put("st_cpf", ""+ MaskUtil.unmask(HomeMedicos.medico.getcpf()));
        params.put("st_nome", ""+HomeMedicos.medico.getNome());
        params.put("st_sobrenome", ""+HomeMedicos.medico.getSobrenome());
        params.put("st_email", ""+HomeMedicos.medico.getEmail());
        params.put("st_genero", ""+HomeMedicos.medico.getGenero());
        params.put("st_datanascimento", ""+HomeMedicos.medico.getDataNascimento());

        callByStringRequest(null);
    }

    public void callByStringRequest(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.contains("sucesso")){
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                            mesg.setMessage("Editado com Sucesso!");
                                            mesg.setTitle("");
                                            mesg.setCancelable(false);
                                            mesg.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    foiSalvo = true;
                                                }
                                            });
                                            //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                            mesg.show();
                                            // onLoginFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 100);
                        } else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
                                            mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                            mesg.setTitle("");
                                            mesg.setCancelable(false);
                                            mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    editarDados();
                                                }
                                            });
                                            mesg.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                            mesg.show();
                                            // onLoginFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 100);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(jsonObject != null){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarContaMedico.this);
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
                header.put("Authorization", "Bearer "+LoginMedico.ACCESS_TOKEN);
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

    public void montarTela(){
        if(HomeMedicos.medico != null){
            name.setText(HomeMedicos.medico.getNome());
            sobrenome.setText(HomeMedicos.medico.getSobrenome());
            if(HomeMedicos.medico.getGenero().length() > 0 && HomeMedicos.medico.getGenero() != null){
                spSexo.setSelection(getIndexSpinner(spSexo, HomeMedicos.medico.getGenero()));
            }
            tvDataNascimento.setText(HomeMedicos.medico.getDataNascimento());
            email.setText(HomeMedicos.medico.getEmail());
            cro_crm.setText(HomeMedicos.medico.getRegistroMedico());
            paisDeOrigem.setText(HomeMedicos.medico.getPaisOrigem());
        }
    }

    @Override
    public void onBackPressed() {
        if(foiSalvo){
            Intent i = new Intent(getApplicationContext(), HomeMedicos.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        } else {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            if(foiSalvo){
                Intent i = new Intent(getApplicationContext(), HomeMedicos.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            } else {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
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

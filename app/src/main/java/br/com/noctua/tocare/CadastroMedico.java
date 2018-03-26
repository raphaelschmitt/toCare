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
import android.widget.CheckBox;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import br.com.noctua.tocare.Objetos.AppStatus;
import br.com.noctua.tocare.Objetos.Paciente;

public class CadastroMedico extends AppCompatActivity {

    boolean execAsyncTask = false;

    //Internet Connection
    String url_acesso = "https://tocaredev.azurewebsites.net/api/medico/cadastro";
    private Map<String, String> params;
    private String ACCESS_TOKEN = "";
    private RequestQueue rq;
    private ProgressDialog progressDialog;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);

    //Spinners Arrays
    private String[] sexos = new String[]{"Gênero", "Masculino", "Feminino"};

    private Spinner spSexo;

    private String dia = "x", mes = "x", ano = "x", sexo = "x";

    EditText name;
    EditText sobrenome;
    EditText cro_crm, etPaisOrigem;
    EditText email;
    EditText emailC;
    EditText password;
    EditText confirmPassword;

    private Toolbar mToolbar;

    private CheckBox checkBoxPoliticasDePrivacidade, checkBoxTermosDeUso;

    private TextView linkTermosDeUso, linkPoliticaDePrivacidade, tvDataNascimento;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private int diaU = 0, mesU = 0, anoU = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medico);

        //internet
        rq = Volley.newRequestQueue(CadastroMedico.this);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.cadastro_medico);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_forward_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        name = (EditText)findViewById(R.id.tfName);
        sobrenome = (EditText)findViewById(R.id.tfSobrenome);
        email = (EditText)findViewById(R.id.tfEmail);
        emailC = (EditText)findViewById(R.id.tfEmailConfirm);
        password = (EditText)findViewById(R.id.tfPassword);
        confirmPassword = (EditText)findViewById(R.id.tfConfirmPassword);
        cro_crm = (EditText)findViewById(R.id.tfCRM_CRO);
        etPaisOrigem = (EditText) findViewById(R.id.etPaisOrigem);

        tvDataNascimento = (TextView) findViewById(R.id.tvDataNascimentoSpinner);

        tvDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                if(diaU != 0 && mesU != 0 && anoU != 0){
                    ano = anoU;
                    dia = diaU;
                    mes = --mesU;
                }
                int theme;
                if (Build.VERSION.SDK_INT < 23) theme = AlertDialog.THEME_HOLO_LIGHT;
                else theme = android.R.style.Theme_Holo_Dialog;

                DatePickerDialog datePickerDialog = new DatePickerDialog(CadastroMedico.this,
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


        checkBoxPoliticasDePrivacidade = (CheckBox) findViewById(R.id.checkBoxPoliticaDePrivacidade);
        checkBoxTermosDeUso = (CheckBox) findViewById(R.id.checkBoxTermosdeUso);

        linkPoliticaDePrivacidade = (TextView) findViewById(R.id.linkPoliticaDePrivacidade);
        linkTermosDeUso = (TextView) findViewById(R.id.linkTermosDeUso);

        linkPoliticaDePrivacidade.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(CadastroMedico.this, MyWebView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("url","http://dev.wips.com.br/politica-privacidade");
                startActivity(i);

                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://stackoverflow.com/questions/16927103/onclicklistener-in-android-studio"));
                //startActivity(browserIntent);
            }
        });

        linkTermosDeUso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(CadastroMedico.this, MyWebView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("url","http://dev.wips.com.br/termos-uso");
                startActivity(i);

                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://stackoverflow.com/questions/16927103/onclicklistener-in-android-studio"));
                //startActivity(browserIntent);
            }
        });

        ArrayAdapter<String> adapterSexo = new ArrayAdapter<String>(this, R.layout.spinner_item, sexos);
        //opicional
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSexo = (Spinner)findViewById(R.id.spinnerSexo);
        spSexo.setAdapter(adapterSexo);

        spSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexo = (String) spSexo.getSelectedItem();
                if(sexo.equals("Masculino")){
                    sexo = "Masculino";
                } else if(sexo.equals("Feminino")) {
                    sexo = "Feminino";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
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
            Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnClickCadastroMedico(View v){
        if(v.getId() == R.id.btRegister){

            //if(checkBoxPoliticasDePrivacidade.isChecked() && checkBoxTermosDeUso.isChecked()){
                Random gerador = new Random();

                String nameStr = name.getText().toString() + " " + sobrenome.getText();
                String emailStr = email.getText().toString().replace(" ", "");
                String emailCStr = emailC.getText().toString().replace(" ", "");
                String birthdayStr = dia + "/" + mes + "/" + ano;
                String passwordStr = password.getText().toString();
                String confirmPasswordStr = confirmPassword.getText().toString();
                String sexoStr = sexo;

                params = new HashMap<String, String>();

                //@requestFields st_cpf, st_nome, st_email, st_sobrenome, st_senha, st_genero, st_datanascimento
                //dados de pessoa + st_registromedico + st_paisorigem

                params.put("st_cpf","");
                params.put("st_documentomedico", "");
                params.put("st_nome", name.getText().toString());
                params.put("st_sobrenome", sobrenome.getText().toString());
                params.put("st_email", email.getText().toString().replace(" ", ""));
                params.put("st_senha", password.getText().toString());
                params.put("st_genero", sexo);
                params.put("st_datanascimento", dia + "/" + mes + "/" + ano);
                params.put("st_registromedico",cro_crm.getText().toString());
                params.put("st_paisorigem",etPaisOrigem.getText().toString());


                if(passwordStr.equals("") || nameStr.equals(" ")
                        || emailStr.equals("") || ano.equals("x") || mes.equals("x") || dia.equals("x") || sexoStr.equals("Gênero") ||
                        etPaisOrigem.getText().toString().isEmpty() || cro_crm.getText().toString().isEmpty()){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("Todos os campos precisam ser preenchidos!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                } else if (!confirmPasswordStr.equals(passwordStr)) {
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("Senhas não coincidem!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                    password.setError("As senhas precisam ser iguais.");
                    confirmPassword.setError("As senhas precisam ser iguais.");
                } else if (confirmPasswordStr.length() < 8) {
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("As senhas precisam ter mais de 8 digitos!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                    password.setError("As senhas precisam ter mais de 8 dígitos!");
                    confirmPassword.setError("As senhas precisam ter mais de 8 dígitos!");
                } else if (!emailCStr.equals(emailStr)) {
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("Os E-mails precisam ser iguais!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                    email.setError("Os E-mails precisam ser iguais!");
                    emailC.setError("Os E-mails precisam ser iguais!");
                }else if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("Insira um e-mail válido!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                    email.setError("Insira um e-mail válido!");
                    emailC.setError("Insira um e-mail válido!");
                } else if(!AppStatus.getInstance(this).isOnline()){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                    mesg.setMessage("Você não está conectado à Internet! Tente Novamente mais Tarde!");
                    mesg.setTitle("Atenção");
                    mesg.setNeutralButton("Ok", null);
                    mesg.setIcon(android.R.drawable.ic_dialog_alert);
                    mesg.show();
                } else if (passwordStr.equals(confirmPasswordStr)) {

                    progressDialog = new ProgressDialog(CadastroMedico.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Cadastrando...");
                    progressDialog.show();

                    callByStringRequest(null);
                }
            /*} else {
                AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                mesg.setMessage("É necessário aceitar os \"Termos de Uso e Serviço\" e a \"Política de Privacidade\" para se cadastrar!");
                mesg.setTitle("Atenção");
                mesg.setNeutralButton("Ok", null);
                mesg.setIcon(android.R.drawable.ic_dialog_alert);
                mesg.show();
            }*/
        }
    }

    public void callByStringRequest(View view){
        final Paciente paciente = new Paciente();
        Gson gson = new Gson();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(!response.isEmpty()){

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(jsonObject != null){
                                AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
                                mesg.setMessage(jsonObject.optString("mensagem"));
                                mesg.setTitle("");
                                mesg.setCancelable(true);
                                mesg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Intent i = new Intent(getApplicationContext(), LoginMedico.class);
                                        startActivity(i);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                });
                                mesg.show();
                            } else {
                                AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
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
                error.printStackTrace();
                progressDialog.dismiss();
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;

                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(error.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

                if(jsonObject != null){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(CadastroMedico.this);
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
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("token", ACCESS_TOKEN);
                return header;
            }*/

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

}

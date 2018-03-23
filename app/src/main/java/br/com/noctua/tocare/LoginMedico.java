package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Objetos.Paciente;

public class LoginMedico extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    //Internet Connection
    public static String ACCESS_TOKEN = "";
    String url_acesso = "https://tocaredev.azurewebsites.net/api/medico/login";
    private Map<String, String> params;
    private RequestQueue rq;
    private ProgressDialog progressDialog;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    //Button _signupButton;
    Button _btVoltar;
    TextView _esqueceuSenha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_medico);

        //internet
        rq = Volley.newRequestQueue(LoginMedico.this);

        _emailText = (EditText)findViewById(R.id.EdnUsername);
        _passwordText = (EditText)findViewById(R.id.EdnPassword);
        _loginButton = (Button)findViewById(R.id.btLogin);
        //_signupButton = (Button)findViewById(R.id.btSignUp);
        _btVoltar = (Button) findViewById(R.id.btVoltar);
        _esqueceuSenha = (TextView) findViewById(R.id.esqueceuSenha);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _btVoltar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        /*_signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                AlertDialog.Builder mesg = new AlertDialog.Builder(LoginPaciente.this);
                mesg.setMessage("Você é um médico ou um paciente?");
                mesg.setTitle("");
                mesg.setCancelable(true);
                mesg.setPositiveButton("Paciente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), CadastroPaciente.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                mesg.setNegativeButton("Médico", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), CadastroMedico.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                mesg.show();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public void login() {
        Log.d(TAG, "Login");

        hideSoftKeyboard();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginMedico.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        String cro_crm = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        params = new HashMap<String, String>();

        params.put("st_registromedico", cro_crm);
        params.put("st_senha", password);

        callByStringRequest(null);
    }

    public void callByStringRequest(View view){
        final Paciente paciente = new Paciente();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        _loginButton.setEnabled(true);

                        if(response.indexOf("token") > 0) {
                            ACCESS_TOKEN = "";
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jsonObject != null) {
                                ACCESS_TOKEN = jsonObject.optString("token").toString();
                            }

                            Toast.makeText(getBaseContext(), "Login efetuado com sucesso!", Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();

                            Intent i = new Intent(getApplicationContext(), HomeMedicos.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "OPS! Algo deu errado!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(error.getMessage());
                } catch (JSONException e) {                    e.printStackTrace();                } catch (Exception e){                    e.printStackTrace();                }

                progressDialog.dismiss();

                if(jsonObject != null){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(LoginMedico.this);
                    mesg.setMessage(jsonObject.optString("error"));
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(LoginMedico.this);
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

                _loginButton.setEnabled(true);

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

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful CadastroPaciente logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(getApplicationContext(), HomeMedicos.class);
        startActivity(i);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Falha no Login", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Entre com CRM/CRO");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError("A senha deve conter no mínimo 8 dígitos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Objetos.Util;

public class EditarFichaConsulta extends AppCompatActivity {

    //Internet Connection
    String url_acesso = "https://tocaredev.azurewebsites.net/api/prontuario/editar";
    private Map<String, String> params;
    private ProgressDialog progressDialog;
    private RequestQueue rq;

    private Toolbar mToolbar;
    private Button btSalvar;
    
    private TextView tvUsername, tvDataNascimento;
    private SimpleDraweeView ivFotoPerfil;
    
    private EditText etAnamnese, etExameFisico, etHipoteseDiagnostica, etPlanoTerapeutico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ficha_consulta);

        //internet
        rq = Volley.newRequestQueue(EditarFichaConsulta.this);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Editar Ficha de Consulta");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivFotoPerfil = (SimpleDraweeView) findViewById(R.id.ivFotoPerfil);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvDataNascimento = (TextView) findViewById(R.id.tv_DataNascimento);
        
        etAnamnese = (EditText) findViewById(R.id.et_anamnese);
        etExameFisico = (EditText) findViewById(R.id.et_exameFisico);
        etHipoteseDiagnostica = (EditText) findViewById(R.id.et_hipoteseDiagnostica);
        etPlanoTerapeutico = (EditText) findViewById(R.id.et_planoTerapeutico);

        btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarDados();
            }
        });
        
        montarTela();

    }
    
    public void montarTela(){
        //NEW PART - FRESCO CARREGAMENTO DE IMAGEM
        ControllerListener listener = new BaseControllerListener(){
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
            }
        };

        String urlImagem = DetalhesPaciente.paciente.getFoto().replace(" ", "%20");

        Uri uri = null;
        DraweeController dc = null;

        if(urlImagem.contains("https")){
            uri = Uri.parse(DetalhesPaciente.paciente.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        } else {
            uri = Uri.parse("https://tocaredev.azurewebsites.net"+DetalhesPaciente.paciente.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        }
        ivFotoPerfil.setController(dc);

        tvUsername.setText(DetalhesPaciente.paciente.getNome() + " " + DetalhesPaciente.paciente.getSobrenome());

        int idade = Util.retornaIdade(DetalhesPaciente.paciente.getDataNascimento());

        tvDataNascimento.setText(DetalhesPaciente.paciente.getDataNascimento() + " (" + idade + ")");
        
        etAnamnese.setText(DetalhesPaciente.paciente.getProntuario().getAnamnese());
        etExameFisico.setText(DetalhesPaciente.paciente.getProntuario().getExameFisico());
        etPlanoTerapeutico.setText(DetalhesPaciente.paciente.getProntuario().getPlanoTerapeutico());
        etHipoteseDiagnostica.setText(DetalhesPaciente.paciente.getProntuario().getHipoteseDiagnostica());
        
    }

    public void editarDados() {
        progressDialog = new ProgressDialog(EditarFichaConsulta.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando...");
        progressDialog.show();

        DetalhesPaciente.paciente.getProntuario().setAnamnese(etAnamnese.getText().toString());
        DetalhesPaciente.paciente.getProntuario().setExameFisico(etExameFisico.getText().toString());
        DetalhesPaciente.paciente.getProntuario().setPlanoTerapeutico(etPlanoTerapeutico.getText().toString());
        DetalhesPaciente.paciente.getProntuario().setHipoteseDiagnostica(etHipoteseDiagnostica.getText().toString());

        //id_paciente, st_anamnese,st_examefisico, st_diagnostico,st_tratamento
        params = new HashMap<String, String>();
        params.put("id_paciente", DetalhesPaciente.paciente.getIdPaciente()+"");
        params.put("st_anamnese", ""+ DetalhesPaciente.paciente.getProntuario().getAnamnese());
        params.put("st_examefisico", ""+DetalhesPaciente.paciente.getProntuario().getExameFisico());
        params.put("st_diagnostico", ""+DetalhesPaciente.paciente.getProntuario().getHipoteseDiagnostica());
        params.put("st_tratamento", ""+DetalhesPaciente.paciente.getProntuario().getPlanoTerapeutico());

        callByStringRequest(null);
    }

    public void callByStringRequest(View view){

        String url = url_acesso +
                "?id_paciente=" + DetalhesPaciente.paciente.getIdPaciente() +
                "&st_anamnese=" + DetalhesPaciente.paciente.getProntuario().getAnamnese() +
                "&st_examefisico=" + DetalhesPaciente.paciente.getProntuario().getExameFisico() +
                "&st_diagnostico=" + DetalhesPaciente.paciente.getProntuario().getHipoteseDiagnostica() +
                "&st_tratamento=" + DetalhesPaciente.paciente.getProntuario().getPlanoTerapeutico();

        if(DetalhesPaciente.paciente.getProntuario().getId() != 0){
            url = url + "&id_prontuario=" + DetalhesPaciente.paciente.getProntuario().getId();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.contains("editado")){
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaConsulta.this);
                                            mesg.setMessage("Editado com Sucesso!");
                                            mesg.setTitle("");
                                            mesg.setCancelable(false);
                                            mesg.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
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
                        } else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaConsulta.this);
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaConsulta.this);
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(EditarFichaConsulta.this);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), FichaConsulta.class);
        i.putExtra("origem", "editarFichaConsulta");
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
            Intent i = new Intent(getApplicationContext(), FichaConsulta.class);
            i.putExtra("origem", "editarFichaConsulta");
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

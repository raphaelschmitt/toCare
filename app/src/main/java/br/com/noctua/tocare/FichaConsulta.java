package br.com.noctua.tocare;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Objetos.Paciente;
import br.com.noctua.tocare.Objetos.Prontuario;
import br.com.noctua.tocare.Objetos.Util;

public class FichaConsulta extends AppCompatActivity {

    //Internet Connection
    String url_base = "https://tocaredev.azurewebsites.net";
    private Map<String, String> params;
    private RequestQueue rq;
    private RelativeLayout layout_noResults, layoutDados;
    private LinearLayout linlaHeaderProgress;

    private SimpleDraweeView ivFotoPerfil;

    private TextView tvUsername, tvDataNascimento, tvAnamnese, tvExameFisico, tvHipoteseDiagnostica, tvPlanoTerapeutico;

    private Toolbar mToolbar;
    private Button btEditar;
    private String origem;

    private Paciente paciente = new Paciente();

    private Button btCriar;

    private boolean detalhesPaciente = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_ficha_consulta);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.ficha_de_consulta);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //internet
        rq = Volley.newRequestQueue(FichaConsulta.this);

        ivFotoPerfil = (SimpleDraweeView) findViewById(R.id.ivFotoPerfil);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvDataNascimento = (TextView) findViewById(R.id.tv_DataNascimento);
        tvAnamnese = (TextView) findViewById(R.id.tv_anamnese);
        tvExameFisico = (TextView) findViewById(R.id.tv_exameFisico);
        tvHipoteseDiagnostica = (TextView) findViewById(R.id.tv_hipoteseDiagnostica);
        tvPlanoTerapeutico = (TextView) findViewById(R.id.tv_planoTerapeutico);

        layout_noResults = (RelativeLayout) findViewById(R.id.layout_noResults);
        layoutDados = (RelativeLayout) findViewById(R.id.layout_dados);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        btCriar = (Button) findViewById(R.id.btCriar);
        btCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditarFichaConsulta.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        btEditar = (Button) findViewById(R.id.btEditar);
        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditarFichaConsulta.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        Intent intent = getIntent();
        origem = intent.getStringExtra("origem");

        if(!origem.equals("detalhesPaciente") && !origem.equals("editarFichaConsulta")){
            btEditar.setVisibility(View.INVISIBLE);
            btEditar.setHeight(0);
            btCriar.setVisibility(View.INVISIBLE);
            btCriar.setHeight(0);
            paciente = HomePaciente.paciente;
            detalhesPaciente = false;
        } else {
            paciente = DetalhesPaciente.paciente;
            detalhesPaciente = true;
        }

        buscaDados();

    }

    public void buscaDados(){

        linlaHeaderProgress.setVisibility(View.VISIBLE);
        layoutDados.setVisibility(View.GONE);
        layout_noResults.setVisibility(View.GONE);

        String url = url_base+"/api/prontuario/retornar/"+paciente.getIdPaciente();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty()){

                            JSONObject jsonBusca = null;
                            try {
                                jsonBusca = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(jsonBusca != null) {
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = jsonBusca.optJSONArray("prontuarios");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                JSONObject jsonObject;

                                if (jsonArray == null || jsonArray.toString().equals("[]")) {
                                    try {
                                        layoutDados.setVisibility(View.GONE);
                                        layout_noResults.setVisibility(View.VISIBLE);
                                        btEditar.setText("Criar Ficha");
                                        if(paciente.getProntuario() == null){
                                            paciente.setProntuario(new Prontuario());
                                        }
                                    } catch (Exception e) {
                                        //util.msg(e.toString(),"Erro!",Principal.this);
                                    }
                                } else {
                                    layoutDados.setVisibility(View.VISIBLE);
                                    layout_noResults.setVisibility(View.GONE);

                                    try {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            jsonObject = jsonArray.getJSONObject(i);
                                            /*
                                            {"prontuarios":[{
                                            "id_prontuario":1,
                                            "st_anamnese":"Teste de cadastro",
                                            "st_examefisico":"Ok",
                                            "st_diagnostico":"Virose",
                                            "st_tratamento":"Descanso",
                                            "id_paciente":"1"}]}
                                             */
                                            if(paciente.getProntuario() == null){
                                                paciente.setProntuario(new Prontuario());
                                            }
                                            paciente.getProntuario().setId(jsonObject.optInt("id_prontuario"));
                                            paciente.getProntuario().setAnamnese(jsonObject.optString("st_anamnese"));
                                            paciente.getProntuario().setExameFisico(jsonObject.optString("st_examefisico"));
                                            paciente.getProntuario().setHipoteseDiagnostica(jsonObject.optString("st_diagnostico"));
                                            paciente.getProntuario().setPlanoTerapeutico(jsonObject.optString("st_tratamento"));
                                        }
                                    } catch (Exception e) {
                                        String e1 = e.toString();
                                        e.printStackTrace();
                                    }

                                    montarTela();

                                }
                            }

                            linlaHeaderProgress.setVisibility(View.GONE);

                            if(detalhesPaciente){
                                DetalhesPaciente.paciente = paciente;
                            } else {
                                HomePaciente.paciente = paciente;
                            }
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

                if(jsonObject != null){
                    AlertDialog.Builder mesg = new AlertDialog.Builder(FichaConsulta.this);
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
                    AlertDialog.Builder mesg = new AlertDialog.Builder(FichaConsulta.this);
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
                header.put("Content-Type", "application/json; charset=UTF-8");
                if(detalhesPaciente){
                    header.put("Authorization", "Bearer "+LoginMedico.ACCESS_TOKEN);
                } else {
                    header.put("Authorization", "Bearer "+LoginPaciente.ACCESS_TOKEN);
                }

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

    public void montarTela(){

        tvAnamnese.setText(paciente.getProntuario().getAnamnese());

        tvPlanoTerapeutico.setText(paciente.getProntuario().getPlanoTerapeutico());

        tvHipoteseDiagnostica.setText(paciente.getProntuario().getHipoteseDiagnostica());

        tvExameFisico.setText(paciente.getProntuario().getExameFisico());

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

        String urlImagem = paciente.getFoto().replace(" ", "%20");

        Uri uri = null;
        DraweeController dc = null;

        if(urlImagem.contains("https")){
            uri = Uri.parse(paciente.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        } else {
            uri = Uri.parse("https://tocaredev.azurewebsites.net"+paciente.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        }
        ivFotoPerfil.setController(dc);

        tvUsername.setText(paciente.getNome() + " " + paciente.getSobrenome());

        int idade = Util.retornaIdade(paciente.getDataNascimento());

        tvDataNascimento.setText(paciente.getDataNascimento() + " (" + idade + ")");

    }

    @Override
    public void onStop(){
        super.onStop();

        rq.cancelAll("tag");

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

}

package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import br.com.noctua.tocare.Objetos.Medico;

public class HomeMedicos extends AppCompatActivity {

    //Internet Connection
    String url_base = "https://tocaredev.azurewebsites.net";
    String url_acesso = "https://tocaredev.azurewebsites.net/api/pessoa/retornar-pessoa-app";
    private Map<String, String> params;
    private RequestQueue rq;
    private ProgressDialog progressDialog;
    private String ACCESS_TOKEN;

    //itens do xml
    private SimpleDraweeView ivFotoPerfil;
    private TextView tvUsername;

    Button btSair;
    int countBackPressed = 0;
    public static Medico medico = new Medico();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_home_medicos);

        btSair = (Button) findViewById(R.id.btLogOut);

        //internet
        rq = Volley.newRequestQueue(HomeMedicos.this);

        ivFotoPerfil = (SimpleDraweeView)findViewById(R.id.ivFotoPerfil);
        tvUsername = (TextView)findViewById(R.id.tvUsername);

        ACCESS_TOKEN = LoginMedico.ACCESS_TOKEN;

        if(!ACCESS_TOKEN.isEmpty() && ACCESS_TOKEN != null){
            carregarDados();
        }

        btSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                mesg.setMessage("Tem certeza que deseja sair?");
                mesg.setTitle("");
                mesg.setCancelable(true);
                mesg.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
                        startActivity(i);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                mesg.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        countBackPressed = 0;
                        return;
                    }
                });
                mesg.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(countBackPressed >= 1){
            AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
            mesg.setMessage("Deseja fechar o aplicativo?");
            mesg.setTitle("");
            mesg.setCancelable(true);
            mesg.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            mesg.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    countBackPressed = 0;
                    return;
                }
            });
            mesg.show();
        }

        countBackPressed++;
    }

    public void carregarDados() {
        progressDialog = new ProgressDialog(HomeMedicos.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando...");
        progressDialog.show();

        params = new HashMap<String, String>();

        callByStringRequest(null);
    }

    public void callByStringRequest(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.indexOf("user") > 0) {

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response.toString());
                                jsonObject = new JSONObject(jsonObject.optJSONObject("user").toString());
                                String aux = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(jsonObject != null){
                                medico.setIdPessoa(jsonObject.optInt("id_pessoa"));
                                medico.setNome(jsonObject.optString("st_nome").toString());
                                medico.setSobrenome(jsonObject.optString("st_sobrenome").toString());
                                if(!jsonObject.optString("st_cpf").toString().contains("null")){
                                    medico.setcpf(jsonObject.optString("st_cpf"));
                                } else {
                                    medico.setcpf("Não Informado");
                                }
                                medico.setEmail(jsonObject.optString("st_email"));
                                medico.setDataNascimento(jsonObject.optString("st_datanascimento"));
                                medico.setGenero(jsonObject.optString("st_genero"));
                                medico.setFoto(jsonObject.optString("st_foto"));
                            }

                            if(medico != null){
                                buscaDadosMedico();
                            }

                        } else {
                            progressDialog.dismiss();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                            mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                            mesg.setTitle("");
                                            mesg.setCancelable(false);
                                            mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    carregarDados();
                                                }
                                            });
                                            mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                    dialog.cancel();
                                                }
                                            });
                                            //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                            mesg.show();
                                            // onLoginFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 1000);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
                try{
                    if(error.networkResponse.statusCode == 401){
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                        mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                        mesg.setTitle("");
                                        mesg.setCancelable(false);
                                        mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                carregarDados();
                                            }
                                        });
                                        mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                dialog.cancel();
                                            }
                                        });
                                        //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                        mesg.show();
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    } else {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                        mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                        mesg.setTitle("");
                                        mesg.setCancelable(false);
                                        mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                carregarDados();
                                            }
                                        });
                                        mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                dialog.cancel();
                                            }
                                        });
                                        //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                        mesg.show();
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                    mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                    mesg.setTitle("");
                                    mesg.setCancelable(false);
                                    mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            carregarDados();
                                        }
                                    });
                                    mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            dialog.cancel();
                                        }
                                    });
                                    //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
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
                header.put("Authorization", "Bearer "+ACCESS_TOKEN);
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

    public void buscaDadosMedico(){

        //@requestfields nome, email, sobrenome, genero, dataNascimento, idPessoa, planoDeSaude, descricaoGeral, deficienciaFisica, descricaoDeficienciaFisica, altura, tipoSanguineo, peso (se nenhum parâmetro for passado, todos serão retornados)

        String url = url_base+"/api/medico/retornar?id_pessoa="+medico.getIdPessoa();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.indexOf("medicos") > 0) {

                            if(!response.isEmpty()){
                                JSONObject jsonBusca = null;
                                try {
                                    jsonBusca = new JSONObject(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(jsonBusca != null){
                                    JSONArray jsonArray = null;
                                    try {
                                        JSONObject aux = new JSONObject(jsonBusca.optString("medicos"));
                                        jsonArray = aux.optJSONArray("data");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    JSONObject jsonObject;

                                    if (jsonArray == null || jsonArray.toString().equals("[]")) {
                                        try {

                                        } catch (Exception e) {
                                            //util.msg(e.toString(),"Erro!",Principal.this);
                                        }
                                    } else {
                                        try {
                                            for(int i=0; i<jsonArray.length();i++) {
                                                jsonObject = jsonArray.getJSONObject(i);

                                            /*
                                            "id_medico":"2",
                                            "id_pessoa":"15",
                                            "st_paisorigem":"Brasil",
                                            "st_registromedico":"1235456789",
                                            "st_documentomedico":null,
                                            "created_at":null,
                                            "deleted_at":null,
                                            "st_nome":"Medico",
                                            "st_cpf":null,
                                            "st_sobrenome":"Teste",
                                            "st_email":"medico2@teste.com",
                                            "st_genero":"Masculino",
                                            "st_datanascimento":"23\/03\/1995"
                                            "st_foto":"\/img\/masculino.png
                                            */
                                            medico.setIdMedico(jsonObject.optInt("id_medico"));
                                            medico.setPaisOrigem(jsonObject.optString("st_paisorigem"));
                                            medico.setRegistroMedico(jsonObject.optString("st_registromedico"));
                                            medico.setDocumentoMedico(jsonObject.optString("st_documentomedico"));

                                            }
                                        } catch (Exception e) {
                                            String e1 = e.toString();
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                            if(medico != null){
                                montaTela(medico);
                            }

                        } else {
                            progressDialog.dismiss();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                            mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                            mesg.setTitle("");
                                            mesg.setCancelable(false);
                                            mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    carregarDados();
                                                }
                                            });
                                            mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                    dialog.cancel();
                                                }
                                            });
                                            //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                            mesg.show();
                                            // onLoginFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 1000);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
                try{
                    if(error.networkResponse.statusCode == 401){
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                        mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                        mesg.setTitle("");
                                        mesg.setCancelable(false);
                                        mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                carregarDados();
                                            }
                                        });
                                        mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                dialog.cancel();
                                            }
                                        });
                                        //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                        mesg.show();
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 1000);
                    } else {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                        mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                        mesg.setTitle("");
                                        mesg.setCancelable(false);
                                        mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                carregarDados();
                                            }
                                        });
                                        mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                dialog.cancel();
                                            }
                                        });
                                        //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                        mesg.show();
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(HomeMedicos.this);
                                    mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                    mesg.setTitle("");
                                    mesg.setCancelable(false);
                                    mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            carregarDados();
                                        }
                                    });
                                    mesg.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            dialog.cancel();
                                        }
                                    });
                                    //mesg.setIcon(android.R.drawable.ic_dialog_alert);
                                    mesg.show();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 1000);
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
                header.put("Authorization", "Bearer "+ACCESS_TOKEN);
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

    public void montaTela(Medico medico){

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

        String urlImagem = medico.getFoto().replace(" ", "%20");

        Uri uri = null;
        DraweeController dc = null;

        if(urlImagem.contains("https")){
            uri = Uri.parse(medico.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        } else {
            uri = Uri.parse("https://tocaredev.azurewebsites.net"+medico.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        }
        ivFotoPerfil.setController(dc);

        if(medico.getGenero().equals("Masculino")){
            tvUsername.setText("Dr. " + medico.getNome() + " " + medico.getSobrenome());
        } else if(medico.getGenero().equals("Feminino")) {
            tvUsername.setText("Dra. " + medico.getNome() + " " + medico.getSobrenome());
        } else {
            tvUsername.setText("Dr./Dra. " + medico.getNome() + " " + medico.getSobrenome());
        }

        progressDialog.dismiss();

    }

    @Override
    public void onStop(){
        super.onStop();

        rq.cancelAll("tag");

    }

    public void onClickHomeMedicos(View v){
        if(v.getId() == R.id.tvPacientes || v.getId() == R.id.ic_Pacientes || v.getId() == R.id.layout_PacientesList) {
            Intent i = new Intent(getApplicationContext(), ListaPacientes.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if(v.getId() == R.id.ic_EditarConta || v.getId() == R.id.layoutEditarConta || v.getId() == R.id.tvEditarConta){
            Intent i = new Intent(getApplicationContext(), EditarContaMedico.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

}

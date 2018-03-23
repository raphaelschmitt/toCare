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

import br.com.noctua.tocare.Objetos.Paciente;
import br.com.noctua.tocare.Objetos.Util;

public class HomePaciente extends AppCompatActivity {

    //Internet Connection
    String url_base = "https://tocaredev.azurewebsites.net";
    String url_acesso = "https://tocaredev.azurewebsites.net/api/pessoa/retornar-pessoa-app";
    private Map<String, String> params;
    private RequestQueue rq;

    //itens do xml
    private SimpleDraweeView ivFotoPerfil;
    private TextView tvUsername;

    Button btSair;
    int countBackPressed = 0;
    private ProgressDialog progressDialog;
    private String ACCESS_TOKEN;
    public static Paciente paciente = new Paciente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_home_paciente);

        //internet
        rq = Volley.newRequestQueue(HomePaciente.this);

        ACCESS_TOKEN = LoginPaciente.ACCESS_TOKEN;

        if(!ACCESS_TOKEN.isEmpty() && ACCESS_TOKEN != null){
            carregarDados();
        }

        ivFotoPerfil = (SimpleDraweeView)findViewById(R.id.ivFotoPerfil);
        tvUsername = (TextView)findViewById(R.id.tvUsername);

        btSair = (Button) findViewById(R.id.btLogOut);

        btSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
                mesg.setMessage("Tem certeza que deseja sair?");
                mesg.setTitle("");
                mesg.setCancelable(true);
                mesg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginPaciente.ACCESS_TOKEN = "";
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), LoginOuCadastro.class);
                        startActivity(i);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                mesg.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mesg.show();
            }
        });

    }

    public void carregarDados() {
        progressDialog = new ProgressDialog(HomePaciente.this,
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
                                paciente.setIdPessoa(jsonObject.optInt("id_pessoa"));
                                paciente.setNome(jsonObject.optString("st_nome").toString());
                                paciente.setSobrenome(jsonObject.optString("st_sobrenome").toString());
                                if(!jsonObject.optString("st_cpf").toString().contains("null")){
                                    paciente.setcpf(jsonObject.optString("st_cpf"));
                                } else {
                                    paciente.setcpf("Não Informado");
                                }
                                paciente.setEmail(jsonObject.optString("st_email"));
                                paciente.setDataNascimento(jsonObject.optString("st_datanascimento"));
                                paciente.setGenero(jsonObject.optString("st_genero"));
                                paciente.setFoto(jsonObject.optString("st_foto"));
                            }

                            if(paciente != null){
                                buscaDadosPaciente();
                            }

                        } else {
                            progressDialog.dismiss();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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

    public void buscaDadosPaciente(){

        //@requestfields nome, email, sobrenome, genero, dataNascimento, idPessoa, planoDeSaude, descricaoGeral, deficienciaFisica, descricaoDeficienciaFisica, altura, tipoSanguineo, peso (se nenhum parâmetro for passado, todos serão retornados)

        String url = url_base+"/api/paciente/retornar?id_pessoa="+paciente.getIdPessoa();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.indexOf("pacientes") > 0) {

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
                                        jsonArray = new JSONArray(jsonBusca.optString("pacientes"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    JSONObject jsonObject;

                                    if (jsonArray == null || jsonArray.toString().equals("[]")) {

                                    } else {
                                        try {
                                            for(int i=0; i<jsonArray.length();i++) {
                                                jsonObject = jsonArray.getJSONObject(i);

                                            /*
                                            "id_paciente":"1",
                                            "bl_planodesaude":"1",
                                            "st_descricaogeral":"teste de cadastro de paciente",
                                            "bl_deficienciafisica":"0",
                                            "st_descricaodeficienciafisica":null,
                                            "nu_altura":"1.8500000000000001",
                                            "st_tiposanguineo":"O-",
                                            "nu_peso":"87.0",
                                             */

                                            paciente.setIdPaciente(jsonObject.optInt("id_paciente"));
                                            paciente.setPlanoDeSaude(Util.parseIntBoolean(jsonObject.optInt("bl_planodesaude")));
                                            paciente.setDescricaoGeral(jsonObject.optString("st_descricaogeral"));
                                            paciente.setDeficienciaFisica(Util.parseIntBoolean(jsonObject.optInt("bl_deficienciafisica")));
                                            paciente.setDescricaoDeficiencia(jsonObject.optString("st_descricaodeficienciafisica"));
                                            paciente.setAltura(jsonObject.optDouble("nu_altura"));
                                            paciente.setTipoSanguineo(jsonObject.optString("st_tiposanguineo"));
                                            paciente.setPeso(jsonObject.optDouble("nu_peso"));
                                            paciente.setImc(paciente.getPeso()/(paciente.getAltura()*paciente.getAltura()));

                                            }
                                        } catch (Exception e) {
                                            String e1 = e.toString();
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                            if(paciente != null){
                                montaTela(paciente);
                            }

                        } else {
                            progressDialog.dismiss();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                        AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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
                                    AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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

    public void montaTela(Paciente paciente){

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

        progressDialog.dismiss();

    }

    @Override
    public void onStop(){
        super.onStop();

        rq.cancelAll("tag");

    }

    @Override
    public void onBackPressed() {
        if(countBackPressed >= 1){
            AlertDialog.Builder mesg = new AlertDialog.Builder(HomePaciente.this);
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

    public void onClickHomePaciente(View v){
        if(v.getId() == R.id.ic_FichaMedica || v.getId() == R.id.layoutFichaMedica || v.getId() == R.id.tvFichaMedica){
            Intent i = new Intent(getApplicationContext(), FichaMedica.class);
            i.putExtra("origem", "homePaciente");
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if(v.getId() == R.id.ic_EditarConta || v.getId() == R.id.layoutEditarConta || v.getId() == R.id.tvEditarConta){
            Intent i = new Intent(getApplicationContext(), EditarConta.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if(v.getId() == R.id.ic_FichaConsulta || v.getId() == R.id.layoutFichaConsulta || v.getId() == R.id.tvFichaConsulta){
            Intent i = new Intent(getApplicationContext(), FichaConsulta.class);
            i.putExtra("origem", "homePaciente");
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if(v.getId() == R.id.ic_Medicos || v.getId() == R.id.layoutMedicos || v.getId() == R.id.tvMedicos){
            Intent i = new Intent(getApplicationContext(), ListaMedicos.class);
            i.putExtra("origem", "homePaciente");
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

}

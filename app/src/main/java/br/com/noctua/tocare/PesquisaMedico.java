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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Adapters.MedicosAdapter;
import br.com.noctua.tocare.Objetos.Medico;
import br.com.noctua.tocare.Objetos.Paginacao;

public class PesquisaMedico extends AppCompatActivity implements AbsListView.OnScrollListener {

    //Internet Connection
    private String url_base = "https://tocaredev.azurewebsites.net";
    private String url_acesso = "https://tocaredev.azurewebsites.net/api/medico/retornar";
    private Map<String, String> params;
    private ProgressDialog progressDialog;
    private RequestQueue rq;
    private Paginacao paginacao = new Paginacao();
    private RelativeLayout layout_noResults;

    private ListView lv_medicos;
    private Toolbar mToolbar;
    private MedicosAdapter medicosAdapter;
    private ArrayList<Medico> listMedicos = new ArrayList<Medico>();;

    private ImageView bt_Search;
    private EditText edtCaixaPesquisa;

    boolean execAsyncTask;
    boolean caixaPesquisaVazia = true;

    private LinearLayout linlaHeaderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_pesquisa_medico);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Adicionar Médico");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_forward_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //internet
        rq = Volley.newRequestQueue(PesquisaMedico.this);
        paginacao.setPaginaAtual(1);
        paginacao.setNextPage(false);

        layout_noResults = (RelativeLayout)findViewById(R.id.layout_noResults);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        edtCaixaPesquisa = (EditText) findViewById(R.id.edtCaixaPesquisa);

        bt_Search = (ImageView) findViewById(R.id.bt_search);

        bt_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if(!edtCaixaPesquisa.getText().toString().isEmpty()){
                    caixaPesquisaVazia = false;
                } else {
                    caixaPesquisaVazia = true;
                }
                getMedicosPesquisar();
            }
        });

        lv_medicos = (ListView) findViewById(R.id.lv_medicos);
        getMedicosOnScroll();
        lv_medicos.setOnScrollListener(this);

        lv_medicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> medicosAdapterView, View view, int position, long l) {

                final Medico medicoAux = listMedicos.get(position);

                AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
                mesg.setMessage("Do you want to add Dr. " + medicoAux.getNome() + "as one of your Doctors? (By doing so the doctor in question will have access to" +
                        " your medical record and you can edit your medical record.)");
                mesg.setTitle("");
                mesg.setCancelable(true);
                mesg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        progressDialog = new ProgressDialog(PesquisaMedico.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Adding...");
                        progressDialog.show();
                        adicionarMedicos(HomePaciente.paciente.getIdPaciente(), medicoAux.getIdMedico());
                    }
                });
                mesg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mesg.show();
            }
        });

    }

    public void getMedicosPesquisar(){

        // SHOW THE SPINNER WHILE LOADING FEEDS
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        lv_medicos.setVisibility(View.GONE);
        layout_noResults.setVisibility(View.GONE);

        lv_medicos.setClickable(false);

        callByStringRequest(true);

    }

    public void getMedicosOnScroll(){

        // SHOW THE SPINNER WHILE LOADING FEEDS
        //linlaHeaderProgress.setVisibility(View.VISIBLE);
        //lv_medicos.setVisibility(View.GONE);
        layout_noResults.setVisibility(View.GONE);

        if(listMedicos.size() == 0){
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            lv_medicos.setVisibility(View.GONE);
        }

        //lv_medicos.setClickable(false);

        callByStringRequest(false);

    }

    public void callByStringRequest(final boolean btPesquisar){

        String url = "";
        final ArrayList<Medico> tempList = new ArrayList<>();

        if(btPesquisar){
            url = url_acesso+"?st_nome="+edtCaixaPesquisa.getText()+"&page="+paginacao.getPaginaAtual();
        } else {
            url = url_acesso+"?page="+paginacao.getPaginaAtual();
        }

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
                            if(jsonBusca != null){
                                JSONArray jsonArray = null;
                                try {
                                    JSONObject aux = new JSONObject(jsonBusca.optString("medicos"));
                                    paginacao.setTotalPaginas(aux.optInt("last_page"));
                                    if(aux.optString("next_page_url").toString().equals("null")){
                                        paginacao.setNextPage(false);
                                    } else {
                                        paginacao.setNextPage(true);
                                    }
                                    if(aux.optString("prev_page_url").toString().equals("null")){
                                        paginacao.setPrevPage(false);
                                    } else {
                                        paginacao.setPrevPage(true);
                                    }
                                    paginacao.setPaginaAtual(aux.optInt("current_page"));
                                    jsonArray = aux.optJSONArray("data");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                JSONObject jsonObject;

                                if (jsonArray == null || jsonArray.toString().equals("[]")) {
                                    try {
                                        lv_medicos.setVisibility(View.GONE);
                                        layout_noResults.setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        //util.msg(e.toString(),"Erro!",Principal.this);
                                    }
                                } else {
                                    lv_medicos.setVisibility(View.VISIBLE);
                                    layout_noResults.setVisibility(View.GONE);

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

                                            Medico medico = new Medico();
                                            medico.setIdMedico(jsonObject.optInt("id_medico"));
                                            medico.setIdPessoa(jsonObject.optInt("id_pessoa"));
                                            medico.setPaisOrigem(jsonObject.optString("st_paisorigem"));
                                            medico.setRegistroMedico(jsonObject.optString("st_registromedico"));
                                            medico.setDocumentoMedico(jsonObject.optString("st_documentomedico"));
                                            medico.setcpf(jsonObject.optString("st_cpf"));
                                            medico.setNome(jsonObject.optString("st_nome"));
                                            medico.setSobrenome(jsonObject.optString("st_sobrenome"));
                                            medico.setEmail(jsonObject.optString("st_email"));
                                            medico.setDataNascimento(jsonObject.optString("st_datanascimento"));
                                            medico.setGenero(jsonObject.optString("st_genero"));
                                            medico.setFoto(jsonObject.optString("st_foto"));
                                            tempList.add(medico);

                                        }
                                    } catch (Exception e) {
                                        String e1 = e.toString();
                                        e.printStackTrace();
                                    }

                                    if(btPesquisar){
                                        // Cria a lista, caso ela não esteja criada
                                        if (listMedicos == null)
                                            listMedicos = new ArrayList<Medico>();

                                        // Limpa a sua lista de livros e adiciona todos os registros da lista temporária
                                        listMedicos.clear();
                                        listMedicos.addAll(tempList);

                                        // Se o adapter for null, cria o adapter, se não notifica que seu dataset teve alteração (No seu caso a lista de livros).
                                        if (medicosAdapter == null) {
                                            medicosAdapter = new MedicosAdapter(PesquisaMedico.this, R.layout.item_medico_list, listMedicos);
                                            lv_medicos.setAdapter(medicosAdapter);
                                            hideSoftKeyboard();
                                        } else {
                                            medicosAdapter = new MedicosAdapter(PesquisaMedico.this, R.layout.item_medico_list, listMedicos);
                                            lv_medicos.setAdapter(medicosAdapter);
                                            hideSoftKeyboard();
                                        }
                                    } else {
                                        // Cria a lista, caso ela não esteja criada
                                        if (listMedicos == null)
                                            listMedicos = new ArrayList<Medico>();

                                        //Adiciona todos os registros da lista temporária
                                        listMedicos.addAll(tempList);

                                        // Se o adapter for null, cria o adapter, se não notifica que seu dataset teve alteração (No seu caso a lista de livros).
                                        //if (medicosAdapter == null) {
                                            medicosAdapter = new MedicosAdapter(PesquisaMedico.this, R.layout.item_medico_list, listMedicos);
                                            lv_medicos.setAdapter(medicosAdapter);
                                            hideSoftKeyboard();
                                        /*} else {
                                            medicosAdapter.notifyDataSetChanged();
                                            hideSoftKeyboard();
                                        }*/
                                    }

                                }
                            }
                        }
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        linlaHeaderProgress.setVisibility(View.GONE);
                        lv_medicos.setClickable(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
                                mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                mesg.setTitle("");
                                mesg.setCancelable(false);
                                mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if(btPesquisar){
                                            getMedicosPesquisar();
                                        } else {
                                            getMedicosOnScroll();
                                        }
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

                                // HIDE THE SPINNER AFTER LOADING FEEDS
                                linlaHeaderProgress.setVisibility(View.GONE);
                                lv_medicos.setClickable(true);
                            }
                        }, 1000);
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
                header.put("Authorization", "Bearer "+ LoginPaciente.ACCESS_TOKEN);
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

    public void adicionarMedicos(int id_paciente, int id_medico){

        //requestFields id_paciente, id_medico

        String url = url_base + "/api/paciente/compartilhar-prontuario?id_paciente="+id_paciente+"&id_medico="+id_medico;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(!response.isEmpty()){

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(jsonObject != null){
                                AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
                                mesg.setMessage(jsonObject.optString("mensagem"));
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
                                AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(jsonObject != null){
                            AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
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
                            AlertDialog.Builder mesg = new AlertDialog.Builder(PesquisaMedico.this);
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
                header.put("Authorization", "Bearer "+ LoginPaciente.ACCESS_TOKEN);
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
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(absListView.getId() == lv_medicos.getId()){
            if(paginacao.isNextPage()){
                if(absListView.getLastVisiblePosition() + 1 == listMedicos.size()){
                    paginacao.setPaginaAtual(paginacao.getPaginaAtual()+1);
                    getMedicosOnScroll();
                }
            }
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ListaMedicos.class);
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
            Intent i = new Intent(getApplicationContext(), ListaMedicos.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

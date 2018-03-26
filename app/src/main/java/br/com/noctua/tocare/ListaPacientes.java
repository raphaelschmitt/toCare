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
import android.widget.AbsListView;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.Adapters.PacientesAdapter;
import br.com.noctua.tocare.Objetos.Paciente;
import br.com.noctua.tocare.Objetos.Paginacao;
import br.com.noctua.tocare.Objetos.Util;

public class ListaPacientes extends AppCompatActivity implements AbsListView.OnScrollListener {

    //Internet Connection
    private String url_base = "https://tocaredev.azurewebsites.net";
    String url_acesso = "https://tocaredev.azurewebsites.net/api/paciente/retornar-pacientes-compartilhados";
    private Map<String, String> params;
    private ProgressDialog progressDialog;
    private RequestQueue rq;
    private Paginacao paginacao = new Paginacao();

    ListView lv_pacientes;
    private Toolbar mToolbar;
    PacientesAdapter pacientesAdapter;
    ArrayList<Paciente> listPacientes = new ArrayList<Paciente>();
    RelativeLayout layout_noResults;
    private LinearLayout linlaHeaderProgress;

    public static Paciente pacienteDetalhes = new Paciente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pacientes);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        layout_noResults = (RelativeLayout)findViewById(R.id.layout_noResults);

        //internet
        rq = Volley.newRequestQueue(ListaPacientes.this);
        paginacao.setPaginaAtual(1);
        paginacao.setNextPage(false);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.meus_pacientes);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_forward_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        
        lv_pacientes = (ListView) findViewById(R.id.lv_pacientes);
        getPacientes(true);
        lv_pacientes.setOnScrollListener(this);

        lv_pacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                pacienteDetalhes = listPacientes.get(position);

                Intent i = new Intent(getApplicationContext(), DetalhesPaciente.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPacientes(boolean clear){

        // SHOW THE SPINNER WHILE LOADING FEEDS
        //linlaHeaderProgress.setVisibility(View.VISIBLE);
        //lv_pacientes.setVisibility(View.GONE);
        layout_noResults.setVisibility(View.GONE);

        //lv_pacientes.setClickable(false);

        callByStringRequest(clear);
        
    }

    public void callByStringRequest(final boolean clear){

        String url = "";

        final ArrayList<Paciente> tempList = new ArrayList<>();

        if(paginacao.isNextPage()){
            url = url_acesso+"?id_medico="+HomeMedicos.medico.getIdMedico()+"&page="+paginacao.getPaginaAtual()+1;
        } else {
            url = url_acesso+"?id_medico="+HomeMedicos.medico.getIdMedico()+"&page="+paginacao.getPaginaAtual();
        }

        if(clear){
            lv_pacientes.setVisibility(View.GONE);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
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
                                    JSONObject aux = new JSONObject(jsonBusca.optString("pacientes"));
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
                                        lv_pacientes.setVisibility(View.GONE);
                                        layout_noResults.setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        //util.msg(e.toString(),"Erro!",Principal.this);
                                    }
                                } else {
                                    lv_pacientes.setVisibility(View.VISIBLE);
                                    layout_noResults.setVisibility(View.GONE);

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

                                            Paciente paciente = new Paciente();

                                            paciente.setIdPaciente(jsonObject.optInt("id_paciente"));
                                            paciente.setPlanoDeSaude(Util.parseIntBoolean(jsonObject.optInt("bl_planodesaude")));
                                            paciente.setDescricaoGeral(jsonObject.optString("st_descricaogeral"));
                                            paciente.setDeficienciaFisica(Util.parseIntBoolean(jsonObject.optInt("bl_deficienciafisica")));
                                            paciente.setDescricaoDeficiencia(jsonObject.optString("st_descricaodeficienciafisica"));
                                            paciente.setAltura(jsonObject.optDouble("nu_altura"));
                                            paciente.setTipoSanguineo(jsonObject.optString("st_tiposanguineo"));
                                            paciente.setPeso(jsonObject.optDouble("nu_peso"));
                                            paciente.setImc(paciente.getPeso()/(paciente.getAltura()*paciente.getAltura()));

                                            paciente.setIdPessoa(jsonObject.optInt("id_pessoa"));
                                            paciente.setcpf(jsonObject.optString("st_cpf"));
                                            paciente.setNome(jsonObject.optString("st_nome"));
                                            paciente.setSobrenome(jsonObject.optString("st_sobrenome"));
                                            paciente.setEmail(jsonObject.optString("st_email"));
                                            paciente.setDataNascimento(jsonObject.optString("st_datanascimento"));
                                            paciente.setGenero(jsonObject.optString("st_genero"));
                                            paciente.setFoto(jsonObject.optString("st_foto"));
                                            paciente.setIdPacienteMedico(jsonObject.optInt("id_pacientemedico"));

                                            tempList.add(paciente);

                                        }
                                    } catch (Exception e) {
                                        String e1 = e.toString();
                                        e.printStackTrace();
                                    }

                                    if(clear){
                                        // Cria a lista, caso ela não esteja criada
                                        if (listPacientes == null)
                                            listPacientes = new ArrayList<Paciente>();

                                        // Limpa a sua lista de livros e adiciona todos os registros da lista temporária
                                        listPacientes.clear();
                                        listPacientes.addAll(tempList);

                                        // Se o adapter for null, cria o adapter, se não notifica que seu dataset teve alteração (No seu caso a lista de livros).
                                        if (pacientesAdapter == null) {
                                            pacientesAdapter = new PacientesAdapter(ListaPacientes.this, R.layout.item_medico_list, listPacientes);
                                            lv_pacientes.setAdapter(pacientesAdapter);
                                        } else {
                                            pacientesAdapter = new PacientesAdapter(ListaPacientes.this, R.layout.item_medico_list, listPacientes);
                                            lv_pacientes.setAdapter(pacientesAdapter);
                                        }
                                    } else {
                                        // Cria a lista, caso ela não esteja criada
                                        if (listPacientes == null)
                                            listPacientes = new ArrayList<Paciente>();

                                        //Adiciona todos os registros da lista temporária
                                        listPacientes.addAll(tempList);

                                        // Se o adapter for null, cria o adapter, se não notifica que seu dataset teve alteração (No seu caso a lista de livros).
                                        //if (pacientesAdapter == null) {
                                        pacientesAdapter = new PacientesAdapter(ListaPacientes.this, R.layout.item_medico_list, listPacientes);
                                        lv_pacientes.setAdapter(pacientesAdapter);
                                        /*} else {
                                            pacientesAdapter.notifyDataSetChanged();
                                        }*/
                                    }

                                }
                            }
                        }
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        linlaHeaderProgress.setVisibility(View.GONE);
                        lv_pacientes.setClickable(true);
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
                                AlertDialog.Builder mesg = new AlertDialog.Builder(ListaPacientes.this);
                                mesg.setMessage("Houve algum erro por favor, tente mais tarde!");
                                mesg.setTitle("");
                                mesg.setCancelable(false);
                                mesg.setNeutralButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        getPacientes(true);
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
                                // onLoginFailed();
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
                header.put("Authorization", "Bearer "+ LoginMedico.ACCESS_TOKEN);
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
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(absListView.getId() == lv_pacientes.getId()){
            if(paginacao.isNextPage()){
                if(absListView.getLastVisiblePosition() + 1 == listPacientes.size()){
                    paginacao.setPaginaAtual(paginacao.getPaginaAtual()+1);
                    getPacientes(false);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

}

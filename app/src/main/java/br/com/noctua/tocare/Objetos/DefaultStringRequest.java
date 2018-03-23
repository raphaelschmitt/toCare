package br.com.noctua.tocare.Objetos;

import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.noctua.tocare.LoginPaciente;

/**
 * Created by Raphael on 09/03/2018.
 */

public class DefaultStringRequest {

    //Internet Connection
    String url_acesso = "https://tocaredev.azurewebsites.net/api/pessoa/retornar-pessoa-app";
    private Map<String, String> params;
    private RequestQueue rq;

    public void callByStringRequest(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_acesso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
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

}

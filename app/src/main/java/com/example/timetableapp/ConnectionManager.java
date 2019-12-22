package com.example.timetableapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    private final String SERVER_LOGIN_URL = "/auth/local/login";
    private String server_url;
    private Context context;
    private RequestQueue queue;

    public ConnectionManager(String server_url, Context context){
        this.server_url = server_url;
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }


    public void getStringRequest(String URL, Response.Listener<String> listener){
        StringRequest request = new StringRequest(Request.Method.GET, server_url + URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT);
            }
        });
        queue.add(request);
    }

    public void getJSONRequest(String URL, Response.Listener<JSONObject> listener){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, server_url + URL, null, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT);
            }
        });
        queue.add(request);
    }

    public void postJSONRequest(String URL, JSONObject params, Response.Listener<JSONObject> listener){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url + URL, params, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT);
            }
        });
        queue.add(request);
    }

    public void authPostJSONRequest(String URL, JSONObject params, Response.Listener<JSONObject> listener){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url + URL, params, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                String token = context.getSharedPreferences("user_info",Context.MODE_PRIVATE).getString("token","");
                headers.put("Content-type","application/json");
                headers.put("Authorization","Bearer "+token);
                return headers;
            }
        };
        queue.add(request);
    }
}

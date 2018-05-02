package ph.pey.finalproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.pey.finalproject.sql.MatchEntity;

public class BackendManager {

    private final String BACKEND = "http://104.131.45.206/";
    private Context context;
    private BackendResponseListener listener;
    private RequestQueue requestQueue;

    public BackendManager(Context context, BackendResponseListener listener) {
        this.context = context;
        this.listener = listener;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getAllMatches() {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, BACKEND, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<MatchEntity> list = new ArrayList<>();
                        for(int i = 0; i < response.length(); i++) {
                            try {
                                list.add((MatchEntity) response.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.allMatches(list.toArray(new MatchEntity[response.length()]));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Backend unreachable", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(arrayRequest);
    }

    public void saveMatch(MatchEntity matchEntity) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(matchEntity));
            Log.e("erf", new Gson().toJson(matchEntity));
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    listener.matchSaved();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Backend unreachable", Toast.LENGTH_SHORT).show();
                    listener.matchSaved();
                }
            });
            requestQueue.add(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface BackendResponseListener {
        void allMatches(MatchEntity[] matchEntities);
        void matchSaved();
    }
}

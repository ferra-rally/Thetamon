package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;

public abstract class VolleyMove implements Response.ErrorListener, Response.Listener<String> {
    private Context context;

    public VolleyMove(Context context) {
        this.context = context;
    }

    public abstract void fill(Move move);

    public void getMoveDetail(Move move) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                context.getString(R.string.url_move_detail) +  move.getName(),
                this,
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.volley_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(response);
            String lang = context.getResources().getString(R.string.localization);

            Move move = gson.fromJson(response, Move.class);

            move.setDamageClass(jsonObject.getJSONObject("damage_class").getString("name"));
            move.setTarget(jsonObject.getJSONObject("target").getString("name"));

            JSONArray effects = jsonObject.getJSONArray("effect_entries");
            JSONObject effectObj = null;

            for (int index = 0; index < effects.length(); index++){
                effectObj = effects.getJSONObject(index);
                if("en".equals(effectObj.getJSONObject("language").getString("name")))
                    break;
            }

            assert effectObj != null;
            String effect = effectObj.getString("effect");
            if(!jsonObject.isNull("effect_chance")) {
                int effect_chance = jsonObject.getInt("effect_chance");

                String[] tmp1 = effect.split("\\$");
                String[] tmp2 = tmp1[1].split("%");

                move.setEffect(tmp1[0].concat(effect_chance + tmp2[1]));
            }

            JSONArray flavorTexts = jsonObject.getJSONArray("flavor_text_entries");
            for(int i = flavorTexts.length() - 1; i >= 0; i--){
                JSONObject tmp = flavorTexts.getJSONObject(i);

                if (lang.equals(tmp.getJSONObject("language").getString("name"))){
                    move.setFlavorText(String.format("%s version: %s",
                            tmp.getString("flavor_text"), tmp.getJSONObject("version_group").getString("name")));
                    break;
                }
            }

            fill(move);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}

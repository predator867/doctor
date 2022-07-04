package legend.sma.uos.Fcm;


import static legend.sma.uos.Fcm.FCM_API.CONTENT_TYPE;
import static legend.sma.uos.Fcm.FCM_API.KEY;
import static legend.sma.uos.Fcm.FCM_API.URL_STRING;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCM_Notification {


    public FCM_Notification(String userSendToken, String title, String msg, Context context) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        Log.d("TAG", "userSendToken: " + userSendToken);
        jsonObject.put("to", userSendToken);
        JSONObject notifection = new JSONObject();
        notifection.put("title", title);
        notifection.put("body", msg);

        JSONObject extraData = new JSONObject();
        //extraData.put(FCM_ContentRef.USER_SEND_TOKEN, userSendToken);


        jsonObject.put("notification", notifection);
        jsonObject.put("data", extraData);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_STRING,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "JSONObject: " + response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "re: " + error, Toast.LENGTH_SHORT).show();
                        //   Log.d("TAG", error.getMessage());
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> header = new HashMap<>();
                header.put("content-type", CONTENT_TYPE);
                header.put("authorization", KEY);
                return header;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);


    }


}

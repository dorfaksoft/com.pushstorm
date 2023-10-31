package com.pushstorm;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.data.ApnSetting;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushStorm {
    public static String serviceKey = "";
    public static void init(final Context context) {
        Bundle metaData = null;
        try {
            metaData = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                serviceKey = metaData.getString("com.pushstorm.token");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        FirebaseApp.initializeApp(context);
        FirebaseMessaging.getInstance().subscribeToTopic(serviceKey)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {

                    }
                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        registerUser(context, token,serviceKey);
                    }
                });
    }

    private static void registerUser(Context context,String token,String serviceKey) {
        final String url = AppSetting.DOMAIN+"/api/register_fcm_token";
        final Map<String, String> params = new HashMap<String, String>();
        params.put("device", Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        params.put("register_token",token );
        params.put("service_key",serviceKey );
        params.put("is_app","1" );
        Response.Listener<String> listener = new Response.Listener<String>() {
            public void onResponse(String s) {


            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError e) {
                NetworkResponse networkResponse = e.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                } else {
                }

            }
        };
        StringRequest postRequest = new StringRequest(1, url, listener, errorListener) {
            protected Map<String, String> getParams() {
                return params;
            }

//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return headers;
//            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 1.0F));
        Volley.newRequestQueue(context).add(postRequest);
    }

    public static void setNotificationListener(NotificationListener callback) {
        PushStormMessagingService.pushpoleNotificationListener = callback;
    }
    //    public static NotificationListener getNotificationListener() {
//       return pushpoleNotificationListener;
//    }
    public interface NotificationListener {
        void onNotificationReceived(NotificationData notificationData);
        void onNotificationClicked(NotificationData notificationData);

        void onNotificationButtonClicked(NotificationData notificationData, NotificationButtonData clickedButton);

        void onCustomContentReceived(JSONObject customContent);

        void onNotificationDismissed(NotificationData notificationData);
    }
}
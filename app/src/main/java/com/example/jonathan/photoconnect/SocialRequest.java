package com.example.jonathan.photoconnect;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SocialRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "https://size-majors.000webhostapp.com/FacebookLogin.php";
    private Map<String, String> params;

    public SocialRequest(String fbid, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("fbid",fbid);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
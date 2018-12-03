package com.kgec.mca.rars;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {
    EditText fname,lname,email,password,ph;
    ProgressDialog pDialog;
    Button register;
    TextView backtologin;
    public String eemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        ph = (EditText) findViewById(R.id.phone);
        register = (Button) findViewById(R.id.register);
        backtologin = (TextView)findViewById(R.id.backtologin);
        pDialog=new ProgressDialog(this);
        pDialog.setCancelable(false);


        //getSupportActionBar().setTitle("Register");
        // implement setOnClickListener event on sign up Button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate the fields and call sign method to implement the api

                    register();

            }
        });
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate the fields and call sign method to implement the api

                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);

            }
        });
    }


    private void register() {
        String tag_string_req = "req_login";
       // pDialog.setMessage("lllll");
        pDialog.setMessage("Registering...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/register.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            public static final String TAG = "Volly Message" ;

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                hideDialog();
                //Toast.makeText(getApplicationContext(), "Select Required Choice", Toast.LENGTH_LONG).show();

                Intent i = new Intent(RegisterActivity.this, ChooseField.class);

                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL",email.getText().toString());
                i.putExtras(bundle);
                startActivity(i);

            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Error Message";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("fname", fname.getText().toString());
                params.put("lname", lname.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("phone_no", ph.getText().toString());
                params.put("college", "");
                params.put("department","");
                params.put("interest", "null");
            /*params.put("email", etemail.getText().toString());
            params.put("phone_no", etphone.getText().toString());
            params.put("dob", etbirth.getText().toString());
            params.put("aniversary", etanniv.getText().toString());
            params.put("suggest", etsuggest.getText().toString());
            params.put("collection", String.valueOf(rtcol.getRating()));
            params.put("service", String.valueOf(rtserv.getRating()));
            params.put("ambience", String.valueOf(rtamb.getRating()));
            params.put("valueformoney", String.valueOf(rtvfm.getRating()));
            params.put("staff", String.valueOf(rtstaff.getRating()));
            params.put("overall", String.valueOf(rtoverall.getRating()));
            params.put("mar", String.valueOf(marstat.getText()));
            params.put("recom", String.valueOf(recommend.getText()));
            params.put("source", String.valueOf(know.getText()));
            params.put("exp", String.valueOf(experience.getText()));
            //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());*/
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

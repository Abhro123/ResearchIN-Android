package com.kgec.mca.rars;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kgec.mca.rars.helper.SQLiteHandler;
import com.kgec.mca.rars.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadMore extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;
    RecyclerView recyclerView;
    ArrayList<String> names;
    Button reportbtn,downloadbtn,reportpaper;
    TextView txt_name,txt_short,txt_long,txt_download,txt_user;
    String user,email,name,pdfurl,long_desc,short_desc,download,pdfname;
    int ishidden;
    JSONArray data1,data2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more);

        getSupportActionBar().setTitle("Paper Details");

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("USER");
        name = bundle.getString("NAME");
        pdfurl = bundle.getString("PDFURL");
        ishidden = bundle.getInt("ISHIDDEN");
        long_desc = bundle.getString("LONG_DESC");
        short_desc = bundle.getString("SHORT_DESC");
        download = bundle.getString("DOWNLOAD");
        pdfname = bundle.getString("PDFNAME");

        txt_name=findViewById(R.id.header_text);
        txt_user=findViewById(R.id.publisher_name);
        txt_short=findViewById(R.id.shortdesc);
        txt_long=findViewById(R.id.blog_content);
        txt_download=findViewById(R.id.comment_number);
        reportpaper=findViewById(R.id.reportppr);

        txt_user.setText("Uploaded by:  "+name);
        txt_short.setText(short_desc);
        txt_long.setText(long_desc);
        txt_download.setText(download);
        txt_name.setText(bundle.getString("PDFNAME"));

        reportbtn=findViewById(R.id.requestpaper);
        downloadbtn=findViewById(R.id.viewpaper);

        if (ishidden==0){
            downloadbtn.setVisibility(View.VISIBLE);
            reportbtn.setVisibility(View.INVISIBLE);
        }
        else {
            downloadbtn.setVisibility(View.INVISIBLE);
            reportbtn.setVisibility(View.VISIBLE);
        }


        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();

        email = user.get("email");

        reportpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportpap();
            }
        });


    }

    private void reportpap() {

        String tag_string_req = "req_login";


        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/report_paper.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            public static final String TAG = "Login";

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);

                Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Hello";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("PdfURL", pdfurl);
                params.put("reporting", user);
                params.put("short_desc", pdfname);

                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}


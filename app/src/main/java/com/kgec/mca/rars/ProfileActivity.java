package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.UUID;

import com.kgec.mca.rars.helper.SQLiteHandler;
import com.kgec.mca.rars.helper.SessionManager;


public class ProfileActivity extends BaseActivity {


    private TextView name;
    private TextView mail;
    private TextView department, pub,followers,coll,phn;
    private ProgressDialog pDialog;
    Button SelectButton, UploadButton, View;
    private SQLiteHandler db;
    private SessionManager session;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    JSONArray data1;

    EditText PdfNameEditText;

    Uri uri;

    public static final String PDF_UPLOAD_HTTP_URL = "http://suhrid1theinceptor.000webhostapp.com/userlogin/file_upload.php";

    public int PDF_REQ_CODE = 1;

    String PdfNameHolder, PdfPathHolder, PdfID, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_profile, this);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);

        getSupportActionBar().setTitle("Profile");

        Bundle bundle = getIntent().getExtras();
        name = (TextView) findViewById(R.id.user_profile_name);
        mail = (TextView) findViewById(R.id.email);
        department = (TextView) findViewById(R.id.department);
        pub = (TextView) findViewById(R.id.publication);
        followers = (TextView)findViewById(R.id.followers);
        coll = (TextView)findViewById(R.id.college);
        phn = (TextView)findViewById(R.id.phone);
        //Extract the data…
        //email = bundle.getString("EMAIL");


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();

        email = user.get("email");

        //SelectButton = (Button) findViewById(R.id.button);
        UploadButton = (Button) findViewById(R.id.button2);
        View = (Button) findViewById(R.id.button3);
        //PdfNameEditText = (EditText) findViewById(R.id.editText);

       /* SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // PDF selection code start from here .

                Intent intent = new Intent();

                intent.setType("application/pdf");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);

            }
        });*/

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), PdfUpload.class);

                Bundle bundle = new Bundle();
//Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL", email);
//Add the bundle to the intent
                i.putExtras(bundle);
                startActivity(i);

            }
        });

        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ViewPapers.class);
                Bundle bundle = new Bundle();
//Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL", email);
//Add the bundle to the intent
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        profile(email);

    }

    private void profile(final String email) {

        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Profile..");

        showDialog();

        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/profile.php?email=" + email;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public static final String TAG = "Login";


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();
                //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();

                try {
                    JSONObject json = new JSONObject(response);

                    data1 = json.getJSONArray("respond");
                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject jObj = data1.getJSONObject(i);
                            String fsname = jObj.getString("fname");
                            String ddept = jObj.getString("department");
                            String mmail = jObj.getString("email");
                            String publications = jObj.getString("publications");
                            String phone_no = jObj.getString("phone_no");
                            String college = jObj.getString("college");
                            String lsname = jObj.getString("lname");
                            String folow = jObj.getString("followers");
                            name.setText(fsname + " " + lsname);
                            department.setText(ddept);
                            coll.setText(college);
                            phn.setText(phone_no);
                            mail.setText(mmail);
                            followers.setText(folow);
                            pub.setText(publications);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Hello";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }





    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }


}
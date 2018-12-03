package com.kgec.mca.rars;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import mabbas007.tagsedittext.TagsEditText;

import static com.kgec.mca.rars.ProfileActivity.PDF_UPLOAD_HTTP_URL;

public class PdfUpload extends AppCompatActivity implements TagsEditText.TagsEditListener {
    Button SelectButton, UploadButton;
    EditText PdfNameEditText,shortdescrip,longdescrip;
    String PdfNameHolder, PdfPathHolder, PdfID, email,vshort,vlong,vfield,coauthor;
    public int PDF_REQ_CODE = 1;
    private TextView name;
    private TextView mail;
    private TextView department;
    Uri uri;
    private TagsEditText field,coauth;
    private static final String TAG = "PDFUpload";
    CheckBox cb1;
    private ArrayList<String> product;
    JSONArray data;
    int subs;

    public static final String PDF_UPLOAD_HTTP_URL = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/file_upload.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);
        SelectButton = (Button) findViewById(R.id.button);
        UploadButton = (Button) findViewById(R.id.button2);
        PdfNameEditText = (EditText) findViewById(R.id.editText);
        shortdescrip = findViewById(R.id.shortdesc);
        longdescrip = findViewById(R.id.longdesc);
        field = findViewById(R.id.editText2);
        coauth=findViewById(R.id.editText3);
        getSupportActionBar().setTitle("Upload Paper");
        cb1=findViewById(R.id.cb1);
        product = new ArrayList<String>();
        field.setTagsListener(this);
        field.setTagsWithSpacesEnabled(true);
        field.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.field)));
        field.setThreshold(1);

        coauth.setTagsListener(this);
        coauth.setTagsWithSpacesEnabled(true);

        coauth.setThreshold(1);

        String tag_string_req = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/users.php";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                //Toast.makeText(getApplicationContext(),"Order added", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    data = jObj.getJSONArray("respond");

                    for (int i = 0; i < data.length(); i++) {
                        try {
                            //Getting json object
                            JSONObject json = data.getJSONObject(i);

                            //Adding the name of the student to array list
                            product.add(json.getString("fname"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    coauth.setAdapter(new ArrayAdapter<String>(PdfUpload.this,
                            android.R.layout.simple_dropdown_item_1line,product));
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
               // hideDialog();
            }
        }) {


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);





        AllowRunTimePermission();

        SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // PDF selection code start from here .

                Intent intent = new Intent();

                intent.setType("application/pdf");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);

            }


        });

        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        email = bundle.getString("EMAIL");

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb1.isChecked()) {
                    subs=1;
                }
                else{
                    subs=0;
                }
                PdfUploadFunction();
                Intent i = new Intent(PdfUpload.this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL",email);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            field.dismissDropDown();
            coauth.dismissDropDown();
        }
        else {
            field.showDropDown();
            coauth.showDropDown();
        }
    }


    @Override
    public void onTagsChanged(Collection<String> tags) {
        Log.d(TAG, "Tags changed: ");
        Log.d(TAG, Arrays.toString(tags.toArray()));
    }

    @Override
    public void onEditingFinished() {
        Log.d(TAG,"OnEditing finished");
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
//        //mTagsEditText.clearFocus();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            SelectButton.setText("PDF is Selected");
        }
    }

    public void PdfUploadFunction() {

        PdfNameHolder = PdfNameEditText.getText().toString().trim();
        vshort = shortdescrip.getText().toString().trim();
        vlong = longdescrip.getText().toString().trim();
        vfield = field.getText().toString().trim();
        coauthor=coauth.getText().toString().trim();
        PdfPathHolder = FilePath.getPath(this, uri);

        if (PdfPathHolder == null) {

            Toast.makeText(this, "Please move your PDF file to internal storage & try again.", Toast.LENGTH_LONG).show();

        } else {

            try {

                PdfID = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, PdfID, PDF_UPLOAD_HTTP_URL)
                        .addFileToUpload(PdfPathHolder, "pdf")
                        .addParameter("name", PdfNameHolder)
                        .addParameter("short_desc",vshort)
                        .addParameter("long_desc",vlong)
                        .addParameter("field",vfield)
                        .addParameter("co_author",coauthor)
                        .addParameter("email", email)
                        .addParameter("is_hidden",String.valueOf(subs))
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();
               // Toast.makeText(this,vfield, Toast.LENGTH_LONG).show();

                 Toast.makeText(this, "Paper Uploaded Successfully", Toast.LENGTH_LONG).show();
                PdfNameEditText.setText("");
                shortdescrip.setText("");
                longdescrip.setText("");
                field.setText("");

            } catch (Exception exception) {

                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void AllowRunTimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(PdfUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {

            Toast.makeText(PdfUpload.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(PdfUpload.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(PdfUpload.this,"Permission Granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(PdfUpload.this,"Permission Canceled", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}

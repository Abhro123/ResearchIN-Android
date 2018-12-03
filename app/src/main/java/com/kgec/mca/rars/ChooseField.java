package com.kgec.mca.rars;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kgec.mca.rars.helper.SQLiteHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mabbas007.tagsedittext.TagsEditText;

public class ChooseField extends Activity implements TagsEditText.TagsEditListener {

    private ArrayList<String> college,depart,categor;
    SearchableSpinner cidspinner,didspinner,categoryspinner;
    Button selectchoice;
    private ProgressDialog pDialog;
    String cname,dname,email,categoryname;
    private SQLiteHandler db;
    private TagsEditText field;
    private static final String TAG = "Interest";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_field);
        field = findViewById(R.id.interest);
        field.setTagsListener(this);
        field.setTagsWithSpacesEnabled(true);
        field.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.field)));
        field.setThreshold(1);

        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();


        email=user.get("email");

        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        email = bundle.getString("EMAIL");
        selectchoice = (Button)findViewById(R.id.submitchoice) ;

        college = new ArrayList<String>();
        college.add("Select College");
        college.add("Kalyani Government Engineering College");
        college.add("Heritage");
        college.add("BP Poddar");
        college.add("Techno India");
        depart = new ArrayList<String>();
        depart.add("Select Department");
        depart.add("Master of Computer Application");
        depart.add("Computer Science");
        depart.add("Civil");
        depart.add("Mechanical");
        depart.add("Information Technology");
        depart.add("Electrical");
        categor = new ArrayList<String>();
        categor.add("Select Subject");
        categor.add("Arts And Humanities");
        categor.add("Computer Science And Engineering");
        categor.add("Life Science And Biology");
        categor.add("Physics And Mathematics");
        //Initializing Spinner
        didspinner = (SearchableSpinner) findViewById(R.id.departmentspin);
        cidspinner = (SearchableSpinner) findViewById(R.id.collegespin);
        categoryspinner=findViewById(R.id.category);
       // iidspinner = (SearchableSpinner) findViewById(R.id.category);

       //1 iidspinner.setOnItemSelectedListener(new iidclass());
        cidspinner.setOnItemSelectedListener(new cidclass());
        didspinner.setOnItemSelectedListener(new didclass());
        categoryspinner.setOnItemSelectedListener(new categoryclass());
        pDialog=new ProgressDialog(this);
        pDialog.setCancelable(false);

        collegedata();

        departmentdata();

        categorydata();

        selectchoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate the fields and call sign method to implement the api

                choices();

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            field.dismissDropDown();
        }
        else {
            field.showDropDown();
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

    /*Bundle bundle = new Bundle();
//Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL", email);
//Add the bundle to the intent
                i.putExtras(bundle);
    startActivity(i);*/

    private void collegedata(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseField.this,
                android.R.layout.simple_spinner_dropdown_item, college) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cidspinner.setAdapter(adapter);
    }

    private void departmentdata(){
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(ChooseField.this,
                android.R.layout.simple_spinner_dropdown_item, depart) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        didspinner.setAdapter(adapter1);
    }


    private void categorydata(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseField.this,
                android.R.layout.simple_spinner_dropdown_item, categor) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryspinner.setAdapter(adapter);
    }

    class didclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            dname=parent.getSelectedItem().toString();
           // cid=getcust(position-1);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class cidclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            cname=parent.getSelectedItem().toString();
            //cid=getcust(position-1);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class categoryclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            categoryname=parent.getSelectedItem().toString();
            // cid=getcust(position-1);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



    private void choices(){
        String tag_string_req = "req_login";
        pDialog.setMessage("Adding Choices...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/register.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            public static final String TAG = "Response";

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                hideDialog();
                db.addUser(email,field.getText().toString().trim());
               // Toast.makeText(getApplicationContext(),
                       // "Choices added successfully", Toast.LENGTH_LONG).show();
                Intent i = new Intent(ChooseField.this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putString("EMAIL",email);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Error";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(),
                //       "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("fname", "");
                params.put("lname", "");
                params.put("password", "");
                //params.put("order_desc", username);
                params.put("email",email);
                params.put("college", cname);
                params.put("department",dname);
                params.put("interest", field.getText().toString());


                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
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

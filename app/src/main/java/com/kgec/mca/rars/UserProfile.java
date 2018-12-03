package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kgec.mca.rars.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    String user1;
    RecyclerView recyclerView;
    EditText editTextSearch;
    ArrayList<String> names;
    JSONArray data1,data2;
    ProgressDialog pDialog;
    CustomAdapter adapter;
    private TextView name;
    private TextView mail;
    private TextView department,pub,followers,coll,phn;
    SwipeRefreshLayout swipeLayout;
    ImageButton follow,reportuser,followin;
    SQLiteHandler db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle bundle=getIntent().getExtras();
        user1=bundle.getString("USER");
        //Toast.makeText(getApplicationContext(), user1, Toast.LENGTH_LONG).show();
        names = new ArrayList<>();
        name = (TextView)findViewById(R.id.user_profile_name);
        mail = (TextView)findViewById(R.id.email);
        pub = (TextView)findViewById(R.id.publication);
        followers = (TextView)findViewById(R.id.followers);
        department = (TextView)findViewById(R.id.department);
        swipeLayout = findViewById(R.id.swipe_container);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        followin=findViewById(R.id.following);
        follow=findViewById(R.id.follow);
        reportuser=findViewById(R.id.report);
        coll = (TextView)findViewById(R.id.college);
        phn = (TextView)findViewById(R.id.phone);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        getSupportActionBar().setTitle("User Profile");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //follow.setVisibility(View.INVISIBLE);
        followin.setVisibility(View.INVISIBLE);

        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();

        email = user.get("email");

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followin.setVisibility(View.VISIBLE);
                follow.setVisibility(View.INVISIBLE);
                fol();
            }
        });

        reportuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report(user1);
            }
        });

      swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
               // Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation
       swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

       followingfunc();
       profile(user1);

    }

    private void followingfunc() {
        // names.clear();
        String tag_string_req = "req_login";
        // pDialog.setMessage("lllll");

        // pDialog.setMessage("Searching Friends...");
        // showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/followers.php?email="+email;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            public static final String TAG = "Volly Message" ;

            @Override
            public void onResponse(String response) {
                //Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                try {

                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("respond");
                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String name33 = json.getString("following");

                            if (user1.equalsIgnoreCase(name33)) {
                                follow.setVisibility(View.INVISIBLE);
                                followin.setVisibility(View.VISIBLE);
                            }
                            else {
                                follow.setVisibility(View.VISIBLE);
                                followin.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),"Feedback form Submitted Successfully", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(getApplicationContext(), "Location Sent", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Error Message";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    void fol() {
        String tag_string_req = "req_login";

        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/follow.php";
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


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("following",user1 );

                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };
            // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void profile(final String email) {

        String tag_string_req = "req_login";


        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/profile.php?email="+email;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public static final String TAG = "Login";

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);

                //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();

                try {
                    JSONObject json = new JSONObject(response);

                    data2 = json.getJSONArray("name");
                    for (int i = 0; i < data2.length(); i++) {
                        try {
                            JSONObject jObj = data2.getJSONObject(i);
                            String name = jObj.getString("PdfName");
                            //id=json.getInt("id");
                            names.add(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),"Feedback form Submitted Successfully", Toast.LENGTH_LONG).show();

                    adapter = new CustomAdapter(names);

                    recyclerView.setAdapter(adapter);

                    data1 = json.getJSONArray("respond");
                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject jObj = data1.getJSONObject(i);
                            String fsname = jObj.getString("fname");
                            String ddept = jObj.getString("department");
                            String mmail = jObj.getString("email");
                            String publications = jObj.getString("publications");
                            String folow = jObj.getString("followers");
                            String phone_no = jObj.getString("phone_no");
                            String college = jObj.getString("college");
                            String lsname = jObj.getString("lname");
                            name.setText(fsname+" "+lsname);
                            followers.setText(folow);
                            department.setText(ddept);
                            coll.setText(college);
                            phn.setText(phone_no);
                            mail.setText(mmail);
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



    private void report(final String user1) {

        String tag_string_req = "req_login";


        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/report.php";
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
                params.put("email", email);
                params.put("reporting", user1);

                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    private String getuser(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data2.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getlong(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data2.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("long_desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getUrl(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data2.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("PdfURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private int getHidden(int position) {
        int id=0;
        try {
            //Getting object of given index
            JSONObject json = data2.getJSONObject(position);

            //Fetching name from that object
            id = json.getInt("is_hidden");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private String download ( int position){
        String id = " ";
        try {
            //Getting object of given index
            JSONObject json = data2.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("download");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<String> names;

        public CustomAdapter(ArrayList<String> names) {
            this.names = names;
        }

        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_layout1, parent, false);
            return new CustomAdapter.ViewHolder(v);
        }




        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder holder, final int position) {
            holder.textViewName.setText(names.get(position));
            holder.textuser.setText("Uploaded by: "+getuser(position));
            holder.textlong.setText(getlong(position));
            holder.noofviews.setText(download(position));
            if (getHidden(position) == 0) {
                holder.request.setVisibility(View.INVISIBLE);
                holder.viewpaper.setVisibility(View.VISIBLE);

                holder.viewpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        down(getUrl(position));

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(getUrl(position)));
                        startActivity(intent);
                    }
                });
            } else {
                holder.viewpaper.setVisibility(View.INVISIBLE);
                holder.request.setVisibility(View.VISIBLE);
                holder.request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        request(getuser(position), getUrl(position));
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName,textuser,textlong,noofviews;
            Button viewpaper,request;

            ViewHolder(View itemView) {
                super(itemView);

                textViewName = (TextView) itemView.findViewById(R.id.header_text);
                textuser = (TextView) itemView.findViewById(R.id.publisher_name);
                textlong = (TextView) itemView.findViewById(R.id.blog_content);
                viewpaper = itemView.findViewById(R.id.viewpaper);
                request = itemView.findViewById(R.id.requestpaper);
                noofviews = itemView.findViewById(R.id.comment_number);
            }
        }

        //This method will filter the list
        //here we are passing the filtered data
        //and assigning it to the list with notifydatasetchanged method
        public void filterList(ArrayList<String> filterdNames) {
            this.names = filterdNames;
            notifyDataSetChanged();
        }
    }

    public void down ( final String Url)
    {
        String tag_string_req = "req_login";

        final String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/watch.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            public static final String TAG = "Login";

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);

               // Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Hello";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("Pdf", Url);
                //params.put("following",user );

                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void request ( final String requesting, final String Url)
    {
        String tag_string_req = "req_login";

        final String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/request.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            public static final String TAG = "Login";

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);

                //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {

            public static final String TAG = "Hello";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("requesting", requesting);
                params.put("PdfURL", Url);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

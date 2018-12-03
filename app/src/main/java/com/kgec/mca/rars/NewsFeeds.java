package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsFeeds extends BaseActivity {
    private SQLiteHandler db;
    private SessionManager session;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    RecyclerView recyclerView;
    EditText editTextSearch;
    ArrayList<String> names;
    ProgressDialog pDialog;
    CustomAdapter adapter;
    JSONArray data1;
    JSONArray data2;
    String email,interest;
    SwipeRefreshLayout swipeLayout;

    public static final String TAG = "Volly Message" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_news_feeds, this);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        swipeLayout = findViewById(R.id.swipe_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News Feeds");
        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);

        names = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide virtual keyboard
                // hide virtual keyboard

                Intent i = new Intent(NewsFeeds.this, Search.class);
                startActivity(i);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();

        email = user.get("email");
        interest = user.get("interest");


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        String tag_string_req = "req_login";
        pDialog.setMessage("Loading your Newsfeed...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/newsfeed.php?interest=" + interest;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("respond");
                    data2 = jObj.getJSONArray("name");
                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String name = json.getString("PdfName");

                            //id=json.getInt("id");
                            names.add(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),"Feedback form Submitted Successfully", Toast.LENGTH_LONG).show();

                    adapter = new CustomAdapter(names);

                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }); /*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }*/


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here

                names.clear();
                String tag_string_req = "req_login";
                String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/newsfeed.php?interest=" + interest;
                StringRequest strReq = new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.i("tagconvertstr", "[" + response + "]");
                        Log.d(TAG, " Response: " + response);
                        hideDialog();

                        try {

                            JSONObject jObj = new JSONObject(response);

                            data1 = jObj.getJSONArray("respond");
                            data2 = jObj.getJSONArray("name");
                            for (int i = 0; i < data1.length(); i++) {
                                try {
                                    JSONObject json = data1.getJSONObject(i);
                                    String name = json.getString("PdfName");

                                    //id=json.getInt("id");
                                    names.add(name);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //Toast.makeText(getApplicationContext(),"Feedback form Submitted Successfully", Toast.LENGTH_LONG).show();

                            adapter = new CustomAdapter(names);

                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, " Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Wrong Credentials!", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }); /*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }*/


                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);






                //Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
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

    }


        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        /*editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<String> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (String s : names) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }*/

    private int getHidden(int position) {
        int id=0;
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getInt("is_hidden");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getuser(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private String getshort(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("short_desc");
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
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("long_desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getpdfname(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("PdfName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getname(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            for (int i = 0; i < data2.length(); i++) {
                try {

                    JSONObject json1 = data2.getJSONObject(i);
                    if (json.getString("email").equals(json1.getString("email"))) {

                        //Fetching name from that object
                        id = json1.getString("fname")+" "+ json1.getString("lname");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (JSONException e) {
                e.printStackTrace();
        }
                //Returning the name
                return id;
    }
        private String download ( int position){
            String id = " ";
            try {
                //Getting object of given index
                JSONObject json = data1.getJSONObject(position);

                //Fetching name from that object
                id = json.getString("download");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Returning the name
            return id;
        }

        private String getUrl ( int position){
            String id = " ";
            try {
                //Getting object of given index
                JSONObject json = data1.getJSONObject(position);

                //Fetching name from that object
                id = json.getString("PdfURL");
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
                holder.textuser.setText("Uploaded by:  "+getname(position));
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

                holder.textuser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(NewsFeeds.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        //Add your data from getFactualResults method to bundle
                        bundle.putString("USER", getuser(position));
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

                holder.textlong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(NewsFeeds.this, ReadMore.class);
                        Bundle bundle = new Bundle();
                        //Add your data from getFactualResults method to bundle
                        bundle.putString("USER", getuser(position));
                        bundle.putString("NAME", getname(position));
                        bundle.putString("PDFURL", getUrl(position));
                        bundle.putInt("ISHIDDEN", getHidden(position));
                        bundle.putString("DOWNLOAD", download(position));
                        bundle.putString("SHORT_DESC", getshort(position));
                        bundle.putString("LONG_DESC", getlong(position));
                        bundle.putString("PDFNAME", getpdfname(position));
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return names.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {

                TextView textViewName, textuser, textlong, noofviews;
                Button viewpaper, request;

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
                    params.put("requesting", requesting);
                    params.put("PdfURL", Url);
                    //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        private void showDialog () {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hideDialog () {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        @Override
        public void onBackPressed () {

            if (this.sDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.sDrawerLayout.closeDrawer(GravityCompat.START);
                new AlertDialog.Builder(this)
                        // .setIcon(R.drawable.dialog_warning)
                        .setTitle("Closing ResearchIN")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                this.sDrawerLayout.closeDrawer(GravityCompat.START);
                new AlertDialog.Builder(this)
                        //.setIcon(R.drawable.dialog_warning)
                        .setTitle("Closing ResearchIN")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }

    }
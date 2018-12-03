package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Abhro on 13-11-2018.
 */

public class Search extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText editTextSearch;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    ArrayList<String> names,names2;
    ProgressDialog pDialog;
    CustomAdapter adapter ;

    JSONArray data1,data2;
    SwipeRefreshLayout swipeLayout;

    public static final String TAG = "Volly Message" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        swipeLayout = findViewById(R.id.swipe_container);

        names = new ArrayList<>();
        names2 = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editTextSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(editTextSearch.getId(), InputMethodManager.SHOW_FORCED);




        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Progress dialog
                    names2.clear();
                    names.clear();
                    // hide virtual keyboard
                   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    String tag_string_req = "req_login";
                   // pDialog.setMessage("Searching...");
                    //showDialog();
                    String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/search.php?search=" + editTextSearch.getText();
                    StringRequest strReq = new StringRequest(Request.Method.GET,
                            url, new Response.Listener<String>() {


                        @Override
                        public void onResponse(String response) {
                            Log.i("tagconvertstr", "[" + response + "]");
                            Log.d(TAG, " Response: " + response);
                      //      hideDialog();


                            try {
                                JSONObject json = new JSONObject(response);

                                data2 = json.getJSONArray("name");
                                for (int i = 0; i < data2.length(); i++) {
                                    try {
                                        JSONObject jObj = data2.getJSONObject(i);
                                        String name = jObj.getString("PdfName");
                                        //id=json.getInt("id");
                                        names.add(name);
                                        adapter = new CustomAdapter(names);

                                        recyclerView.setAdapter(adapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //Toast.makeText(getApplicationContext(),"Feedback form Submitted Successfully", Toast.LENGTH_LONG).show();

                                data1 = json.getJSONArray("respond");
                                for (int i = 0; i < data1.length(); i++) {
                                    try {
                                        JSONObject jObj = data1.getJSONObject(i);
                                        String fsname = jObj.getString("fname");

                                        //String mmail = jObj.getString("email");
                                        names2.add(fsname);
                                        adapter = new CustomAdapter(names2);

                                        recyclerView.setAdapter(adapter);
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

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, " Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    "Wrong Credentials!", Toast.LENGTH_LONG).show();
                        //    hideDialog();
                        }
                    }); /*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }*/  if (names.size() > names2.size()) {


                    } else

                        {



                    }


                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                    return true;



                }
                return false;
            }
        });




        //Swipe Layout
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
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

    private String getUrl(int position) {
        String id=" ";
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
                    .inflate(R.layout.list_layout, parent, false);
            return new CustomAdapter.ViewHolder(v);
        }






        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder holder, final int position) {
            holder.textViewName.setText(names.get(position));
//            holder.viewpaper.setVisibility(View.INVISIBLE);
           // holder.textuser.setText(getuser(position));
            //holder.textlong.setText(getlong(position));


            holder.textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Search.this,UserProfile.class);
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("USER",getuser(position));
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

            TextView textViewName,textuser,textlong;
            Button viewpaper;

            ViewHolder(View itemView) {
                super(itemView);

                textViewName = (TextView) itemView.findViewById(R.id.textViewName);
                textuser = (TextView) itemView.findViewById(R.id.publisher_name);
                textlong = (TextView) itemView.findViewById(R.id.blog_content);
                viewpaper = itemView.findViewById(R.id.viewpaper);
            }
        }







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



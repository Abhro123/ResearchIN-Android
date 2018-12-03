package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.kgec.mca.rars.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhro on 25-11-2018.
 */

public class Requesting extends Fragment {


    private SQLiteHandler db;
    String email;
    RecyclerView recyclerView;
    Button find;
    ArrayList<String> names;
    ProgressDialog pDialog;
    CustomAdapter adapter;
    JSONArray data1,data2,data3;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    SwipeRefreshLayout swipeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.requesting, container, false);
        db = new SQLiteHandler(getActivity().getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        email=user.get("email");
        names = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestfunc();
        return rootView;

        // AllowRunTimePermission();



    }


    private void requestfunc() {
        // names.clear();
        String tag_string_req = "req_login";
        // pDialog.setMessage("lllll");

        // pDialog.setMessage("Searching Friends...");
        // showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/requested.php?email="+email;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            public static final String TAG = "Volly Message" ;

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                try {

                    JSONObject jObj = new JSONObject(response);
                    data2 = jObj.getJSONArray("name");
                    data3 = jObj.getJSONArray("PDF");
                    data1 = jObj.getJSONArray("respond");
                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String name = json.getString("requesting");

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
    private String getuser(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            for (int i = 0; i < data2.length(); i++) {
                try {

                    JSONObject json1 = data2.getJSONObject(i);
                    if (json.getString("requesting").equals(json1.getString("email"))) {

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

    private String getlname(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            for (int i = 0; i < data3.length(); i++) {
                try {

                    JSONObject json1 = data3.getJSONObject(i);
                    if (json.getString("PdfURL").equals(json1.getString("PdfURL"))) {

                        //Fetching name from that object
                        id = json1.getString("short_desc");
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
    private String getcol(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            for (int i = 0; i < data2.length(); i++) {
                try {

                    JSONObject json1 = data2.getJSONObject(i);
                    if (json.getString("requesting").equals(json1.getString("email"))) {

                        //Fetching name from that object
                        id = json1.getString("college");
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

    private String getdept(int position) {
        String id=" ";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);
            for (int i = 0; i < data2.length(); i++) {
                try {

                    JSONObject json1 = data2.getJSONObject(i);
                    if (json.getString("requesting").equals(json1.getString("email"))) {

                        //Fetching name from that object
                        id = json1.getString("department");
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

    private int getapprove ( int position){
        int id = 0;
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getInt("approve");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private int getdeclined ( int position){
        int id = 0;
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getInt("decline");
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
                    .inflate(R.layout.list_layout4, parent, false);
            return new CustomAdapter.ViewHolder(v);
        }




        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder holder, final int position) {
             holder.textViewName.setText( getlname(position));
            holder.textuser.setText(getuser(position));
            //holder.textuser.setTextSize(20);
            holder.textlong.setText("Faculty of "+getdept(position)+ " at " + getcol(position));

            if (getapprove(position)==1){
                holder.cancel.setVisibility(View.INVISIBLE);
                holder.download.setVisibility(View.VISIBLE);
                holder.pending.setVisibility(View.INVISIBLE);
            }
            else if (getdeclined(position)==1){
                holder.cancel.setVisibility(View.VISIBLE);
                holder.download.setVisibility(View.INVISIBLE);
                holder.pending.setVisibility(View.INVISIBLE);
            }
            else {
                holder.cancel.setVisibility(View.INVISIBLE);
                holder.download.setVisibility(View.INVISIBLE);
                holder.pending.setVisibility(View.VISIBLE);
            }

            //holder.viewpaper.setVisibility(View.INVISIBLE);

            holder.textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(getUrl(position)));
                    startActivity(intent);
                }
            });


            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(getUrl(position)));
                    startActivity(intent);
                }
            });



            holder.textuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(),UserProfile.class);
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("USER",names.get(position));
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
            Button cancel,pending,download;

            ViewHolder(View itemView) {
                super(itemView);

                textViewName = (TextView) itemView.findViewById(R.id.header_text);
                textuser = (TextView) itemView.findViewById(R.id.publisher_name);
                textlong = (TextView) itemView.findViewById(R.id.blog_content);
                cancel = itemView.findViewById(R.id.cancel);
                pending = itemView.findViewById(R.id.pending);
                download = itemView.findViewById(R.id.download);
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


}

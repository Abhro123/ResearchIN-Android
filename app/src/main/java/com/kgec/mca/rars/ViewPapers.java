package com.kgec.mca.rars;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ViewPapers extends AppCompatActivity {



    RecyclerView recyclerView;
    EditText editTextSearch;
    ArrayList<String> names;
    ProgressDialog pDialog;
    CustomAdapter adapter;
    JSONArray data1;
    public static int id;
    String email;
    private SQLiteHandler db;

    public static final String TAG = "Volly Message" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_papers);

        names = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("View Papers");
        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();
        // AllowRunTimePermission();

        email=user.get("email");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Papers...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/scholar_network/paperlist.php?email="+email;
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


        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        editTextSearch.addTextChangedListener(new TextWatcher() {
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

    public class CustomAdapter extends RecyclerView.Adapter<ViewPapers.CustomAdapter.ViewHolder> {

        private ArrayList<String> names;

        public CustomAdapter(ArrayList<String> names) {
            this.names = names;
        }

        @Override
        public ViewPapers.CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_layout, parent, false);
            return new ViewPapers.CustomAdapter.ViewHolder(v);
        }




        @Override
        public void onBindViewHolder(ViewPapers.CustomAdapter.ViewHolder holder, final int position) {
            holder.textViewName.setText(names.get(position));
            holder.textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(getUrl(position)));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName;


            ViewHolder(View itemView) {
                super(itemView);

                textViewName = (TextView) itemView.findViewById(R.id.textViewName);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
package Program.Tech;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private postadapter adapter;
    private List<postadapter.PostItem> itemList;
    private RequestQueue requestQueue;
    private Spinner filterSpinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedFilter = "All";
    private String username;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.refresh_all);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchItems();
            }
        });

        // Initialize Spinner
        filterSpinner = view.findViewById(R.id.order_by);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString();
                fetchItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.postQueueView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data list
        itemList = new ArrayList<>();
        adapter = new postadapter(itemList, getContext(), username); // Pass context and username here
        recyclerView.setAdapter(adapter);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(getContext());

        // Fetch JSON data
        fetchItems();

        return view;
    }

    private void fetchItems() {
        String url = "https://studev.groept.be/api/a23PT414/joinpost"; // Use your API URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        itemList.clear(); // Clear the list before adding new items
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String image = jsonObject.getString("image");
                                String account = jsonObject.getString("account");
                                String title = jsonObject.getString("introTitle");
                                String subtitle = jsonObject.getString("introText");
                                String postID = jsonObject.getString("postID");
                                String fundGoal = jsonObject.getString("fundGoal");
                                String voluntaryTitle = jsonObject.getString("voluntaryTitle");
                                String donationGoal = jsonObject.getString("donationGoal");

                                boolean addItem = false;

                                switch (selectedFilter) {
                                    case "All":
                                        addItem = true;
                                        break;
                                    case "Fund":
                                        addItem = fundGoal != null && !fundGoal.equals("null");
                                        break;
                                    case "Volunteer":
                                        addItem = voluntaryTitle != null && !voluntaryTitle.equals("null");
                                        break;
                                    case "Donation":
                                        addItem = donationGoal != null && !donationGoal.equals("null");
                                        break;
                                }

                                if (addItem) {
                                    postadapter.PostItem item = new postadapter.PostItem(image, account, title, subtitle, postID);
                                    itemList.add(item);
                                }
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter that data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}

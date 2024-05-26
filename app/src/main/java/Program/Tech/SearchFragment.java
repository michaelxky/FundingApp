package Program.Tech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private searchadapter adapter;
    private List<searchadapter.PostItem> itemList;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.refresh_all);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchItems(null); // Refresh without any query
            }
        });

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchItems(query); // Fetch items based on search query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optionally implement real-time search results as text changes
                return false;
            }
        });

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.postQueueView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false); // Important to allow scrolling in NestedScrollView

        // Initialize data list
        itemList = new ArrayList<>();
        adapter = new searchadapter(itemList, getContext());
        recyclerView.setAdapter(adapter);

        // Fetch initial data
        fetchItems(null);

        return view;
    }

    private void fetchItems(@Nullable String query) {
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

                                // Perform client-side filtering based on query
                                if (query == null || query.isEmpty() ||
                                        account.toLowerCase().contains(query.toLowerCase()) ||
                                        title.toLowerCase().contains(query.toLowerCase()) ||
                                        subtitle.toLowerCase().contains(query.toLowerCase())) {

                                    searchadapter.PostItem item = new searchadapter.PostItem(image, account, title, subtitle, postID);
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

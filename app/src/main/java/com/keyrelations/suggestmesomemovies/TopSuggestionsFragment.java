package com.keyrelations.suggestmesomemovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.AccessToken;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TopSuggestionsFragment extends Fragment {

    SwipeRefreshLayout mSwipeRefreshLayout;
    List<Movie> movie;
    FindMovieAdapter adapter;
    AdView adView;
    ListView lv;
    String url = "http://api.keyrelations.in/smsm/findmovie/" + AccessToken.getCurrentAccessToken().getToken() +"/99";
    RequestQueue queue;
    JsonArrayRequest jsArrRequest;
    TextView textMsg;
    ProgressBar spinner;
    RelativeLayout mlayout;

    public void refreshActivityData() {
        queue.add(jsArrRequest);
    }

    public void navigateToMovieInfoActivity(String movieId) {
        Intent intent = new Intent(getContext(), MovieInfoActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    public void addMovie(final String movieId){
        spinner.setVisibility(View.VISIBLE);
        //Log.d("DEBUGLOG", "Started to add movie");
        url = "http://api.keyrelations.in/smsm/addusermovie/" + AccessToken.getCurrentAccessToken().getToken() + "/" + movieId;
        //Log.d("DEBUGLOG", url);
        JsonArrayRequest jrArrRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("DEBUGLOG", "Response is valid");
                        ////Log.d("DEBUGLOG", "Response has " + String.valueOf(response.length()) + " records");
                        try {
                            ////Log.d("DEBUGLOG", response.getJSONObject(0).getString("message"));
                            ////Log.d("DEBUGLOG", movieId);

                            if (response.getJSONObject(0).getInt("code")==1) {
                                spinner.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Movie added to library", Toast.LENGTH_SHORT).show();
                                //Log.d("DEBUGLOG", "Movie added");
                            } else {
                                spinner.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Movie added in library", Toast.LENGTH_SHORT).show();
                                //Log.d("DEBUGLOG", "Movie added");
                            }
                        } catch (JSONException e) {

                            spinner.setVisibility(View.GONE);
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ////Log.d("ERROR", "ERROR");
                //textMsg.setText("Error! Please try again.");
                spinner.setVisibility(View.GONE);
                //Log.d("DEBUGLOG", "Error Response");
            }
        });
        jrArrRequest.setShouldCache(false);
        queue.add(jrArrRequest);
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_find_movie, container, false);





        textMsg = (TextView) rootView.findViewById(R.id.textViewMessage);

        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        //change the spinner color
        spinner.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshActivityData();
            }
        });

        movie = new ArrayList<>();
        adapter = new FindMovieAdapter(getContext(), R.layout.findmovie_list, movie,this);
        lv = (ListView) rootView.findViewById(R.id.listViewFindMovie);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Movie mov = (Movie) lv.getItemAtPosition(position);
                Movie mov = (Movie) lv.getAdapter().getItem(position);
                ////Log.d("CLICKED", String.valueOf(mov.getId()));
                navigateToMovieInfoActivity(String.valueOf(mov.getId()));
            }
        });

        // Instantiate an AdView view
        adView = new AdView(getContext(), "223264698023434_232733610409876", AdSize.BANNER_HEIGHT_50);

        adView.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad failed to load.
                // Add code to hide the ad's view

                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad was loaded
                // Add code to show the ad's view
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Use this function to detect when an ad was clicked.
            }

        });

        // Find the main layout of your activity
        mlayout = (RelativeLayout)rootView.findViewById(R.id.layoutFindMovie);

        // Add the ad view to your activity layout
        mlayout.addView(adView);

        adView.setId(R.id.reservedNamedIdAds);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //p.setMargins(0,0,0,adView.getHeight());

        adView.setLayoutParams(p);
        //ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)adView.getLayoutParams();
        //lpt.setMargins(lpt.leftMargin, lpt.topMargin, lpt.rightMargin, adView.getHeight() / 2);

        //AdSettings.addTestDevice("c69b4f0755b4deb0c0bb4061f950e40a");

        // Request to load an ad
        adView.loadAd();

        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        //define the json request
        jsArrRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("DEBUGLOG",String.valueOf(url));
                        //Log.d("DEBUGLOG","Response is valid");
                        //Log.d("DEBUGLOG", "Response has " + String.valueOf(response.length()) + " records");
                        if(response.length()==0){
                            movie.clear();
                            adapter.notifyDataSetChanged();
                            textMsg.setText(getResources().getString(R.string.no_data_found));
                        }
                        else{
                            movie.clear();
                            adapter.notifyDataSetChanged();
                            textMsg.setText("");
                            //setTitle(title + " (" + String.valueOf(response.length())+")");
                        }
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Movie mov = new Movie(response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("title"),response.getJSONObject(i).getString("release_year"),response.getJSONObject(i).getString("poster_path"),response.getJSONObject(i).getString("suggested_cnt"),response.getJSONObject(i).getString("imdb_rating"));
                                movie.add(mov);
                                if(i==response.length()-1){
                                    //lv.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    //Log.d("DEBUGLOG", "Data is now updated");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Log.d("DEBUGLOG", "JSON Exception occured");
                        }
                        spinner.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                //Log.d("DEBUGLOG", "API call failed. Unable to retrieve data");
                textMsg.setText(getResources().getString(R.string.server_error));
            }
        });

        jsArrRequest.setShouldCache(false);
        //Log.d("DEBUGLOG", "Request sent to server for movie query");
        spinner.setVisibility(View.VISIBLE);
        queue.add(jsArrRequest);

        return rootView;
    }


}

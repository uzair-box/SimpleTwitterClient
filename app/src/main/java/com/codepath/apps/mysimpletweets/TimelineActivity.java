package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;
    private ListView lvTweets;

    private User currentUser;

    private long lastMaxId = 0;

    private static final int COMPOSE_TWEET_INTENT_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);

        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populateTimeline();
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

        // Create the array list (data source)
        tweets = new ArrayList<>();

        // Construct adapter from data source
        aTweets = new TweetsArrayAdapter(this, tweets);

        // Connect adapter to listview
        lvTweets.setAdapter(aTweets);

        // Get the client
        client = TwitterApplication.getRestClient(); // Singleton client

        populateTimeline();

        // Get current user info
        loadCurrentUserInfo();
    }

    private void loadCurrentUserInfo() {
        client.getCurrentUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject userJson) {
                Log.d("DEBUG", userJson.toString());
                currentUser = User.fromJson(userJson);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void populateTimeline() {
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                aTweets.addAll(tweets);
                if (tweets.size() > 0) {
                    Tweet lastTweet = tweets.get(tweets.size() - 1);
                    lastMaxId = lastTweet.getUid();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, lastMaxId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCreateTweet) {
            Intent i = new Intent(this, ComposeTweetActivity.class);
            i.putExtra("currentUser", currentUser);
            startActivityForResult(i, COMPOSE_TWEET_INTENT_CODE);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET_INTENT_CODE) {
            if (resultCode == RESULT_OK) {
                tweets.clear();
                aTweets.notifyDataSetChanged();
                lastMaxId = 0;
                populateTimeline();
            }
        }
    }
}

package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class ComposeTweetActivity extends ActionBarActivity {

    private User currentUser;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        Intent i = getIntent();
        currentUser = (User) i.getSerializableExtra("currentUser");

        TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        ImageView ivUserProfilePic = (ImageView) findViewById(R.id.ivUserProfilePic);

        tvUserScreenName.setText("@" + currentUser.getScreenName());
        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivUserProfilePic);

        client = TwitterApplication.getRestClient();

        // Set up click handler for filtering
        Button btnTweet = (Button) findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etTweetBody = (EditText) findViewById(R.id.etTweetBody);
                String tweetString = etTweetBody.getText().toString();

                client.postTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(ComposeTweetActivity.this, "Tweet posted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(ComposeTweetActivity.this, "Failed to post tweet", Toast.LENGTH_SHORT).show();
                    }
                }, tweetString);

                Intent i = new Intent();
                setResult(RESULT_OK, i);

                finish();
            }
        });


        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_OK, i);

                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

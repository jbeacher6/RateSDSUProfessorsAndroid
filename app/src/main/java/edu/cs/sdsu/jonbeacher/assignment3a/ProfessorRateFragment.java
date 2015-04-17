package edu.cs.sdsu.jonbeacher.assignment3a;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ProfessorRateFragment extends Fragment implements View.OnClickListener {
    HttpClient httpclient;
    String urlProfessorRate = "http://bismarck.sdsu.edu/rateme/comment/";
    //http://bismarck.sdsu.edu/rateme/comment/n
    String currentUrlProfessorRate;
    String urlProfessorRateStars = "http://bismarck.sdsu.edu/rateme/rating/";
    //http://bismarck.sdsu.edu/rateme/rating/n/k, n=professorID k= #stars/5
    int numStars;
    String currentUrlProfessorRateStars;
    String currentUrlProfessorRateStarsWithStars;
    int professorID;
    String professorFullName;
    StringEntity stringEntityPostStringRating;
    Button postRatingButton;
    TextView ratingTextView;
    String ratingTextString;
    RatingBar postRatingsBar;
    HttpResponse responseBodyText;
    HttpResponse responseBodyStars;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        professorID = (int) getActivity().getIntent().getSerializableExtra("professorID");
        professorFullName = (String) getActivity().getIntent().getSerializableExtra("professorFullName");
        currentUrlProfessorRate = urlProfessorRate + professorID;
        currentUrlProfessorRateStars = urlProfessorRateStars + professorID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_professor, container, false);
        if (isOnline()) {
            showToast("Online");
        } else {
            showToast("Offline");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ratingTextView = (TextView) view.findViewById(R.id.professorratefragment_post_edit_text);
        postRatingsBar = (RatingBar) view.findViewById(R.id.professorrategragment_rate_stars_bar);
        postRatingButton = (Button) view.findViewById(R.id.professorratefragment_post_rating_button);
        postRatingButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.professorratefragment_post_rating_button:
                numStars = postRatingsBar.getNumStars();
                currentUrlProfessorRateStarsWithStars = currentUrlProfessorRateStars + "/" + numStars;
                ratingTextString = ratingTextView.getText().toString();
                new HttpClientTask().execute(currentUrlProfessorRate);
                break;
        }
    }

    public void onResume() {
        super.onResume();
        //new HttpClientTask().execute(urlProfessorList);
    }

    class HttpClientTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... urls) {
            String userAgent = null;
            httpclient = AndroidHttpClient.newInstance(userAgent);
            HttpPost postText = new HttpPost(currentUrlProfessorRate);
            HttpPost postStars = new HttpPost(currentUrlProfessorRateStarsWithStars);
            try {
                stringEntityPostStringRating = new StringEntity(ratingTextString, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                Log.i("e", e.toString());
                return 1;
            }
            postText.setHeader("Content-Type", "application/json;charset=UTF-8");
            postText.setEntity(stringEntityPostStringRating);
            try {
                responseBodyText = httpclient.execute(postText);
                responseBodyStars = httpclient.execute(postStars);
            } catch (Throwable t) {
                Log.i("t", t.toString());
            }
            httpclient.getConnectionManager().shutdown();
            return 0;
        }

        protected void onPostExecute(Integer postInteger) {
            if(postInteger == 0) {
                showToast("Rating posted!");
                httpclient.getConnectionManager().shutdown();
            }
            else if(postInteger == 1) {
                showToast("Rating not posted, error");
                httpclient.getConnectionManager().shutdown();
            }
            else {
                Log.i("wtf","error");
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        CharSequence charSequenceText = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, charSequenceText, duration);
        toast.show();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isOnline() && httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
    }
}
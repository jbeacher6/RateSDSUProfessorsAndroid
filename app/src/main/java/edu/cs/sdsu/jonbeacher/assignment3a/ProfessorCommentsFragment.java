package edu.cs.sdsu.jonbeacher.assignment3a;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfessorCommentsFragment extends ListFragment {
    String urlProfessorComments = "http://bismarck.sdsu.edu/rateme/comments/";
    int professorID;
    String professorFullName;
    String currentUrlProfessorComments;
    HttpClient httpclient;
    ListView mProfessorCommentsView;
    ArrayList<ProfessorComment> mProfessorComments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        professorID = (int) getActivity().getIntent().getSerializableExtra("professorID");
        professorFullName = (String) getActivity().getIntent().getSerializableExtra("professorFullName");
        currentUrlProfessorComments = urlProfessorComments + professorID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_rating_list, container, false);
        mProfessorCommentsView = (ListView) view.findViewById(android.R.id.list);
        if (isOnline()) {
            showToast("Online");
            new HttpClientTask().execute(currentUrlProfessorComments);
        } else {
            showToast("Offline");
        }
        return view;
    }

    public void onResume() {
        super.onResume();
    }

    void setupAdapter() {
        if (getActivity() == null || mProfessorCommentsView == null)
            return;
        if (mProfessorComments != null) {
            //Log.i("setupAdapter", mProfessorDetail.toString());
            mProfessorCommentsView.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, mProfessorComments));
        } else {
            mProfessorCommentsView.setAdapter(null);
        }
    }

    class HttpClientTask extends AsyncTask<String, Void, ArrayList<ProfessorComment>> {
        @Override
        protected ArrayList<ProfessorComment> doInBackground(String... urls) {
            ArrayList<ProfessorComment> professorComments = new ArrayList<>();
            HttpGet getMethod = new HttpGet(urls[0]);
            //Log.i("URL", urls[0].toString());
            String userAgent = null;
            httpclient= AndroidHttpClient.newInstance(userAgent);
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(getMethod, responseHandler);
                JSONArray jArray = new JSONArray(responseBody);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jArray.get(i);
                    String text = jsonObject.getString("text");
                    //Log.i("text", text.toString());
                    String date = jsonObject.getString("date");
                    //Log.i("date", date.toString());
                    professorComments.add(listDriver(text, date));
                }
                httpclient.getConnectionManager().shutdown();
                return professorComments;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(ArrayList<ProfessorComment> professorComment) {
            mProfessorComments = professorComment;
            //Log.i("COMMENTS:", mProfessorComments.toString());
            httpclient.getConnectionManager().shutdown();
            setupAdapter();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isOnline() && httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
    }

    void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        CharSequence charSequenceText = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, charSequenceText, duration);
        toast.show();
    }

    public ProfessorComment listDriver(String text, String date) {
        ProfessorComment professorComment = new ProfessorComment();
        professorComment.setText(text);
        professorComment.setDate(date);
        return professorComment;
    }
}

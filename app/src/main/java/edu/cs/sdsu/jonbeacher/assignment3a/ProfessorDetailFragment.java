package edu.cs.sdsu.jonbeacher.assignment3a;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import android.view.View;
import android.widget.Toast;

public class ProfessorDetailFragment extends ListFragment implements View.OnClickListener  {
    String urlProfessorDetail = "http://bismarck.sdsu.edu/rateme/instructor/";
    String urlProfessorComments = "http://bismarck.sdsu.edu/rateme/comments/";
    int professorID;
    String professorIDString;
    String currentUrlProfessorDetail;
    String professorFullName;
    HttpClient httpclient;
    ListView mProfessorDetailView;
    Button getRatingsButton;
    Button postRatingsButton;
    ArrayList<ProfessorDetail> mProfessorDetail = new ArrayList<>();
    DatabaseDriver dbDriver;
    SQLiteDatabase db;
    private static final String table_professor_detail = "TABLE_PROFESSOR_DETAIL";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        professorID = (int) getActivity().getIntent().getSerializableExtra("professorID");
        professorFullName = (String) getActivity().getIntent().getSerializableExtra("professorFullName");
        currentUrlProfessorDetail = urlProfessorDetail + professorID;
        dbDriver = (new DatabaseDriver(getActivity()));
        db = dbDriver.getWritableDatabase();
        professorIDString = Integer.toString(professorID);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRatingsButton = (Button) view.findViewById(R.id.get_ratings_button);
        getRatingsButton.setOnClickListener(this);
        postRatingsButton = (Button) view.findViewById(R.id.post_ratings_button);
        postRatingsButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_detail, container, false);
        mProfessorDetailView = (ListView) view.findViewById(android.R.id.list);
        if (isOnline()) {
            showToast("Online");
            new HttpClientTask().execute(currentUrlProfessorDetail);
        } else {
            showToast("Offline");
            if (tableExists(table_professor_detail)) {
                Log.i("tag","detail exists");
                Cursor c = db.query(table_professor_detail, new String[]{"professorID", "professorFirstName", "professorLastName", "professorOffice", "professorPhone", "professorEmail", "professorRating", "professorTotalRatings"}, null, null, null, null, null);
                    c.moveToFirst();
                    while (c.moveToNext()) {
                        mProfessorDetail.add(listDriver(String.valueOf(c.getInt(0)), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7)));
                    }
                    c.close();
                    setupAdapter();
                }
            }
        return view;
    }

    public void onResume() {
        super.onResume();
        if (isOnline()) {
            showToast("Online");
            new HttpClientTask().execute(currentUrlProfessorDetail);
        } else {
            showToast("Offline");
        }
    }

    void setupAdapter() {
        if (getActivity() == null || mProfessorDetailView == null)
            return;
        if (mProfessorDetail != null) {
            mProfessorDetailView.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, mProfessorDetail));
        } else {
            mProfessorDetailView.setAdapter(null);
        }
    }

    class HttpClientTask extends AsyncTask<String, Void, ArrayList<ProfessorDetail>> {
        @Override
        protected ArrayList<ProfessorDetail> doInBackground(String... urls) {
            ArrayList<ProfessorDetail> professorDetail = new ArrayList<>();
            String userAgent = null;
            httpclient = AndroidHttpClient.newInstance(userAgent);
            HttpGet getMethod = new HttpGet(urls[0]);
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(getMethod, responseHandler);
                JSONObject jsonObject = new JSONObject(responseBody);
                    String office = jsonObject.getString("office");
                    String phone = jsonObject.getString("phone");
                    String email = jsonObject.getString("email");
                    String rating = jsonObject.getString("rating");
                    JSONObject ratingJSONObject = new JSONObject(rating);
                    String averageRating = ratingJSONObject.getString("average");
                    String totalRatings = ratingJSONObject.getString("totalRatings");
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    professorDetail.add(listDriver(professorIDString, firstName, lastName, office, phone, email, averageRating, totalRatings));
                //SQLLite
                Cursor cursor = db.query(table_professor_detail, new String[] {"professorID", "professorFirstName", "professorLastName", "professorOffice", "professorPhone", "professorEmail", "professorRating", "professorTotalRatings"}, "professorID = "+professorIDString, null, null, null, null);
                if ( cursor.getCount() == 0 ) {
                    //Log.i("TAG", "To TABLE_PROFESSOR_LIST, inserting "+ professorIDString);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("professorID", professorID);
                    contentValues.put("professorFirstName", firstName);
                    contentValues.put("professorLastName", lastName);
                    contentValues.put("professorOffice", office);
                    contentValues.put("professorPhone", phone);
                    contentValues.put("professorEmail", email);
                    contentValues.put("professorRating", averageRating);
                    contentValues.put("professorTotalRatings", totalRatings);
                    db.beginTransaction();
                    try {
                        // Log.i("TAG", "database inserting");
                        db.insert(table_professor_detail, null, contentValues);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }
                cursor.close();
                httpclient.getConnectionManager().shutdown();
                return professorDetail;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<ProfessorDetail> professorDetail) {
            mProfessorDetail = professorDetail;
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

    public boolean tableExists(String tableName) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ tableName +"'", null);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public ProfessorDetail listDriver(String id, String firstName, String lastName, String office, String phone, String email, String averageRating, String totalRatings) {
        ProfessorDetail professorDetail = new ProfessorDetail();
        professorDetail.setOffice(office);
        professorDetail.setPhone(phone);
        professorDetail.setEmail(email);
        professorDetail.setAverageRating(averageRating);
        professorDetail.setTotalRatings(totalRatings);
        professorDetail.setFirstName(firstName);
        professorDetail.setLastName(lastName);
        professorDetail.setID(id);
        return professorDetail;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_ratings_button:
                onClickGetRatings(v);
                break;
            case R.id.post_ratings_button:
                onClickPostRatings(v);
                break;
        }
    }

    void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        CharSequence charSequenceText = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, charSequenceText, duration);
        toast.show();
    }

    public void onClickGetRatings (View view) {
        int indexProfessor = professorID;
        Intent i = new Intent(getActivity(), ProfessorCommentsActivity.class);
        i.putExtra("professorID", indexProfessor);
        i.putExtra("professorFullName", professorFullName);
        startActivity(i);
    }

    public void onClickPostRatings (View view) {
        int indexProfessor = professorID;
        Intent i = new Intent(getActivity(), ProfessorRateActivity.class);
        i.putExtra("professorID", indexProfessor);
        i.putExtra("professorFullName", professorFullName);
        startActivity(i);
    }
}

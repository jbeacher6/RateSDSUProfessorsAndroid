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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import android.view.View;
import android.widget.Toast;

public class ProfessorListFragment extends ListFragment {
    HttpClient httpclient;
    String urlProfessorList = "http://bismarck.sdsu.edu/rateme/list";
    ListView mProfessorListView;
    ArrayList<Professor> mProfessorList = new ArrayList<>();
    DatabaseDriver dbDriver;
    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        dbDriver = (new DatabaseDriver(getActivity()));
        db = dbDriver.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_list, container, false);
        mProfessorListView = (ListView) view.findViewById(android.R.id.list);
        if (isOnline()) {
            showToast("Online");
            new HttpClientTask().execute(urlProfessorList);
        } else {
            showToast("Not Online");
            if (tableExists("TABLE_PROFESSOR_LIST")) {
                Cursor c = db.query("TABLE_PROFESSOR_LIST", new String[]{"professorID", "professorFirstName", "professorLastName"}, null, null, null, null, null);
                c.moveToFirst();
                while (c.moveToNext()) {
                    mProfessorList.add(listDriver(String.valueOf(c.getInt(0)), c.getString(1), c.getString(2)));
                }
                c.close();
                setupAdapter();
            }
        }
        return view;
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

    public void onResume() {
        super.onResume();
        //new HttpClientTask().execute(urlProfessorList);
    }

    void setupAdapter() {
        if (getActivity() == null || mProfessorListView == null)
            return;
        if (mProfessorList != null) {
            //Log.i("TAG setupAdapter", mProfessorList.toString());
            mProfessorListView.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, mProfessorList));
        } else {
            mProfessorListView.setAdapter(null);
        }
    }

    void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        CharSequence charSequenceText = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, charSequenceText, duration);
        toast.show();
    }

    class HttpClientTask extends AsyncTask<String, Void, ArrayList<Professor>> {
        @Override
        protected ArrayList<Professor> doInBackground(String... urls) {
            ArrayList<Professor> professors = new ArrayList<>();
            String userAgent = null;
            httpclient= AndroidHttpClient.newInstance(userAgent);
            //String urlProfessorList = "http://bismarck.sdsu.edu/rateme/list";
            HttpGet getMethod = new HttpGet(urlProfessorList);
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(getMethod, responseHandler);
                JSONArray jArray = new JSONArray(responseBody);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jArray.get(i);
                    String id = jsonObject.getString("id");
                   // Log.i("id", id.toString());
                    String firstName = jsonObject.getString("firstName");
                    //Log.i("firstname", firstName.toString());
                    String lastName = jsonObject.getString("lastName");
                    //Log.i("lastname", lastName.toString());
                    professors.add(listDriver(id, firstName, lastName));

                   Cursor cursor = db.query("TABLE_PROFESSOR_LIST", new String[] {"professorID", "professorFirstName", "professorLastName"}, "professorID = "+id, null, null, null, null);
                    if ( cursor.getCount() == 0 ) {
                        //Log.i("TAG", "To TABLE_PROFESSOR_LIST, inserting "+ id);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("professorID", id);
                        contentValues.put("professorFirstName", firstName);
                        contentValues.put("professorLastName", lastName);
                        db.beginTransaction();
                        try {
                            // Log.i("TAG", "database inserting");
                            db.insert("TABLE_PROFESSOR_LIST", null, contentValues);
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                    }
                    cursor.close();
                }
                httpclient.getConnectionManager().shutdown();
                return professors;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Professor> professors) {
            mProfessorList = professors;
            //Log.i("LIST", mProfessorList.toString());
            httpclient.getConnectionManager().shutdown();
            setupAdapter();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int indexProfessor = position + 1;
        String professorFirstName = mProfessorList.get(position).getFirstName();
        String professorLastName = mProfessorList.get(position).getLastName();
        String professorFullName = professorFirstName + " " + professorLastName;
        Intent i = new Intent(getActivity(), ProfessorDetailActivity.class);
        i.putExtra("professorID", indexProfessor);
        i.putExtra("professorFullName", professorFullName);
        //Log.i("fullName: ", professorFullName);
        startActivity(i);
        //Log.i("index professor: ", Integer.toString(indexProfessor));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public Professor listDriver(String id, String firstName, String lastName) {
        Professor professor = new Professor();
        professor.setId(id);
        professor.setFirstName(firstName);
        professor.setLastName(lastName);
        return professor;
    }
}

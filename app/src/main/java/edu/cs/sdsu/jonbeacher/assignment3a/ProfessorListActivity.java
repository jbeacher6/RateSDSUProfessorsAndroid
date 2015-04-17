package edu.cs.sdsu.jonbeacher.assignment3a;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public class ProfessorListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new ProfessorListFragment();
    }
}

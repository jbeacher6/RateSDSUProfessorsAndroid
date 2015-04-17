package edu.cs.sdsu.jonbeacher.assignment3a;
import android.support.v4.app.Fragment;

public class ProfessorDetailActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new ProfessorDetailFragment();
    }
}

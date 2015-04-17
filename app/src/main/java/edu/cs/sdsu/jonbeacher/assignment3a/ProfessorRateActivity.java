package edu.cs.sdsu.jonbeacher.assignment3a;
import android.support.v4.app.Fragment;

public class ProfessorRateActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new ProfessorRateFragment();
    }
}

package cengallut.parrotius;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ParrotMain extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parrot_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_parrot_main, menu);
        return true;
    }
}

package net.cdmsoftware.mobilechef;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.cdmsoftware.mobilechef.sync.RecipeSyncUtilities;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipeSyncUtilities.scheduleSyncTask(this);
        //startService(new Intent(this, RecipeIntentService.class));
    }
}

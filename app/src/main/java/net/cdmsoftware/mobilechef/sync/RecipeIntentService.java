package net.cdmsoftware.mobilechef.sync;

import android.app.IntentService;
import android.content.Intent;

import net.cdmsoftware.mobilechef.Utilities;

public class RecipeIntentService extends IntentService {
    //SwipeRefreshLayout: parameters for broadcast receiver
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "net.cdmsoftware.mobilechef.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "net.cdmsoftware.mobilechef.intent.extra.REFRESHING";

    public RecipeIntentService() {
        super(RecipeIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!Utilities.isNetworkAvailable(this)) {
            sendBroadcast(
                    new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            return;
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        RecipeSyncTask.getRecipes(this);

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}

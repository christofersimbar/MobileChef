package net.cdmsoftware.mobilechef.sync;

import android.app.IntentService;
import android.content.Intent;

import net.cdmsoftware.mobilechef.Utilities;

import static android.provider.Telephony.BaseMmsColumns.RESPONSE_STATUS;

public class RecipeIntentService extends IntentService {
    //SwipeRefreshLayout: parameters for broadcast receiver
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "net.cdmsoftware.mobilechef.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "net.cdmsoftware.mobilechef.intent.extra.REFRESHING";
    public static final String EXTRA_RESPONSE_STATUS
            = "net.cdmsoftware.mobilechef.intent.extra.RESPONSE_STATUS";

    public RecipeIntentService() {
        super(RecipeIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int response = Utilities.ApiResponseStatus.NONE;

        if (!Utilities.isNetworkAvailable(this)) {
            response = Utilities.ApiResponseStatus.ERROR;
        } else {
            // set status to refreshing (tell UI to show loading indicator)
            sendBroadcast(
                    new Intent(BROADCAST_ACTION_STATE_CHANGE)
                            .putExtra(EXTRA_REFRESHING, true)
                            .putExtra(RESPONSE_STATUS, response));

            response = RecipeSyncTask.getRecipes(this);
        }

        // set status to refreshed (tell UI to stop showing loading indicator)
        // and upate response status
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE)
                        .putExtra(EXTRA_REFRESHING, false)
                        .putExtra(EXTRA_RESPONSE_STATUS, response));
    }
}

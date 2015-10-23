package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by DivyaM on 10/22/2015.
 */
public class TodayWidgetIntentService extends IntentService {

    private String[] fragmentdate = new String[1];

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_MATCHTIME = 2;

    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Date fragmentDate = new Date(System.currentTimeMillis()+(0*86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        fragmentdate[0] = mformat.format(fragmentDate);

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        // Get today's data from the ContentProvider
       Cursor data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
               null,
               null,
               fragmentdate,
               null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }
        // Extract the data from the Cursor
        String homeName = data.getString(COL_HOME);
        String awayName = data.getString(COL_AWAY);
        //String date = data.getString(COL_MATCHTIME);
        String score = Utilities.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS));
      //  int homeResourceId = Utilities.getTeamCrestByTeamName(
        //        data.getString(COL_HOME));
      //  int awayResourceId = Utilities.getTeamCrestByTeamName(
      //         data.getString(COL_AWAY));

        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_today_small;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
//            views.setImageViewResource(R.id.widget_home_icon, homeResourceId);
//            views.setImageViewResource(R.id.widget_away_icon, awayResourceId);
            // Content Descriptions for RemoteViews were only added in ICS MR1
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views, homeName, awayName);
//            }
            views.setTextViewText(R.id.widget_home_team, homeName);
            views.setTextViewText(R.id.widget_scores_text,  score);
            views.setTextViewText(R.id.widget_away_team, awayName);

            //views.setTextViewText(R.id.widget_date_text, date);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//    private void setRemoteContentDescription(RemoteViews views, String homeDescription, String awayDescription) {
//        views.setContentDescription(R.id.widget_home_icon, homeDescription);
//        views.setContentDescription(R.id.widget_away_icon, awayDescription);
//    }
}

//-------------------------------------------------------------------------------------------------------
// Copyright (C) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.txt file in the project root for full license information.
//-------------------------------------------------------------------------------------------------------
package run.ace;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.json.*;

public abstract class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    public static final String TOAST_ACTION = "run.ace.TOAST_ACTION";
    public static final String EXTRA_ITEM = "run.ace.EXTRA_ITEM";

	protected abstract int getLayoutResourceId(Context context);
	protected abstract int getViewResourceId(Context context);
	protected abstract int getItemResourceId(Context context);
	protected abstract int getItemTextResourceId(Context context);
	protected abstract int getItemImageResourceId(Context context);
	protected abstract int getItemLayoutResourceId(Context context);
	protected abstract int getItemBackgroundResourceId(Context context);
	protected abstract int getItemBackground1ResourceId(Context context);
	protected abstract int getItemBackground2ResourceId(Context context);
	protected abstract int getItemBackground3ResourceId(Context context);
	protected abstract int getItemBackground4ResourceId(Context context);


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        AppWidgetData.clear();
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        try {
            JSONObject actionData = new JSONObject();
            actionData.put("name", "");
            actionData.put("target", "Open app");
            actionData.put("state", 0);
    		AppWidgetData.add(actionData.toString(), context);
        } catch (JSONException e) {
            System.out.println(e);
        }
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
			try {
				// Start the main activity, passing along the index of the selection
				Intent onClickIntent = new Intent(context, Class.forName(context.getPackageName() + ".MainActivity"));
				onClickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	            int viewIndex = intent.getIntExtra("position", 0);
                String action = intent.getStringExtra("action");
                String cible = intent.getStringExtra("cible");
                int state = intent.getIntExtra("state", 0);
                int appWidgetId = intent.getIntExtra("appWidgetId", 0);
                int viewResourceId = intent.getIntExtra("viewResourceId", 0);

                AppWidgetData.changeState(viewIndex);

				onClickIntent.putExtra("widgetSelectionIndex", viewIndex);
                onClickIntent.putExtra("widgetSelectionAction", action);
				onClickIntent.putExtra("widgetSelectionCible", cible);
				onClickIntent.putExtra("widgetSelectionState", AppWidgetData.getState(viewIndex));




				context.startActivity(onClickIntent);
                
                mgr.notifyAppWidgetViewDataChanged(appWidgetId, viewResourceId);
			}
			catch (Exception ex)
			{
	            Toast.makeText(context, "Open the app first to load data", Toast.LENGTH_LONG).show();
			}
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
			AppWidgetData.addWidget(appWidgetIds[i], getViewResourceId(context));

			// Hook up with the service
            Intent intent = new Intent(context, AppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.putExtra("widgetItemId", getItemResourceId(context));
            intent.putExtra("widgetViewId", getViewResourceId(context));
            intent.putExtra("widgetItemTextId", getItemTextResourceId(context));
            intent.putExtra("widgetItemImageId", getItemImageResourceId(context));
            intent.putExtra("widgetItemLayoutId", getItemLayoutResourceId(context));
            intent.putExtra("widgetItemBackgroundId", getItemBackgroundResourceId(context));
            intent.putExtra("widgetItemBackground1Id", getItemBackground1ResourceId(context));
            intent.putExtra("widgetItemBackground2Id", getItemBackground2ResourceId(context));
            intent.putExtra("widgetItemBackground3Id", getItemBackground3ResourceId(context));
            intent.putExtra("widgetItemBackground4Id", getItemBackground4ResourceId(context));

			// Encode the extras into the data so it's not ignored when intents are compared
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), getLayoutResourceId(context));
            rv.setRemoteAdapter(appWidgetIds[i], getViewResourceId(context), intent);

			// The toast
            Intent toastIntent = new Intent(context, this.getClass());
            toastIntent.setAction(AppWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            toastIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			// The collection can set up a pending intent template, and then items set a fillInIntent
			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(getViewResourceId(context), toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

             //Toast.makeText(context, "SampleRemoteViewsFactory._appWidgetId", Toast.LENGTH_LONG).show();
              //Toast.makeText(context, SampleRemoteViewsFactory._itemLayoutResourceId, Toast.LENGTH_LONG).show();
            //appWidgetManager.notifyAppWidgetViewDataChanged(SampleRemoteViewsFactory._appWidgetId, SampleRemoteViewsFactory._itemLayoutResourceId);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

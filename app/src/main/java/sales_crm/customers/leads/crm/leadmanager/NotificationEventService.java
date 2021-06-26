package sales_crm.customers.leads.crm.leadmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Spinner;

import androidx.core.app.NotificationCompat;

import java.util.Random;


public class NotificationEventService extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "yttrtr";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        showNotification(context, "oooo", "gggggg", intent);

    }


    public void showNotification(Context context, String title, String body, Intent intent) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel: " + intent.getStringExtra("number")));
        PendingIntent pendingIntentCall = PendingIntent.getActivity(context, 0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
        whatsappIntent.setType("text/plain");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + intent.getStringExtra("number"));
        //whatsappIntent.putExtra(Intent.EXTRA_TEXT, template.getDescription());

        whatsappIntent.putExtra("jid", intent.getStringExtra("number") + "@s.whatsapp.net");
        whatsappIntent.setPackage("com.whatsapp");

        PendingIntent pendingIntentWhatsApp = PendingIntent.getActivity(context, 0, whatsappIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //whatsappIntent.setType("application/pdf");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = new Random().nextInt(9000) + 20;;
        String channelId = "lead_manager_meetings";
        String channelName = "Lead Manager";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(largeIcon)
                .setAutoCancel(false)
                .setContentTitle("Followup with " + intent.getStringExtra("name"))
                .setContentText(intent.getStringExtra("lfd"))
                .addAction(R.drawable.ic_phone_call,"Call", pendingIntentCall)
                .addAction(R.drawable.ic_whatsapp,"WhatsApp", pendingIntentWhatsApp);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, SplashActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                notificationId,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

}

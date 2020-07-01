package org.digitalcampus.oppia.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;

import com.google.common.util.concurrent.ListenableFuture;

import org.digitalcampus.mobile.learning.R;
import org.digitalcampus.oppia.activity.DownloadActivity;
import org.digitalcampus.oppia.activity.TagSelectActivity;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.database.DbHelper;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.utils.ui.OppiaNotificationUtils;

import java.util.List;

public class NoCourseWorker extends Worker {
    private static final String TAG =NoCourseWorker.class.getSimpleName() ;

    public NoCourseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    private void sendNoCourseNotification() {
        DbHelper db = DbHelper.getInstance(getApplicationContext());
        List<Course> courses = db.getAllCourses();
        if (courses.isEmpty()){
            Intent resultIntent = new Intent(getApplicationContext(), TagSelectActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = OppiaNotificationUtils.getBaseBuilder(getApplicationContext(), true);
            mBuilder
                    .setContentTitle(getString(R.string.notification_course_download_title))
                    .setContentText(getString(R.string.notification_course_download_text))
                    .setContentIntent(resultPendingIntent);
            int mId = 002;

            OppiaNotificationUtils.sendNotification(getApplicationContext(), mId, mBuilder.build());
        }

    }

    private String getString(int stringId) {
        return getApplicationContext().getString(stringId);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean isLoggedIn = SessionManager.isLoggedIn(getApplicationContext());
        if (isLoggedIn){
            sendNoCourseNotification();
            Log.d(TAG, "doWork: Work is done");
            return Result.success();
        } else {
            Log.i(TAG, "startWork: user not logged in. exiting TrakerWorker");
            return Result.failure();
        }

    }
}

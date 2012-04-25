package no.ntnu.osnap.tshirt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 25.04.12
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDataFetcher extends Service {
    
    Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchDataFromSocialService();
            }
        };
        timer = new Timer();
        timer.schedule(task, 4000, 4000);

    }

    private void fetchDataFromSocialService() {
    }
}

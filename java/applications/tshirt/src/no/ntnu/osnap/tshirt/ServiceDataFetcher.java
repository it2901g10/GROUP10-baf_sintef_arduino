package no.ntnu.osnap.tshirt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.SocialService;
import no.ntnu.osnap.social.listeners.ConnectionListener;
import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;
import no.ntnu.osnap.social.models.Person;
import no.ntnu.osnap.tshirt.helperClass.L;
import no.ntnu.osnap.tshirt.helperClass.Rule;
import no.ntnu.osnap.tshirt.helperClass.RuleArduinoTransfer;
import no.ntnu.osnap.tshirt.helperClass.TshirtSingleton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Background service that reads rules in Database and use them to get information from social service </br>
 * it then transmits data to arduino.
 */
public class ServiceDataFetcher extends Service {
    
    Timer timer;
    Prototype prototype;
    ArrayList<String> list;
    TshirtSingleton singleton;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        singleton = TshirtSingleton.getInstance(this);

        list = new ArrayList<String>();
        L.i("ServiceTshirtApp started");
        prototype = new Prototype(this, createConnectionListener());
        prototype.discoverServices();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                L.i("Timer initiates to fetch data from social service");
                iterateRulesAndCallSocialService();
            }
        };
        timer = new Timer();
//        timer.schedule(task, 1000, 10000);
        L.i("Timer to fetch data from social service is currently DISABLED");
    }


    private void iterateRulesAndCallSocialService() {

        Rule[] rules = singleton.database.getRules();

        //TODO RuleArduinoTransfer ruleArduinoTransfer = new RuleArduinoTransfer()
//        Rule[] rules = singleton.database.getRules();
//        RuleArduinoTransfer ruleArduinoTransfer = new RuleArduinoTransfer(prototype, this, socialServiceList.get(0),rules[0]);



    }

    private ConnectionListener createConnectionListener(){

        return new ConnectionListener() {
            @Override
            public void onConnected(String name) {
                L.i("TshirtService found service " + name);
                list.add(name);
            }
        };

    }
}

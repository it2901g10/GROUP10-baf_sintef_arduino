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
    TshirtSingleton singleton;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        singleton = TshirtSingleton.getInstance(this);

        prototype = new Prototype(this, "ServiceDataFetcher Connection");

        //Dummy listener
        prototype.discoverServices(null);
        L.i("ServiceTshirtApp started");
        singleton.connect();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(isClearToGo()){
                    L.i("Timer initiates to fetch data from social service");
                    iterateRulesAndCallSocialService();
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 1000, 10000);

    }

    private boolean isClearToGo() {

        if(singleton.serviceActivated == false){
            return false;
        }

        if(singleton.getServiceName() != null){
            return true;
        }
        
        L.i("No selected service to connect to");
        return false;
    }


    private void iterateRulesAndCallSocialService() {

        Rule[] rules = singleton.database.getRules();
        if(rules.length == 0){
            L.i("There are no rules in database to check");
            return;
        }
        for (int i = 0; i < rules.length; i++) {
            Rule rule = rules[i];
            activateRule(rule);
        }
    }

    private void activateRule(final Rule rule) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                (new RuleArduinoTransfer(prototype, ServiceDataFetcher.this, singleton.getServiceName(), rule)).start();
            }
        });
        t.start();
    }
}

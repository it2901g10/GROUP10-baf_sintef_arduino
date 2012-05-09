package no.ntnu.osnap.tshirt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.listeners.ConnectionListener;
import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;
import no.ntnu.osnap.social.models.Person;
import no.ntnu.osnap.tshirt.helperClass.Rule;

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
                fetchDataFromSocialService();
            }
        };
        timer = new Timer();
//        timer.schedule(task, 1000, 10000);
        L.i("Timer to fetch data from social service is currently DISABLED");
    }


    private void fetchDataFromSocialService() {
        L.i("Attempt to fetch data from social service");

//        Request request = Request.obtain(Request.RequestCode.MESSAGES);
//
//        if(list.size() > 0){
//            prototype.sendRequest(list.get(0), request, createResponseListener());
//        }


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

    private ResponseListener createResponseListener(){
        return new ResponseListener() {
            @Override
            public void onComplete(Response response) {
                L.i("Got Response " + response.getStatus().name());
                L.i("Got Response " + response.getNetwork());
                if(response.getModel() instanceof Person){
                    L.i("Got Response " + ((Person) response.getModel()).getName());
                }
                if(response.getModel() instanceof Message){
                    L.i("Got Response " + ((Message) response.getModel()).getText());

                    Rule[] rules = singleton.database.getRules();
                    L.i("Number of rules is " + rules.length);
                    for (int i = 0; i < rules.length; i++) {
                        if(rules[i].isRuleSatisfied(response.getModel())){
                            L.i("Rule is satisfied");
                            sendToArduino(rules[i], response.getModel());
                        }
                        else{
                            L.i("Rule is not satisfied");
                        }
                    }
                }
            }
        };
    }

    private void sendToArduino(Rule rule, Model model) {
        if(rule.getOutputFilter().equals("Message")){
            String text = ((Message)model).getText();
            if(rule.getOutputDevice().equals("LED")){ singleton.sendToLEDArduino(text);}
            if(rule.getOutputDevice().equals("LCD Display")){ singleton.sendToLCDDiplayArduino(text); }
            if (rule.getOutputDevice().equals("Vibrator")){ singleton.sendToLCDDiplayArduino(text); }
            if(rule.getOutputDevice().equals("Speaker")){ singleton.sendToLCDDiplayArduino(text); }
        }
        if(rule.getOutputFilter().equals("Sender")){
            String text = ((Message)model).getSenderAsPerson().getName();
            if(rule.getOutputDevice().equals("LED")){ singleton.sendToLEDArduino(text);}
            if(rule.getOutputDevice().equals("LCD Display")){ singleton.sendToLCDDiplayArduino(text); }
            if (rule.getOutputDevice().equals("Vibrator")){ singleton.sendToLCDDiplayArduino(text); }
            if(rule.getOutputDevice().equals("Speaker")){ singleton.sendToLCDDiplayArduino(text); }

        }
    }


}

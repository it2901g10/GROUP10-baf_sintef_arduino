package no.ntnu.osnap.tshirt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 24.04.12
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */


public class SocialFinderHandler extends Handler {
    public static final int SERVICE_DISCOVERED = 0;

    Messenger messenger;
    ArrayList<String> list;
    TextView view;
    Activity activity;

    public SocialFinderHandler(Activity activity, TextView view) {
        this.activity = activity;
        this.view = view;
        list = new ArrayList<String>();
        messenger = new Messenger(this);

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        L.i("Received Message in SocialHandler");
        
        switch(msg.what){
            case SERVICE_DISCOVERED:
                if(msg.replyTo != null){
                    String serviceName = ((Bundle) msg.obj).getString("name");
                    L.i("Found service " + serviceName);
                    
                    list.add(serviceName);
                    updateView();
                }

                break;
        }
    }

    private void updateView() {
        
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size() == 0){
                    view.setText("No service found");
                    return;
                }

                String services = "";
                for (int i = 0; i < list.size(); i++) {
                    services += list.get(i) + ((i < list.size()-1)?"\n":"");
                    
                }
                view.setText(services);
            }
        });
    }

    public void searchSocialServices() {
        list.clear();
        updateView();
        Intent i = new Intent("android.intent.action.SOCIAL");
        i.putExtra("replyTo", messenger);
        L.i("Sending broadcast to search for Social Service");
        activity.sendBroadcast(i);
    }
}

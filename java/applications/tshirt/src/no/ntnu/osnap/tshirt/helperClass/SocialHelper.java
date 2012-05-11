package no.ntnu.osnap.tshirt.helperClass;

import android.content.Context;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;
import no.ntnu.osnap.social.models.Person;
import no.ntnu.osnap.tshirt.R;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 11.05.12
 * Time: 02:59
 * To change this template use File | Settings | File Templates.
 */
public class SocialHelper {
    
    private Prototype prototype;
    private Context context;
    private String serviceName;

    public SocialHelper(Prototype prototype, Context context, String serviceName) {
        this.prototype = prototype;
        this.context = context;
        this.serviceName = serviceName;
    }

    public String getStringFromFilter(String filter){
        
        String[] segments = filter.split(":");
        recursiveFiltering(segments,null);
        return "NOT_IMPLEMENTED";
    }

    private void recursiveFiltering(String[] list, Model model){

        String filterPart = list[0];
        
        final String[] decrList = new String[list.length - 1];
        for (int i = 0; i < decrList.length; i++) {
            decrList[i] = list[i+1];
        }

        if(filterPart.equals(context.getString(R.string.getLatestPost))){
            getLatestMessageFromSocialService(decrList);
        }
        else if(filterPart.equals(context.getString(R.string.getMessage))){
            getTextInMessage(model);
        }        
        else if(filterPart.equals(context.getString(R.string.getSender))){
            getMessageSender(model);
        }


    }

    private void getMessageSender(Model model) {
    }

    private void getTextInMessage(Model model) {
        if(model instanceof Message){
            Message message = (Message)model;
            L.i("Message in text is " + message.getText());

        }
        else {
            L.e("ERR, model was not instance of Message");
        }
    }

    private void getLatestMessageFromSocialService(final String[] decrList) {
        Request request = new Request(Request.RequestCode.MESSAGES);
        prototype.sendRequest(serviceName,request, new ResponseListener() {
            @Override
            public void onComplete(Response response) {
                L.i("!!!!!Response from social service was " + response.getStatus().name() + " and model is " + response.getModel());
                if (response.getStatus() == Response.Status.COMPLETED) {
                    if(decrList.length == 0){
                        L.e("ERROR, FILTER IS NOT COMPLETE");
                    }
                    else{
                        recursiveFiltering(decrList, response.getModel());
                    }
                }
                else{
                    L.e("####Response from social service was not COMPLETED but " + response.getStatus().name());
                }

            }
        });
    }

//    /** Turns the async call to social service into a sync one */
//    
//    public Person getSelf(final String serviceName){
//        L.i("Trying to get Self from " + serviceName);
//        final AtomicReference<Model> notifier = new AtomicReference<Model>();
//
//        final Request request = new Request(Request.RequestCode.SELF);
//        Thread t = new Thread( new Runnable() {
//            @Override
//            public void run() {
//
//                prototype.sendRequest(serviceName, request, new ResponseListener() {
//                    @Override
//                    public void onComplete(Response response) {
//                        L.i("!!!!!Response from social service was " + response.getStatus().name() + " and model is " + response.getModel());
//
//                        if (response.getStatus() == Response.Status.COMPLETED) {
//                            notifier.set(response.getModel());
//                        }
//                        else{
//                            L.i("####Response from social service was not COMPLETED but " + response.getStatus().name());
//                        }
//
//                    }
//                });
//
//            }
//        });
//
//        t.start();
//
//        try {
//            Thread.sleep(1000);
//            Thread.sleep(1000);
//            Thread.sleep(1000);
//            Thread.sleep(1000);
//            Thread.sleep(1000);
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        prototype.
//        Model result = notifier.get();
//        if(result == null){
//            L.i("####Problem getting self from " + serviceName);
//        }
//        L.i("####Got " + result);
//        return (Person)result;
//    }
    
    
//    public Model getLoggedInUser(String serviceName){
//
//        final BlockAsync latch = new BlockAsync();
//        Request request = new Request(Request.RequestCode.SELF);
//        prototype.sendRequest(serviceName, request, new ResponseListener() {
//            @Override
//            public void onComplete(Response response) {
//                L.i("!!!!!Response from social service was " + response.getStatus().name() + " and model is " + response.getModel());
//                if (response.getStatus() == Response.Status.COMPLETED) {
//                    latch.onModelResult(response.getModel());
//                }
//                else{
//                    L.i("Response from social service was not COMPLETED but " + response.getStatus().name());
//                }
//            }
//        });
//
//        L.i("######Waiting for latch to finish");
//        Model result = null;
//        try {
//            result =  latch.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (ExecutionException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        L.i("####GOT result " + result );
//
//        return result;
//
//    }
    
}

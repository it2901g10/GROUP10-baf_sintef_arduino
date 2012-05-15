package no.ntnu.osnap.tshirt.helperClass;

import android.content.Context;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;
import no.ntnu.osnap.social.models.Notification;
import no.ntnu.osnap.social.models.Person;
import no.ntnu.osnap.tshirt.R;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/** 
 * Transmits Data to arduino if Rule is satisfied.
 * 
 * */

//This class makes use of recursion
 public class RuleArduinoTransfer {
    
    private Prototype prototype;
    private TshirtSingleton singleton;
    private Context context;
    private String serviceName;
    private String resultToOutput;
    private Rule rule;
    private LinkedList<Filter> linkedList = new LinkedList<Filter>();

    public RuleArduinoTransfer(Prototype prototype, Context context, String serviceName, Rule rule) {
        this.prototype = prototype;
        this.context = context;
        this.serviceName = serviceName;
        this.rule = rule;
        singleton = TshirtSingleton.getInstance(context);
        

    }

    /** Begins to check the filters, before grabbing the output text and finally push it to the arduino */
    public void start(){
        isFiltersSatisfied();
    }

    /** Grab all filters and see if they are satisfied by information from serviceName */
    private void isFiltersSatisfied() {
        Filter[] filters = rule.getFilters();
        for (int i = 0; i < filters.length; i++) {
            linkedList.add(filters[i]);
        }
        recursiveFiltering(linkedList.peek().filter.split(":"),null);
    }

    /** Goes over filer and calls social service at each step. */
    private void recursiveFiltering(String[] list, Model model){

        if(list.length == 0){
            L.e("Err, end of filter");
            return;
        }

        String filterPart = list[0];

        final String[] decrList = new String[list.length - 1];
        for (int i = 0; i < decrList.length; i++) {
            decrList[i] = list[i+1];
        }

        if(filterPart.equals(context.getString(R.string.getLatestMessage))){
            getLatestMessageFromSocialService(decrList);
        }
        else if(filterPart.equals(context.getString(R.string.getSender))){
            getMessageSender(model, decrList);
        }
        else if(filterPart.equals(context.getString(R.string.getLoggedInUser))){
            getLoggedInUser(decrList);
        }
        else if(filterPart.equals(context.getString(R.string.getLatestNotification))){
            getLatestNotificationFromSocialService(decrList);
        }
        else if(filterPart.equals(context.getString(R.string.getMessage))){
            getMessageText(model);
        }
        else if(filterPart.equals(context.getString(R.string.getName))){
            getPersonName(model);
        }
        else if(filterPart.equals(context.getString(R.string.getLink))){
            getPersonName(model);
        }
        else{
            L.e("ERR: Unknown filter " + filterPart);
        }
    }

    private void getLatestNotificationFromSocialService(String[] decrList) {
        Request request = new Request(Request.RequestCode.NOTIFICATIONS);
        prototype.sendRequest(serviceName,request,getNewResponseListener(decrList));
    }

    private void getLoggedInUser(String[] decrList) {
        Request request = new Request(Request.RequestCode.SELF);
        prototype.sendRequest(serviceName,request,getNewResponseListener(decrList));
    }

    /** Get the latest message in stream for user logged in given serviceName */
    private void getLatestMessageFromSocialService(String[] decrList) {
        Request request = new Request(Request.RequestCode.MESSAGES);
        prototype.sendRequest(serviceName,request,getNewResponseListener(decrList));
    }

    /** Get Sender of message as Person  */
    private void getMessageSender(Model model, String[] decrList) {
        if(model instanceof Message){
            Request request = new Request(Request.RequestCode.PERSON_DATA, ((Message)model).getSenderAsPerson());
            prototype.sendRequest(serviceName,request,getNewResponseListener(decrList));
        }
        else if(model instanceof Notification){
            Request request = new Request(Request.RequestCode.PERSON_DATA, ((Notification)model).getSenderAsPerson());
            prototype.sendRequest(serviceName,request,getNewResponseListener(decrList));
        } else {L.e("Err, model was not instance of Message or Notification but " + model.getClass()); }
    }

    /** Get name of person in model */
    private void getPersonName(Model model) {
        if(model instanceof Person){
            Person person = (Person)model;
            checkFilterInitNext(person.getName());
        }else {L.e("Err, model was not instance of Person but " + model.getClass()); }
    }

    /** Get text in message */
    private void getMessageText(Model model) {
        if(model instanceof Message){
            Message message = (Message)model;
            checkFilterInitNext(message.getText());
        }
        else if(model instanceof Notification){
            Notification notification = (Notification)model;
            checkFilterInitNext(notification.getMessage());
        }
        else {L.e("Err, model was not instance of Message or Notification but " + model.getClass()); }
    }

    private void getLink(Model model) {
        if(model instanceof Notification){
            Notification notification = (Notification)model;
            checkFilterInitNext(notification.getLink().toString());
        }
        else {L.e("Err, model was not instance of Message or Notification but " + model.getClass()); }

    }


    /** Compare filters to results from social service,
     * if all are satisfied, get */
    private void checkFilterInitNext(String result){

        //If there is no longer
        if(linkedList.size() == 0){
            resultToOutput = result;
            L.i("Rule Passed: got output " + resultToOutput + " and sent to " + rule.getOutputDevice());
            if(singleton.isConnected()){
                singleton.sendToArduino(resultToOutput, rule.getOutputDevice());
            }
            else{
                L.i("App is not connected to arduino");
            }
            return;

        }

        Filter f = linkedList.poll();
        if(!f.isFilterValid(result)){
            L.i("Rule " + rule.getName() + " was not satisfied with filter " + result + " " + f.getOperator() +  " " + f);
            return;
        }

        if(linkedList.size() == 0){
            //Get rule filteroutput
            recursiveFiltering(rule.getOutputFilter().split(":"),null);

        }
        else{
            recursiveFiltering(linkedList.peek().filter.split(":"),null);
        }
    }

    private ResponseListener getNewResponseListener(final String[] decrList){
        return new ResponseListener() {
            @Override
            public void onComplete(Response response) {
                if (response.getStatus() == Response.Status.COMPLETED) {
                    if(decrList.length == 0){
                        L.e("ERROR, FILTER IS NOT COMPLETE");
                        return;
                    }

                    L.i(response.toString());
                    if(response.getModel() != null){
                        recursiveFiltering(decrList, response.getModel());
                    }
                    else{
                        L.e("Model received is empty");
                    }
                }
                else{
                    L.e("Response from social service was not COMPLETED but " + response.getStatus().name());
                }
            }
        };
    }
}

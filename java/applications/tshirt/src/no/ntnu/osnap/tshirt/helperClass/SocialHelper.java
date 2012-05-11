package no.ntnu.osnap.tshirt.helperClass;

import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.models.Model;
import no.ntnu.osnap.social.models.Person;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 11.05.12
 * Time: 02:59
 * To change this template use File | Settings | File Templates.
 */
public class SocialHelper {
    
    private Prototype prototype;

    public SocialHelper(Prototype prototype) {
        this.prototype = prototype;
    }

    /** Turns the async call to social service into a sync one */
    
    public Person getSelf(String serviceName){
        final Person[] result = {null};
        Request request = new Request(Request.RequestCode.SELF);
        prototype.sendRequest(serviceName, request, new ResponseListener() {
            @Override
            public void onComplete(Response response) {
                if (response.getStatus() == Response.Status.COMPLETED) {
                    Model m = response.getModel();
                    if(m instanceof Person){
                        result[0] = (Person)m;
                    }
                    else{
                        L.i("Returned model was not Person");
                    }
                    notify();
                }

            }
        });
        try {
            wait(1000);
        } catch (InterruptedException e) {

        }
        if(result[0] == null){
            L.i("Timeout getting self from " + serviceName);
        }
        return result[0];
    }
    
}

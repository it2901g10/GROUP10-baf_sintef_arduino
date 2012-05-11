package no.ntnu.osnap.tshirt.filterMode;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.osnap.tshirt.helperClass.L;

/**
 * helper class to swap between Filtermodes (Person/Message/Group etc)
 */
public class ChangeMode {

    /** Current filter filter while going from activity to activity to build filter**/
    final public static String CURRENT_FILTER = "CURRENT_FILTER";
    /** Final filter for **/
    final public static String FINAL_FILTER = "FINAL_FILTER";
    /** If we don't want any comparison at end, used when we want field to send to arduino output */
    final public static String NO_COMPARE= "FINAL_FILTER";

    static void changeActivityToMessage(Activity parent, String currentFilter){
        Intent i = new Intent(parent, FilterMessage.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        i.putExtra(NO_COMPARE, parent.getIntent().getBooleanExtra(ChangeMode.NO_COMPARE, false));
        parent.startActivityForResult(i, 0);
    }
    static void changeActivityToPerson(Activity parent, String currentFilter){
        Intent i = new Intent(parent, FilterPerson.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        i.putExtra(NO_COMPARE, parent.getIntent().getBooleanExtra(ChangeMode.NO_COMPARE, false));
        parent.startActivityForResult(i, 0);
    }
    static void changeActivityToCompareResult(Activity parent, String currentFilter){
        if(parent.getIntent().getBooleanExtra(ChangeMode.NO_COMPARE, false)){
            //If we dont want to compare filter to a string we return here
            Intent i = new Intent();
            i.putExtra(ChangeMode.FINAL_FILTER, currentFilter);
            parent.setResult(parent.RESULT_OK, i);
            L.i("Return with filter " + currentFilter);
            parent.finish();
            return;
        }
        Intent i = new Intent(parent, FilterCompare.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        parent.startActivityForResult(i, 0);
    }
}

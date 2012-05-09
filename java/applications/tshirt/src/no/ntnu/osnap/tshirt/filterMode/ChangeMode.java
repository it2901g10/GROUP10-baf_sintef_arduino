package no.ntnu.osnap.tshirt.filterMode;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.osnap.tshirt.ActivityRulesEdit;
import no.ntnu.osnap.tshirt.L;

/**
 * helper class to swap between Filtermodes (Person/Message/Group etc)
 */
public class ChangeMode {
    final public static String CURRENT_FILTER = "CURRENT_FILTER";
    final public static String FINAL_FILTER = "FINAL_FILTER";


    final public static int COMPARE_RESULT = 0;
    public static void changeActivityToMessage(Activity parent, String currentFilter){
        Intent i = new Intent(parent, FilterMessage.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        parent.startActivityForResult(i,COMPARE_RESULT);
    }
    public static void changeActivityToPerson(Activity parent, String currentFilter){
        Intent i = new Intent(parent, FilterPerson.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        parent.startActivityForResult(i,COMPARE_RESULT);
    }
    public static void changeActivityToCompareResult(Activity parent, String currentFilter){
        Intent i = new Intent(parent, FilterCompare.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        parent.startActivityForResult(i,COMPARE_RESULT);
    }
}

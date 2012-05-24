/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    /** If we don't want any comparison at end, used when we want field to sent to arduino output */
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
    public static void changeActivityToNotification(FilterStart parent, String currentFilter) {
        Intent i = new Intent(parent, FilterNotification.class);
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
            L.d("Return with filter " + currentFilter);
            parent.finish();
            return;
        }
        Intent i = new Intent(parent, FilterCompare.class);
        i.putExtra(CURRENT_FILTER,currentFilter);
        parent.startActivityForResult(i, 0);
    }
}

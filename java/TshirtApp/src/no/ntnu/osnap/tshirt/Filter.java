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
package no.ntnu.osnap.tshirt;

import android.os.Parcel;
import android.os.Parcelable;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;

public class Filter implements Parcelable {
    String filter, compare, operator;

    Filter(String filter, String compare, String operator) {
        this.filter = filter;
        this.compare = compare;
        this.operator = operator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{filter, compare, operator});

    }

    public Filter(Parcel in){
        String[] array = new String[3];

        in.readStringArray(array);

        filter = array[0];
        compare = array[1];
        operator = array[2];
    }
    
    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    @Override
    public String toString() {
        return filter + " " + operator + " " + compare;
    }

    public boolean isFilterValid(Model model) {
        
        if(model instanceof Message) {
            Message message = (Message) model;
            if(filter.equals("Message")){
                return(checkOperator(message.getText()));
            }
            if(filter.equals("Sender")){
                return(checkOperator(message.getSenderAsPerson().getName()));
            }
        }
        return false;
    }
    
    private boolean checkOperator(String string){
        

        if(operator.equals("==")){
            return string.equals(compare);
        }
        if(operator.equals("!=")){
            return !string.equals(compare);
        }

        return false;
    }

}
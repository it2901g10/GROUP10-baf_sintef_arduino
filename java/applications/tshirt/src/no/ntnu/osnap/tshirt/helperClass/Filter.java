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
package no.ntnu.osnap.tshirt.helperClass;

import android.os.Parcel;
import android.os.Parcelable;
import no.ntnu.osnap.social.models.Message;
import no.ntnu.osnap.social.models.Model;

/**
 * A filter contains information on what to request from a social service and what to compare the results to </br>
 * Example, the filter "getLatestMessage:getSender:getName:=:David" checks if the creator of the latest post is called David
 *
 */

public class Filter implements Parcelable {
    public String filter;
    String[] segments;

    public Filter(String filter) {
        this.filter = filter;
        segments = filter.split(":");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{filter});

    }

    public Filter(Parcel in){
        String[] array = new String[1];

        in.readStringArray(array);

        filter = array[0];
        segments = filter.split(":");
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
        return filter;
    }

    public boolean isFilterValid(String string) {
        
        String operator = segments[segments.length-2];
        String compare = segments[segments.length-1];
        if(operator.equals("!")){
            return !compare.equals(string);
            
        }
        else if(operator.equals("=")){
            return compare.equals(string);
        }

        else if(operator.equals("contains")){
            return string.contains(compare);
        }
        else {
            L.e("Err, invalid operator " + operator);
        }
        
        return false;
    }

    public String getOperator() {
        if(segments.length > 2){
            return segments[segments.length - 2];
        }
        L.e("Tried to get operator from filter that is too short");
        return "ERROR NO FILTER";
        
        
    }
}
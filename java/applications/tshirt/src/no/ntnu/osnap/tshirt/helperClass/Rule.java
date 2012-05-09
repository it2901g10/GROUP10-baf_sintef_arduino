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
import no.ntnu.osnap.tshirt.L;


/***
 * A rule includes the rulename, the outputfilter to send, the name of the component to receive, </br>
 * and an array of filters that needs to be passed to send data to component.
 */
public class Rule implements Parcelable{

    public String name;
    private String outputFilter;
    private String outputDevice;
    private int id;
    private Filter[] filters;

    public Rule(String name, String outputFilter, String outputDevice, Filter[] filters, int id) {
        this.name = name;
        this.outputFilter = outputFilter;
        this.outputDevice = outputDevice;
        this.id = id;
        this.filters = filters;
    }

    public Rule(Parcel in) {
        name = in.readString();
        outputFilter = in.readString();
        outputDevice = in.readString();
        id = in.readInt();
        Parcelable[] par = in.readParcelableArray(Filter.class.getClassLoader());
        filters = new Filter[par.length];
        for (int i = 0; i < par.length; i++) {
            filters[i] = (Filter)par[i];
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(outputFilter);
        parcel.writeString(outputDevice);
        parcel.writeInt(id);
        parcel.writeParcelableArray(filters, 0);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Rule createFromParcel(Parcel in) {
            return new Rule(in);
        }

        public Rule[] newArray(int size) {
            return new Rule[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getOutputFilter() {
        return outputFilter;
    }

    public String getOutputDevice() {
        return outputDevice;
    }

    public int getId() {
        return id;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public boolean isRuleSatisfied(Model model){
        if(model instanceof Message){
            Message p = (Message)model;
            for (int i = 0; i < filters.length; i++) {
                if(filters[i].isFilterValid(model) == false){
                    L.i("Rule " + name + " filters did not satisfy given model");
                    return false;
                }
            }
            L.i("Rule " + name + " has passed filters");
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = name + " " + id + " " + outputFilter+ " " + outputDevice + " {";

        if(filters != null){
            for (int i = 0; i < filters.length; i++) {
                result += filters[i] + ",";
            }
        }
        else{
            result+= "NULL FILTER";
        }

        result += "}";
        return result;
    }
}

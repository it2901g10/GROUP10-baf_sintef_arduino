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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import no.ntnu.osnap.tshirt.helperClass.Rule;


public class RuleListAdapter extends BaseAdapter{

    Rule[] rules;
    Context context;

    public RuleListAdapter(Rule[] rules, Context context) {
        this.rules = rules;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rules.length;
    }

    @Override
    public Object getItem(int i) {
        return rules[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setText(rules[i].name);
        textView.setPadding(5,5,5,5);
        textView.setTextSize(18);

        layout.addView(textView);
        return layout;
    }

}

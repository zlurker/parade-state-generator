package com.example.talls.paradestategenerator;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Status extends AppCompatActivity {

    LinearLayout ll;
    RecyclerView rV;
    EditText status;
    Button addStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ll = findViewById(R.id.main);
        rV = new RecyclerView(getApplicationContext());

        RecyclerView.LayoutManager lM = new LinearLayoutManager(this);
        rV.setLayoutManager(lM);

        final StatusAdapter sA = new StatusAdapter();
        rV.setAdapter(sA);

        status = new EditText(getApplicationContext());
        addStatus = new Button(getApplicationContext());

        status.setTextColor(Color.WHITE);

        addStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ParadeStatePage.cStatus.add(new CustomStatus(status.getEditableText().toString().toUpperCase(), false, true));
                    sA.addItem();
                    status.setText("");
                }catch (Exception e){
                    Log.e("EXCEPTIONCAPTURED", e.toString());
                }
            }
        });

        addStatus.setText("Add Status");

        LinearLayout textHeaders = new LinearLayout(getApplicationContext());

        TextView text= new TextView(getApplicationContext());
        TextView swi= new TextView(getApplicationContext());
        TextView listSwi= new TextView(getApplicationContext());
        TextView del= new TextView(getApplicationContext());

        text.setTextColor(Color.WHITE);
        swi.setTextColor(Color.WHITE);
        listSwi.setTextColor(Color.WHITE);
        del.setTextColor(Color.WHITE);

        text.setText("Status");
        swi.setText("Show as Present");
        listSwi.setText("List Status Personnel");
        del.setText("Actions");

        textHeaders.addView(text);
        textHeaders.addView(swi);
        textHeaders.addView(listSwi);
        textHeaders.addView(del);

        text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));
        swi.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        listSwi.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        del.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));


        ll.addView(status);
        ll.addView(addStatus);
        ll.addView(textHeaders);

        ll.addView(rV);
    }


}

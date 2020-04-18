package com.example.talls.paradestategenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParadeStatePage extends AppCompatActivity {

    public static List<CustomStatus> cStatus;
    LinearLayout main;
    LinearLayout personnelList;

    Button addPersonnel;
    Button generateState;
    Button status;
    Button resetStatus;
    Button copyPersonnel;

    EditText name;
    EditText branchText;
    String branch;
    TextView credits;
    TextView branchDescript;
    TextView addPersonnelDescript;

    String[] statuses;
    EnhancedList<Personnel> pData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parade_state_page);
        main = findViewById(R.id.main);

        pData = new EnhancedList<>(new ArrayCreator<Personnel>() {
            @Override
            public Personnel[] createNewArray(int length) {
                return new Personnel[length];
            }
        });

        loadStatuses();


        branchDescript = new TextView(getApplicationContext());
        branchText = new EditText(getApplicationContext());
        personnelList = new LinearLayout(getApplicationContext());
        name = new EditText(getApplicationContext());
        addPersonnelDescript = new TextView(getApplicationContext());
        addPersonnel = new Button(getApplicationContext());
        generateState = new Button(getApplicationContext());
        status = new Button(getApplicationContext());
        resetStatus = new Button(getApplicationContext());

        personnelList.setOrientation(LinearLayout.VERTICAL);

        branchText.setTextColor(Color.WHITE);

        addPersonnel.setText("Add new Personnel");
        generateState.setText("Generate Parade State");

        addPersonnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> names = new ArrayList<>();
                try {
                    JSONArray personnelArray = new JSONArray(name.getEditableText().toString().toUpperCase());

                    for (int i = 0; i < personnelArray.length(); i++)
                        names.add((String) personnelArray.get(i));

                } catch (Exception e) {
                    names.add(name.getEditableText().toString().toUpperCase());
                }

                for (int i=0; i < names.size(); i++) {
                    int id = pData.add(new Personnel(names.get(i)));
                    generatePersonnel(id);
                }

                name.setText("");
            }
        });

        generateState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                generateReport();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent status = new Intent(getApplicationContext(), Status.class);
                startActivity(status);
            }
        });

        status.setText("Edit Status");

        resetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDefaultCustomStatus();
                refreshPage();
            }
        });

        resetStatus.setText("Restore Default Statuses");

        copyPersonnel = new Button(getApplicationContext());
        copyPersonnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("PersonnelList", convertPersonnelDataToJson());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Personnel List Generated", Toast.LENGTH_LONG).show();
            }
        });

        copyPersonnel.setText("Copy Personnel List");

        name.setTextColor(Color.WHITE);

        credits = new TextView(getApplicationContext());
        credits.setTextColor(Color.WHITE);
        credits.setText("Parade State Generator v1.00.0 \nProduct of N Games Studios ©2019");

        credits.setTextSize(9);

        branchDescript.setText("Enter Branch/ Unit Name Below:");
        addPersonnelDescript.setText("Enter Personnel Name/ Personnel Data List Below:");

        branchDescript.setTextColor(Color.WHITE);
        addPersonnelDescript.setTextColor(Color.WHITE);

        main.addView(branchDescript);
        main.addView(branchText);
        main.addView(personnelList);
        main.addView(addPersonnelDescript);
        main.addView(name);
        main.addView(addPersonnel);
        main.addView(generateState);
        main.addView(status);
        main.addView(resetStatus);
        main.addView(copyPersonnel);
        main.addView(credits);

        updateStatuses();
        loadPersonnel();

        branchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                branch = s.toString();
            }
        });
    }

    void loadStatuses() {
        File saveFile = new File(getApplicationContext().getFilesDir(), "StatusList");

        if (saveFile.exists()) {
            cStatus = new ArrayList<>();

            try {
                JSONObject statusFile = new JSONObject(readFile("StatusList"));
                JSONArray statusArray = statusFile.getJSONArray("statusArray");

                for (int i = 0; i < statusArray.length(); i++) {
                    JSONObject currStatus = statusArray.getJSONObject(i);
                    cStatus.add(new CustomStatus(currStatus.getString("name"), currStatus.getBoolean("presentStatus"), currStatus.getBoolean("listNames")));
                }

            } catch (Exception e) {
            }
        } else
            createDefaultCustomStatus();
    }

    void createDefaultCustomStatus() {
        cStatus = new ArrayList<>();
        cStatus.add(new CustomStatus("PRESENT", true, false));
        cStatus.add(new CustomStatus("OPS ROOM DUTY", true, true));
        cStatus.add(new CustomStatus("OUTSTATIONED", false, true));
        cStatus.add(new CustomStatus("LEAVE", false, true));
        cStatus.add(new CustomStatus("MA", false, true));
        cStatus.add(new CustomStatus("MC", false, true));
        cStatus.add(new CustomStatus("AWOL", false, true));
        cStatus.add(new CustomStatus("RSI", true, true));
        cStatus.add(new CustomStatus("RSO", false, true));
        cStatus.add(new CustomStatus("ON COURSE", false, true));
        cStatus.add(new CustomStatus("ATTACHED OUT", false, true));
        cStatus.add(new CustomStatus("OFF", false, true));
        cStatus.add(new CustomStatus("OTHERS", false, true));
        cStatus.add(new CustomStatus("LATE", false, true));
    }

    String convertPersonnelDataToJson() {
        JSONArray data = new JSONArray();
        Personnel[] activeList = pData.getActiveList();

        try {
            for (int i = 0; i < activeList.length; i++) {
                data.put(activeList[i].name);
            }
        } catch (Exception e) {

        }

        return data.toString();
    }

    void saveData() {
        JSONArray data = new JSONArray();
        Personnel[] activeList = pData.getActiveList();
        JSONObject details = new JSONObject();
        JSONObject status = new JSONObject();

        try {
            for (int i = 0; i < activeList.length; i++) {
                JSONObject databit = new JSONObject();

                databit.put("name", activeList[i].name);
                databit.put("status", activeList[i].status);
                data.put(databit);
            }

            File saveFile = new File(getApplicationContext().getFilesDir(), "PersonnelList");

            FileWriter writer = new FileWriter(saveFile);
            writer.append(data.toString());
            writer.flush();
            writer.close();

            details.put("branch", branch);

            File detailsSaveFile = new File(getApplicationContext().getFilesDir(), "Details");
            writer = new FileWriter(detailsSaveFile);
            writer.append(details.toString());
            writer.flush();
            writer.close();

            JSONArray statusArray = new JSONArray();

            for (int i = 0; i < cStatus.size(); i++) {
                JSONObject statusElement = new JSONObject();
                statusElement.put("name", cStatus.get(i).name);
                statusElement.put("presentStatus", cStatus.get(i).presentStatus);
                statusElement.put("listNames", cStatus.get(i).listNames);
                statusArray.put(statusElement);
            }

            status.put("statusArray", statusArray);

            File statusFile = new File(getApplicationContext().getFilesDir(), "StatusList");
            writer = new FileWriter(statusFile);
            writer.append(status.toString());
            writer.flush();
            writer.close();

        } catch (Exception e) {
        }
    }

    void generateReport() {

        if (cStatus.size() == 0) {
            Toast.makeText(getApplicationContext(), "ERROR - NO STATUS LIST DETECTED. PLEASE FILL UP THE LIST BEFORE GENERATING.", Toast.LENGTH_LONG).show();
            return;
        }

        Catergory[] cats = new Catergory[cStatus.size()];
        Personnel[] activeList = pData.getActiveList();

        for (int i = 0; i < cats.length; i++)
            cats[i] = new Catergory();

        for (int i = 0; i < activeList.length; i++)
            cats[activeList[i].status].personnelId.add(i);

        String report = "";

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("ddMMyy");
        String formattedDate = df.format(c);

        int numberPresent = 0;

        for (int i = 0; i < cats.length; i++) {

            if (cats[i].personnelId.size() > 0) {
                report += "\n\n" + cStatus.get(i).name + ": " + String.valueOf(cats[i].personnelId.size());

                if (cStatus.get(i).listNames)
                    for (int j = 0; j < cats[i].personnelId.size(); j++)
                        report += "\n" + activeList[cats[i].personnelId.get(j)].name;
            }

            if (cStatus.get(i).presentStatus)
                numberPresent += cats[i].personnelId.size();
        }

        report = branch + "\n" + formattedDate + "\n" + "PRESENT/TOTAL:" + String.valueOf(numberPresent) + "/" + String.valueOf(activeList.length) + report;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Report", report);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Parade State Generated", Toast.LENGTH_LONG).show();
    }

    void loadPersonnel() {
        try {
            JSONArray data = new JSONArray(readFile("PersonnelList"));

            for (int i = 0; i < data.length(); i++) {
                int id = pData.add(new Personnel(data.getJSONObject(i).getString("name"), data.getJSONObject(i).getInt("status")));
                generatePersonnel(id);
            }

            JSONObject obj = new JSONObject(readFile("Details"));
            branch = obj.getString("branch");
            branchText.setText(branch);

        } catch (Exception e) {
            Log.e("ExceptionError", e.toString());
        }
    }

    void generatePersonnel(int id) {
        final int persID = id;
        final LinearLayout personnelData = new LinearLayout(getApplicationContext());

        TextView personnelName = new TextView(getApplicationContext());
        personnelName.setText(pData.getGlobal(persID).name);
        personnelName.setTextColor(Color.WHITE);

        Spinner status = new Spinner(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_item_spinner, statuses);
        status.setAdapter(adapter);


        pData.getGlobal(persID).status = pData.getGlobal(persID).status >= statuses.length ? 0 : pData.getGlobal(persID).status;

        if (statuses.length > 0)
            status.setSelection(pData.getGlobal(persID).status);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pData.getGlobal(persID).status = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button deletePers = new Button(getApplicationContext());
        deletePers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personnelList.removeView(personnelData);
                pData.remove(persID);
            }
        });

        deletePers.setText("Delete Personnel");

        personnelName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        status.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        deletePers.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));

        personnelData.addView(personnelName);
        personnelData.addView(status);
        personnelData.addView(deletePers);

        personnelList.addView(personnelData);
    }

    public String readFile(String substring) {
        String line = null;

        try {
            File saveFile = new File(getApplicationContext().getFilesDir(), substring);

            FileInputStream fileInputStream = new FileInputStream(saveFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }

            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (Exception ex) {
        }
        return line;
    }

    void refreshPage() {
        personnelList.removeAllViews();
        updateStatuses();

        int pLength = pData.getActiveList().length;

        for (int i = 0; i < pLength; i++)
            generatePersonnel(pData.returnTrueInt(i));
    }

    public void onRestart() {
        super.onRestart();
        refreshPage();
    }

    void updateStatuses() {
        statuses = new String[cStatus.size()];

        for (int i = 0; i < statuses.length; i++)
            statuses[i] = cStatus.get(i).name;
    }
}

package oppen.phaedra.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import oppen.phaedra.PhaedraScreen;
import oppen.phaedra.R;

public class SettingsActivity extends Activity {

    private ImageButton backButton;
    private EditText homeCapsuleEditText;
    private Button saveButton;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        PhaedraScreen.configure(getWindow(), getResources());

        setContentView(R.layout.activity_settings);

        homeCapsuleEditText = (EditText) findViewById(R.id.home_capsule_edit_text);

        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = homeCapsuleEditText.getText().toString();

                if(address.startsWith("mercury://")){
                    prefs.edit().putString("home_capsule", address).commit();
                    toast("Home capsule updated");
                }else{
                    toast("Home capsule must start with mercury://");
                }
            }
        });

        String savedHomeCapsule = prefs.getString("home_capsule", null);

        if(savedHomeCapsule != null){
            homeCapsuleEditText.setText(savedHomeCapsule);
            saveButton.setText("Update");
        }
    }

    private void toast(String message){
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

    }
}
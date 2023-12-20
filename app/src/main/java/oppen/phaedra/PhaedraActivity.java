package oppen.phaedra;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import oppen.phaedra.bookmarks.ui.add.AddBookmarkActivity;
import oppen.phaedra.bookmarks.ui.show.BookmarksActivity;
import oppen.phaedra.image_viewer.ImageViewerActivity;
import oppen.phaedra.mercury.Mercury;
import oppen.phaedra.mercury.MercuryListener;
import oppen.phaedra.settings.SettingsActivity;

public class PhaedraActivity extends Activity {

    private static final String TAG = "PhaedraActivity";
    private static final String DEF_HOME = "mercury://oppen.digital/phaedra/";

    private static final Integer BOOKMARKS_SCREEN_REQ = 1;

    //Views
    private ImageButton overflowButton;
    private EditText addressEdit;
    private ListView mertextListView;
    private TextView loadingTextView;

    //Mercury
    private Mercury mercury;
    private MercuryListener mercuryListener;
    private MertextAdapter adapter;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        PhaedraScreen.configure(getWindow(), getResources());

        setContentView(R.layout.activity_phaedra);

        mercuryListener = new MercuryListener() {
            @Override
            public void showProgress(final String message) {
                Log.d(TAG, String.format("MercuryListener: showProgress(): %s", message));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingTextView.setVisibility(View.VISIBLE);
                        loadingTextView.setText(message);
                    }
                });
            }

            @Override
            public void error(String error) {
                Log.d(TAG, String.format("MercuryListener: error(): %s", error));
            }

            @Override
            public void response(final String address, int code, String meta, final ArrayList<String> lines) {
                Log.d(TAG, String.format("MercuryListener: response(): %s", meta));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingTextView.setVisibility(View.GONE);
                        addressEdit.setText(address);
                        addressEdit.clearFocus();
                        adapter = new MertextAdapter(PhaedraActivity.this, lines, new MertextListener() {
                            @Override
                            public void openBrowser(String address) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(address));
                                startActivity(browserIntent);
                            }

                            @Override
                            public void openMercury(String link) {
                                mercury.request(link);
                            }
                        });
                        mertextListView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void imageReady(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(ImageViewerActivity.createIntent(PhaedraActivity.this, path));
                    }
                });
            }
        };

        mercury = new Mercury(this.getCacheDir(), mercuryListener);

        overflowButton = (ImageButton) findViewById(R.id.overflow_button);
        addressEdit = (EditText) findViewById(R.id.address_edit);
        mertextListView = (ListView) findViewById(R.id.mertext_listview);
        loadingTextView = (TextView) findViewById(R.id.loading_text_view);

        findViewById(R.id.overflow_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverflow();
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverflow();
            }
        });

        addressEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    go();
                    return true;
                }
                return false;
            }
        });

        home();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
    }

    private void hideKeyboard(){
        //todo - can we hide keyboard on Android 1?
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BOOKMARKS_SCREEN_REQ && resultCode == RESULT_OK){
            updateAddressAndGo(data.getStringExtra("address"));
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showOverflow(){
        MainMenu menu = new MainMenu(PhaedraActivity.this, overflowButton, new MainMenu.Listener() {
            @Override
            public void goBack() {
                back();
            }

            @Override
            public void goHome() {
                home();
            }

            @Override
            public void showBookmarks() {
                startActivityForResult(new Intent(PhaedraActivity.this, BookmarksActivity.class), BOOKMARKS_SCREEN_REQ);
            }

            @Override
            public void addBookmark() {
                startActivity(AddBookmarkActivity.createIntent(PhaedraActivity.this, addressEdit.getText().toString()));
            }

            @Override
            public void showSettings() {
                startActivity(new Intent(PhaedraActivity.this, SettingsActivity.class));
            }

            @Override
            public void showAbout() {
                Toast.makeText(PhaedraActivity.this, "Made by ÖLAB: oppen.digital", Toast.LENGTH_SHORT).show();
            }
        });
        menu.show();
    }

    private void updateAddressAndGo(String address){
        updateAddress(address);
        go();
    }

    private void updateAddress(String address){
        addressEdit.setText(address);
        addressEdit.setSelection(address.length()-1);
    }

    private void go(){
        hideKeyboard();
        String address = addressEdit.getText().toString();
        mercury.request(address);
    }

    private void back(){
        if(mercury.canGoBack()){
            String previous = mercury.goBack();
            addressEdit.setText(previous);
            go();
        }
    }

    private void home(){
        mercury.clearHistory();
        String homeAddress = prefs.getString("home_capsule", DEF_HOME);
        addressEdit.setText(homeAddress);
        go();
    }
}

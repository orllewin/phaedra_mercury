package oppen.phaedra.bookmarks.ui.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import oppen.phaedra.PhaedraScreen;
import oppen.phaedra.R;
import oppen.phaedra.bookmarks.Bookmark;
import oppen.phaedra.bookmarks.Bookmarks;

public class AddBookmarkActivity extends Activity {

    public static Intent createIntent(Context context, String address){
        Intent intent = new Intent(context, AddBookmarkActivity.class);
        intent.putExtra("address", address);
        return intent;
    }

    private EditText bookmarkLabelEditText;
    private EditText bookmarkAddressEditText;
    private Button saveButton;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhaedraScreen.configure(getWindow(), getResources());

        setContentView(R.layout.activity_add_bookmark);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ImageButton close = (ImageButton) findViewById(R.id.back_button);
        close.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bookmarkLabelEditText = (EditText) findViewById(R.id.bookmark_label_edit_text);

        bookmarkAddressEditText = (EditText) findViewById(R.id.bookmark_address_edit_text);

        String address = getIntent().getStringExtra("address");
        bookmarkAddressEditText.setText(address);

        bookmarkLabelEditText.setText(address.replace("mercury://", ""));

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bookmarks bookmarks = new Bookmarks(prefs, new Bookmarks.Listener() {
                    @Override
                    public void onBookmarkAdded() {
                        Toast.makeText(AddBookmarkActivity.this, "Bookmark saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onBookmarks(ArrayList<Bookmark> bookmarks) {
                        //unused
                    }

                    @Override
                    public void onBookmarkDeleted() {
                        //unused
                    }
                });

                bookmarks.addBookmark(
                        bookmarkAddressEditText.getText().toString(),
                        bookmarkLabelEditText.getText().toString());
            }
        });
    }
}

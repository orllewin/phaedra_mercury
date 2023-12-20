package oppen.phaedra.bookmarks.ui.show;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import oppen.phaedra.PhaedraScreen;
import oppen.phaedra.R;
import oppen.phaedra.bookmarks.Bookmark;
import oppen.phaedra.bookmarks.Bookmarks;

public class BookmarksActivity extends Activity {

    private ListView bookmarksListView;
    private TextView noBookmarksTextView;
    private Bookmarks bm;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhaedraScreen.configure(getWindow(), getResources());

        setContentView(R.layout.activity_bookmarks);

        noBookmarksTextView = (TextView) findViewById(R.id.no_bookmarks_text_view);

        ImageButton close = (ImageButton) findViewById(R.id.back_button);
        close.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bookmarksListView = (ListView) findViewById(R.id.bookamrks_list_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        bm = new Bookmarks(prefs, new Bookmarks.Listener() {
            @Override
            public void onBookmarkAdded() {
                //unused
            }

            @Override
            public void onBookmarks(ArrayList<Bookmark> bookmarks) {
                if(bookmarks.size() > 0){
                    noBookmarksTextView.setVisibility(View.GONE);
                }else {
                    noBookmarksTextView.setVisibility(View.VISIBLE);
                }

                setAdapter(bookmarks);
            }

            @Override
            public void onBookmarkDeleted() {
                bm.getBookmarks();
            }
        });

        bm.getBookmarks();
    }

    private void setAdapter(ArrayList<Bookmark> bookmarks) {
        BookmarksAdapter adapter = new BookmarksAdapter(this, bookmarks, new BookmarksAdapter.Listener() {
            @Override
            public void go(String address) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("address", address);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void overflow(final Bookmark bookmark, View view) {
                final PopupWindow overflow = new PopupWindow(BookmarksActivity.this, null);
                LinearLayout layout = (LinearLayout) View.inflate(BookmarksActivity.this, R.layout.bookmark_overflow, null);
                overflow.setContentView(layout);

                overflow.setBackgroundDrawable(null);

                TextView delete = (TextView) layout.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overflow.dismiss();
                        bm.deleteBookmark(bookmark);
                    }
                });

                TextView cancel = (TextView) layout.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overflow.dismiss();
                    }
                });

                overflow.showAsDropDown(view);
            }
        });

        bookmarksListView.setAdapter(adapter);
    }
}

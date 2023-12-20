package oppen.phaedra.bookmarks.ui.show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import oppen.phaedra.R;
import oppen.phaedra.bookmarks.Bookmark;


public class BookmarksAdapter extends ArrayAdapter<Bookmark> {

    public interface Listener{
        void go(String address);
        void overflow(Bookmark bookmark, View view);
    }

    private final Listener listener;

    public BookmarksAdapter(Context context, ArrayList<Bookmark> bookmarks, Listener listener){
        super(context, 0, bookmarks);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Bookmark bookmark = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bookmark, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(bookmark.title);

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(bookmark.address);

        final ImageButton overflow = (ImageButton) convertView.findViewById(R.id.overflow_button);

        overflow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.overflow(bookmark, overflow);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.go(bookmark.address);
            }
        });


        return convertView;
    }
}

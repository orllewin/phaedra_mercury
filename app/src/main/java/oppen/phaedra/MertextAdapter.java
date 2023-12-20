package oppen.phaedra;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MertextAdapter extends ArrayAdapter<String> {

    private final MertextListener mertextListener;

    public MertextAdapter(Context context, ArrayList<String> lines, MertextListener mertextListener){
        super(context, 0, lines);
        this.mertextListener = mertextListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String line = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mertext_line, parent, false);
        }

        TextView lineTextView = (TextView) convertView.findViewById(R.id.line);

        if(line.startsWith("=>")) {
            final String[] linkSegments = line.substring(3).split(" ");
            if(linkSegments.length == 1) {
                SpannableString content = new SpannableString(linkSegments[0]);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                lineTextView.setText(content);
            }else{
                String link = linkSegments[0].trim();
                SpannableString content = new SpannableString(line.substring(line.indexOf("=> " + link) + link.length() + 4));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                lineTextView.setText(content);
            }

            lineTextView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    String uri = linkSegments[0];
                    if(uri.startsWith("http")){
                        mertextListener.openBrowser(uri);
                    }else {
                        mertextListener.openMercury(uri);
                    }
                }
            });

            //This fixes the need for a double tap to register a click
            lineTextView.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        v.requestFocus();
                    }
                    return false;
                }
            });
        }else{
            lineTextView.setText(line);
            convertView.setOnClickListener(null);
        }

        return convertView;
    }
}

package oppen.phaedra.mercury;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public interface MercuryListener {
    void showProgress(String message);
    void error(String error);
    void response(String address, int code, String meta, ArrayList<String> lines);
    void imageReady(String path);
}

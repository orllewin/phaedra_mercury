package oppen.phaedra;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PhaedraScreen {

    public static void configure(Window window, Resources resources){
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }
}

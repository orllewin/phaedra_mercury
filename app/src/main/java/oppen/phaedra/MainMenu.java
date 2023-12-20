package oppen.phaedra;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainMenu implements View.OnClickListener{

    private final Context context;
    private final PopupWindow window;
    private final View anchor;
    private final Listener listener;

    private final LinearLayout layout;

    public MainMenu(Context context, View anchor, Listener listener){
        this.context = context;
        this.anchor = anchor;
        this.listener = listener;

        window = new PopupWindow(context, null);
        layout = (LinearLayout) View.inflate(context, R.layout.main_menu, null);
        window.setContentView(layout);

        window.setBackgroundDrawable(null);

        layout.findViewById(R.id.go_home).setOnClickListener(this);
        layout.findViewById(R.id.go_back).setOnClickListener(this);
        layout.findViewById(R.id.add_bookmark).setOnClickListener(this);
        layout.findViewById(R.id.bookmarks).setOnClickListener(this);
        layout.findViewById(R.id.preferences).setOnClickListener(this);
        layout.findViewById(R.id.about).setOnClickListener(this);
        layout.findViewById(R.id.cancel).setOnClickListener(this);
    }

    public void show(){
        layout.measure( View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
        int height = layout.getMeasuredHeight();
        int width = layout.getMeasuredWidth();
        window.setWidth( width );
        window.setHeight( height );
        window.showAsDropDown(anchor);
    }

    public boolean isShowing(){
        return window.isShowing();
    }

    public void dismiss(){
        window.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_back:
                listener.goBack();
                break;
            case R.id.go_home:
                listener.goHome();
                break;
            case R.id.add_bookmark:
                listener.addBookmark();
                break;
            case R.id.bookmarks:
                listener.showBookmarks();
                break;
            case R.id.preferences:
                listener.showSettings();
                break;
            case R.id.about:
                listener.showAbout();
                break;
            case R.id.cancel:
                //NOOP
                break;

        }

        dismiss();
    }

    interface Listener{
        void goBack();
        void goHome();
        void showBookmarks();
        void addBookmark();
        void showSettings();
        void showAbout();
    }
}

package oppen.phaedra.image_viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import oppen.phaedra.PhaedraScreen;
import oppen.phaedra.R;

public class ImageViewerActivity extends Activity {

    public static Intent createIntent(Context context, String path) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra("path", path);
        return intent;
    }

    private ImageView imageView;
    private ImageButton backButton;

    private String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhaedraScreen.configure(getWindow(), getResources());

        setContentView(R.layout.activity_view_image);

        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = (ImageView) findViewById(R.id.image_view);

        path = getIntent().getStringExtra("path");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(path != null){
            try{
                Drawable image = Drawable.createFromPath(path);
                imageView.setImageDrawable(image);
            }catch(Exception exception){
                toast(exception.toString());
            }
        }else{
            toast("No path to image available - developer error?");
        }

    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}
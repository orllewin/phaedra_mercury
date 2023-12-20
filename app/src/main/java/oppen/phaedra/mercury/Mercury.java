package oppen.phaedra.mercury;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;

public class Mercury {

    private static final String TAG = "Mercury";
    private final MercuryListener listener;
    private final File cacheDir;
    private Uri prevUri = null;

    private final ArrayList<String> history = new ArrayList();

    public Mercury(File cacheDir, MercuryListener listener){
        this.cacheDir = cacheDir;
        this.listener = listener;
    }

    public void request(String link){
        if(prevUri != null && !link.startsWith("mercury://")){
            if(prevUri.getPathSegments().size() > 0 && prevUri.getLastPathSegment().contains(".")){
                prevUri = Uri.parse(prevUri.toString().substring(0, prevUri.toString().lastIndexOf("/")));
            }
            prevUri = prevUri.buildUpon().appendPath(link).build();
        }else{
            prevUri = Uri.parse(link);
        }

        String url = prevUri.toString();
        String fixed = "mercury://" + url
                .replace("mercury://", "")
                .replace("%2F", "/")
                .replace("//", "/")
                .replace("/./", "/");

        requestThreaded(Uri.parse(fixed));
    }

    private void requestThreaded(final Uri uri){
        listener.showProgress("loading: " + uri.toString().replace("mercury://", ""));
        new Thread() {
            @Override
            public void run() {
                int port = 1963;
                if(uri.getPort() != -1) port = uri.getPort();

                try {

                    Socket socket = new Socket(uri.getHost(), port);

                    //Out
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    PrintWriter outWriter = new PrintWriter(bufferedWriter);

                    //Not sure where this is coming from - but remove any encoded slash
                    String requestEntity = uri.toString() + "\r\n";

                    Log.d(TAG, String.format("Phaedra socket requesting %s", requestEntity));
                    outWriter.print(requestEntity);
                    outWriter.flush();

                    //In
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader headerInputReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(headerInputReader);
                    String headerLine = bufferedReader.readLine();

                    Log.d(TAG, "Phaedra: response header: " + headerLine);

                    if(headerLine == null){
                        listener.error("Server did not respond with a Mercury header");
                        //todo - close all
                        return;
                    }

                    int responseCode = Character.getNumericValue(headerLine.charAt(0));
                    String meta = headerLine.substring(headerLine.indexOf(" ")).trim();


                    switch (responseCode) {
                        case 2: {
                            if (meta.startsWith("text/gemini") || meta.startsWith("text/mercury")) {
                                ArrayList lines = new ArrayList<String>();
                                String line = bufferedReader.readLine();

                                while (line != null) {
                                    lines.add(line);
                                    line = bufferedReader.readLine();
                                }

                                history.add(uri.toString());

                                listener.response(uri.toString(), responseCode, meta, lines);
                            }else{
                                //Not Gemtext/Mertext - download binary
                                if(meta.startsWith("image/")){
                                    File cachedImage = download(socket, uri);
                                    listener.imageReady(cachedImage.getAbsolutePath());
                                }else{
                                    listener.showProgress("Not supported: " + meta);
                                }
                            }
                        }
                    }


                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private File download(Socket socket, Uri uri){
        String filename = uri.getLastPathSegment();



        File downloadFile = new File(cacheDir, filename);

        Log.d(TAG, "Phaedra: caching download file: " + downloadFile.getPath());

        if(downloadFile.exists()) downloadFile.delete();
        try {
            downloadFile.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(downloadFile);
            byte[] buffer = new byte[1024];
            int len = socket.getInputStream().read(buffer);
            while (len != -1) {
                outputStream.write(buffer, 0, len);
                len = socket.getInputStream().read(buffer);
            }
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return downloadFile;
    }

    public boolean canGoBack(){
        return (history.size() > 1);
    }

    public String goBack(){
        String last = history.get(history.size() - 2);
        history.remove(history.size() - 1);
        history.remove(history.size() - 1);
        return last;
    }

    public void clearHistory() {
        history.clear();
    }
}

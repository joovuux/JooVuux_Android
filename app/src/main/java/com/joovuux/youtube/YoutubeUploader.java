package com.joovuux.youtube;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Video;
import android.util.Log;

import ua.net.lsoft.joovuux.R;

public class YoutubeUploader {

    private static final String TAG = "YoutubeUploader";

    // After creating project at http://www.appspot.com DEFAULT_YTD_DOMAIN == <Developers Console Project ID>.appspot.com [ You can find from Project -> Administration -> Application settings]
    public static final String DEFAULT_YTD_DOMAIN = "s~demopairpost.appspot.com";

    // I used Google APIs Console Project Title as Domain name:
    public static final String DEFAULT_YTD_DOMAIN_NAME = "YajuPairPost";

    //From Google Developer Console from same project (Created by SHA1; project package)
    //Example https://console.developers.google.com/project/apps~gtl-android-youtube-test/apiui/credential
    public static final String DEVELOPER_KEY = "AIzaSyDJ7RGmBCUJxLx6hOp4O57KdvvGnvuXgfI";

    // CLIENT_ID == Google APIs Console Project Number:
    public static final String CLIENT_ID = "213262993835";

    public static final String YOUTUBE_AUTH_TOKEN_TYPE = "youtube";

    private static final String AUTH_URL = "https://www.google.com/accounts/ClientLogin";

    // Uploader's user-name and password
    private static final String USEsR_NAME = "zlenko.d.s@gmail.com";
    private static final String PASsSWORD = "3atqngrgtt21";

    private static final String INITIAL_UPLOAD_URL = "https://uploads.gdata.youtube.com/resumable/feeds/api/users/default/uploads";
    private static String userName;
    private static String password;

    public static String getClientAuthToken(String userName, String password) {

        try {

            URL url = new URL(AUTH_URL);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String template = "Email=%s&Passwd=%s&service=%s&source=%s";

//            String userName = USER_NAME; // TODO
//            String password = PASSWORD; // TODO

            String service = YOUTUBE_AUTH_TOKEN_TYPE;
            String source = CLIENT_ID;

            userName = URLEncoder.encode(userName, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");

            String loginData = String.format(template, userName, password, service, source);

            OutputStreamWriter outStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outStreamWriter.write(loginData);
            outStreamWriter.close();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode != 200) {

                Log.d(TAG, "Got an error response : " + responseCode + " "  + urlConnection.getResponseMessage());

                throw new IOException(urlConnection.getResponseMessage());

            } else {

                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;

                while ((line = br.readLine()) != null) {

                    if (line.startsWith("Auth=")) {

                        String split[] = line.split("=");
                        String token = split[1];

                        Log.d(TAG, "Auth Token : " + token);
                        return token;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String upload(YoutubeUploadRequest uploadRequest, ProgressListner listner, Activity activity, String userName, String password) {

        totalBytesUploaded = 0;

        String authToken = getClientAuthToken(userName, password);
        YoutubeUploader.userName = userName;
        YoutubeUploader.password = password;

        if(authToken != null) {

            String uploadUrl = uploadMetaData(uploadRequest, authToken, activity, true);

//            File file = getFileFromUri(uploadRequest.getUri(), activity);
            Log.d("myLogs", "URL UPLOAD:   " + uploadUrl);

            File file = new File(uploadRequest.getUri().getEncodedPath());


            long currentFileSize = file.length();

            int uploadChunk = 1024 * 1024 * 3; // 3MB

            int start = 0;
            int end = -1;

            String videoId = null;

            double fileSize = currentFileSize;

            while (fileSize > 0) {

                if (fileSize - uploadChunk > 0) {
                    end = start + uploadChunk - 1;
                } else {
                    end = start + (int) fileSize - 1;
                }

                Log.d(TAG, String.format("start=%s end=%s total=%s", start, end, file.length()));

                try {

                    videoId = gdataUpload(file, uploadUrl, start, end, authToken, listner);
                    fileSize -= uploadChunk;
                    start = end + 1;
                } catch (IOException e) {
                    Log.d(TAG,"Error during upload : " + e.getMessage());
                }
            }

            if (videoId != null) {
                return videoId;
            }
        }

        return null;
    }

    public static int totalBytesUploaded = 0;

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("resource")
    private static String gdataUpload(File file, String uploadUrl, int start, int end, String clientLoginToken, ProgressListner listner) throws IOException {

        int chunk = end - start + 1;
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        FileInputStream fileStream = new FileInputStream(file);

        URL url = new URL(uploadUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", String.format("GoogleLogin auth=\"%s\"",  clientLoginToken));
        urlConnection.setRequestProperty("GData-Version", "2");
        urlConnection.setRequestProperty("X-GData-Client", CLIENT_ID);
        urlConnection.setRequestProperty("X-GData-Key", String.format("key=%s", DEVELOPER_KEY));
        // some mobile proxies do not support PUT, using X-HTTP-Method-Override to get around this problem

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("X-HTTP-Method-Override", "PUT");
        urlConnection.setDoOutput(true);

        urlConnection.setFixedLengthStreamingMode(chunk);
        urlConnection.setRequestProperty("Content-Type", "video/3gpp");
        urlConnection.setRequestProperty("Content-Range", String.format("bytes %d-%d/%d", start, end,
                file.length()));

        Log.d(TAG, urlConnection.getRequestProperty("Content-Range"));

        OutputStream outStreamWriter = urlConnection.getOutputStream();

        fileStream.skip(start);

        double currentFileSize = file.length();

        int bytesRead;
        int totalRead = 0;
        while ((bytesRead = fileStream.read(buffer, 0, bufferSize)) != -1) {
            outStreamWriter.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            totalBytesUploaded += bytesRead;

            double percent = (totalBytesUploaded / currentFileSize) * 100;

            if(listner != null){
                listner.onUploadProgressUpdate((int) percent);
            }

            System.out.println("GTL You tube upload progress: " + percent + "%");
             /*
                Log.d(LOG_TAG, String.format(
                "fileSize=%f totalBytesUploaded=%f percent=%f", currentFileSize,
                totalBytesUploaded, percent));
             */

            //dialog.setProgress((int) percent);
            // TODO My settings

            if (totalRead == (end - start + 1)) {
                break;
            }
        }

        outStreamWriter.close();

        int responseCode = urlConnection.getResponseCode();

        Log.d(TAG, "responseCode=" + responseCode);
        Log.d(TAG, "responseMessage=" + urlConnection.getResponseMessage());

        try {
            if (responseCode == 201) {
                String videoId = parseVideoId(urlConnection.getInputStream());

                return videoId;
            } else if (responseCode == 200) {
                Set<String> keySet = urlConnection.getHeaderFields().keySet();
                String keys = urlConnection.getHeaderFields().keySet().toString();
                Log.d(TAG, String.format("Headers keys %s.", keys));
                for (String key : keySet) {
                    Log.d(TAG, String.format("Header key %s value %s.", key, urlConnection.getHeaderField(key)));
                }
                Log.w(TAG, "Received 200 response during resumable uploading");
                throw new IOException(String.format("Unexpected response code : responseCode=%d responseMessage=%s", responseCode,
                        urlConnection.getResponseMessage()));
            } else {
                if ((responseCode + "").startsWith("5")) {
                    String error = String.format("responseCode=%d responseMessage=%s", responseCode,
                            urlConnection.getResponseMessage());
                    Log.w(TAG, error);
                    // TODO - this exception will trigger retry mechanism to kick in
                    // TODO - even though it should not, consider introducing a new type so
                    // TODO - resume does not kick in upon 5xx
                    throw new IOException(error);
                } else if (responseCode == 308) {
                    // OK, the chunk completed succesfully
                    Log.d(TAG, String.format("responseCode=%d responseMessage=%s", responseCode,
                            urlConnection.getResponseMessage()));
                } else {
                    // TODO - this case is not handled properly yet
                    Log.w(TAG, String.format("Unexpected return code : %d %s while uploading :%s", responseCode,
                            urlConnection.getResponseMessage(), uploadUrl));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String parseVideoId(InputStream atomDataStream) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(atomDataStream);

        NodeList nodes = doc.getElementsByTagNameNS("*", "*");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName != null && nodeName.equals("yt:videoid")) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return null;
    }

    private static File getFileFrsomUri(Uri uri, Activity activity) {

        try {
            String filePath = null;

            String[] proj = { Video.VideoColumns.DATA };

            Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);

            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(Video.VideoColumns.DATA);
                filePath = cursor.getString(column_index);
            }

            cursor.close();

            //String filePath = cursor.getString(cursor.getColumnIndex(Video.VideoColumns.DATA));

            File file = new File(filePath);
            cursor.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String uploadMetaData(YoutubeUploadRequest uploadRequest, String clientLoginToken, Activity activity, boolean retry) {

        try {

//            File file = getFileFromUri(uploadRequest.getUri(), activity);

            File file = new File(uploadRequest.getUri().getEncodedPath());

            if(file != null) {

                String uploadUrl = INITIAL_UPLOAD_URL;
                URL url = new URL(uploadUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", String.format("GoogleLogin auth=\"%s\"", clientLoginToken));
                connection.setRequestProperty("GData-Version", "2");
                connection.setRequestProperty("X-GData-Client", CLIENT_ID);
                connection.setRequestProperty("X-GData-Key", String.format("key=%s", DEVELOPER_KEY));

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/atom+xml");
                connection.setRequestProperty("Slug", file.getAbsolutePath());

                String title = uploadRequest.getTitle();
                String description = uploadRequest.getDescription();
                String category = uploadRequest.getCategory();
                String tags = uploadRequest.getTags();

                String template = readFile(activity, R.raw.gdata).toString();
                String atomData = String.format(template, title, description, category, tags);

                /*String template = readFile(activity, R.raw.gdata_geo).toString();
                atomData = String.format(template, title, description, category, tags,
                    videoLocation.getLatitude(), videoLocation.getLongitude());*/

                OutputStreamWriter outStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outStreamWriter.write(atomData);
                outStreamWriter.close();

                int responseCode = connection.getResponseCode();

                if (responseCode < 200 || responseCode >= 300) {

                    // The response code is 40X

                    if ((responseCode + "").startsWith("4") && retry) {

                        Log.d(TAG, "retrying to fetch auth token for ");

                        clientLoginToken = getClientAuthToken(userName, password);

                        // Try again with fresh token
                        return uploadMetaData(uploadRequest, clientLoginToken, activity, false);
                    } else {
                        return null;
                    }
                }

                return connection.getHeaderField("Location");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CharSequence readFile(Activity activity, int id) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(id)));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = in.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            // Chomp the last newline
            buffer.deleteCharAt(buffer.length() - 1);
            return buffer;
        } catch (IOException e) {
            return "";
        } finally {
            closeStream(in);
        }
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public static interface ProgressListner {

        void onUploadProgressUpdate(int progress);
    }
}
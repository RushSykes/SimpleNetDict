package ADT;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Jinshan {
    private String phoneticUS;
    private String phoneticUK;
    private String explains;
    private String webExplains;
    private int errorCode;

    public String getPhoneticUS() {
        return phoneticUS;
    }

    public String getPhoneticUK() {
        return phoneticUK;
    }

    public String getExplains() {
        return explains;
    }

    public String getWebExplains() {
        return webExplains;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void query(String word) {
        String pack = "";
        BufferedReader in = null;
        try {
            URL urlNameString = new URL(
                    "http://dict-co.iciba.com/api/dictionary.php?key=1537178571501B9DCE979B3B16C3A72D&type=json&w="+word);

            // Create connection with the URL
            HttpURLConnection connection = (HttpURLConnection)urlNameString.openConnection();
            // Set universal request properties
            connection.setRequestProperty("encoding", "UTF-8");
            connection.setRequestMethod("GET");

            // Establish real connection
            connection.connect();

            // Read json data pack into line
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                pack += line;
            }
        }
        catch (MalformedURLException ex) {
            System.out.println("Server: URL malformed!\n");
        }
        catch(IOException ex) {
            System.out.println("Server: Readings invalid!");
        }

        // Close buffer reader in stream
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception ex) {
                System.err.println("Server: " + ex);
            }
        }

        // Process result data pack
        JsonObject object = new JsonParser().parse(pack).getAsJsonObject();

        JsonArray info = object.get("symbols").getAsJsonArray();

        phoneticUS = info.get(0).getAsJsonObject().get("ph_am").getAsString();
        phoneticUK = info.get(0).getAsJsonObject().get("ph_en").getAsString();

        explains = "";

        explains += (word +"\n美:[" + phoneticUS + "]\t"
                + "英:[" + phoneticUK  + "]\n\n");


        JsonArray parts = info.get(0).getAsJsonObject().get("parts").getAsJsonArray();

        for(int i = 0 ; i < parts.size(); i++) {
            JsonObject temp = parts.get(i).getAsJsonObject();
            explains += (temp.get("part").getAsString() + "\t");
            JsonArray tempAr = temp.get("means").getAsJsonArray();
            for(int j = 0; j < tempAr.size(); j++) {
                explains += (tempAr.get(j).getAsString() + " ");
            }
            explains += "\n";
        }
    }
}
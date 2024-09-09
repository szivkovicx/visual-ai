import java.io.File;
import java.util.Scanner;

import org.json.JSONObject;

public class Configuration {
    public JSONObject read() {
        String confString = "";
        try {
            File myObj = new File("../conf.json");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                confString += data;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("Default configuration not found. Please write a conf.json file inside the model directory.");
            System.exit(0);
        }
        JSONObject obj = new JSONObject(confString);
        final String[] requiredKeys = { "name", "version", "labels", "training_parameters" }; 
        for (String key : requiredKeys) {
            if (!obj.has(key)) {
                System.out.println("Missing required '" + key + "' key in validation.");
                System.exit(0);  
            }
        }
        return obj;
    }
}

import org.json.JSONArray;

public class Logger {
  public static void info(
    String message,
    Boolean endNewLine
  ) {
    System.out.println("\n [ LOG ] " + message + (endNewLine ? "\n" : ""));
  }

  public static void preTrainingSummaryLog(
      String name,
      String version,
      JSONArray labels
  ) {
      System.out.println("\n");

      System.out.println("---------- Meta Summary ----------");
  
      System.out.println("Model Name: " + name);
      System.out.println("Model Version: " + version);

      System.out.println("---------- Data Summary ----------");
  
      for (int i = 0; i < labels.length(); i++) {
        System.out.println(
          "Label " + (i+1) + ": " + labels.getJSONObject(i).getString("id")
        );
        System.out.println(
          "Label " + (i+1) + " images ( for training ): " + labels.getJSONObject(i).getJSONArray("data").length()
        );
      }
  }
}
package online.stocktweets.StockTweets.util;

import java.io.*;
import java.util.ArrayList;

public class Utils {

    private static long systime;

    public static ArrayList<String> readTxtFile(String path){
        ArrayList<String> out = new ArrayList<>();
        File file = new File(path);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String str;

        while ((str = bufferedReader.readLine()) != null) {
            out.add(str);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }


    public static void writeTxtFile(String path, ArrayList<String> content){
        File file = new File(path);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            for (String str : content) {
                bw.write(str);
                bw.newLine();
            }

            bw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startMeasureTime() {
        systime = System.currentTimeMillis();
    }

    public static void measureTime(String message) {
        System.out.println(message + ": " + (System.currentTimeMillis() - systime)/1000.0);
    }
}

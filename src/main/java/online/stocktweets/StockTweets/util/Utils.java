package online.stocktweets.StockTweets.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Utils {

    public static class readFiles {
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
    }
}

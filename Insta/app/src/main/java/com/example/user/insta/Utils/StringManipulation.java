package com.example.user.insta.Utils;

/**
 * Created by USER on 11/13/2017.
 */

public class StringManipulation {

    public static String expandUsernamme(String username){return username.replace("_"," ");}

    public static String condenserUsername(String username){return username.replace(" ","_");}

    public static String getTags(String string){
        if (string.indexOf("#") > 0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for (char c: charArray){
                if (c == '#'){
                    foundWord = true;
                    sb.append(c);
                }else {
                    if (foundWord){
                        sb.append(c);
                    }
                }
                if (c == ' '){
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1,s.length());
        }
        return string;
    }
}

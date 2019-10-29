package com.example.lukas.multiplicationtablelearner;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void string() throws Exception {
        List<Integer> ints = new ArrayList<>();

        ints.add(1);
        ints.add(2);

        String str = ints.toString();

        str = str.replace("[", "");
        str = str.replace("]", "");
        str = str.replace(",", "");
        str = str.replace(" ", "");

        int i = Integer.parseInt(str);
    }

    @Test
    public void string2() throws Exception {
        List<Integer> ints = new ArrayList<>();

        String number = "123";

        //String str = number.replace("", " ");
        String [] strs = number.split("");
    }

    @Test
    public void rand() throws Exception {
        List<Integer> ints = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            ints.add((int)(Math.random()*2));
        }

        ints.toString();
    }

    @Test
    public void locale() throws Exception {
        Locale[] locales = Locale.getAvailableLocales();
        List<Locale> localeList = new ArrayList<Locale>();
        for (Locale locale : locales) {
            localeList.add(locale);
//            int res = tts.isLanguageAvailable(locale);
//            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
//                localeList.add(locale);
//            }
        }
        localeList.toString();

    }
}
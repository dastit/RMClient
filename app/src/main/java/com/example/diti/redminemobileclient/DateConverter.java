package com.example.diti.redminemobileclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    public static String getDate(String stringDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date;
        Calendar cal = Calendar.getInstance();
        try {
            date = formatter.parse(stringDate.replaceAll("Z$", "+0000"));
            cal.setTime(date);
            String day = addZero(cal.get(Calendar.DATE));
            String month =addZero( cal.get(Calendar.MONTH) + 1);
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String hours =addZero( cal.get(Calendar.HOUR_OF_DAY));
            String minutes =addZero( cal.get(Calendar.MINUTE));
            stringDate = day + "." + month + "." + year + " " + hours + ":" + minutes;
        } catch (ParseException e) {
            stringDate = "";
            e.printStackTrace();
        }
        return stringDate;
    }

    public static String addZero(int i){
        String x = String.valueOf(i);
        if(i<10){
            x = 0+x;
        }
        return x;
    }
}

package com.example.todo.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    //zamiana jpd/pdf na obiekt
    public static List<String> fromString(String value) {
        Gson gson = new Gson();
        List<String> list = new ArrayList<>();
        list = gson.fromJson(value, list.getClass());
        return list;
    }

    //zmiana na liste  nazw plikow
    @TypeConverter
    public static String fromArrayList(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    //zmiana liczby na date
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
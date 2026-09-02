package me.zyouime.holymoderation.config.setting;

import com.google.common.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.List;

public class SettingsTypes {

    public static final Type INTEGER = new TypeToken<Integer>() {}.getType();
    public static final Type FLOAT = new TypeToken<Float>() {}.getType();
    public static final Type STRING = new TypeToken<String>() {}.getType();
    public static final Type BOOLEAN = new TypeToken<Boolean>() {}.getType();
    public static final Type COLOR = new TypeToken<Color>() {}.getType();
    public static final Type LIST_STRING = new TypeToken<List<String>>() {}.getType();
}
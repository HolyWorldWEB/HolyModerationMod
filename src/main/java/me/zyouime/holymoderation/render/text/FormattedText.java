package me.zyouime.holymoderation.render.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class FormattedText {

    public static final char SECTION = '§';

    private FormattedText() {
    }

    private static Color colorOf(char code) {
        return switch (Character.toLowerCase(code)) {
            case '0' -> new Color(0, 0, 0);
            case '1' -> new Color(0, 0, 170);
            case '2' -> new Color(0, 170, 0);
            case '3' -> new Color(0, 170, 170);
            case '4' -> new Color(170, 0, 0);
            case '5' -> new Color(170, 0, 170);
            case '6' -> new Color(255, 170, 0);
            case '7' -> new Color(170, 170, 170);
            case '8' -> new Color(85, 85, 85);
            case '9' -> new Color(85, 85, 255);
            case 'a' -> new Color(85, 255, 85);
            case 'b' -> new Color(85, 255, 255);
            case 'c' -> new Color(255, 85, 85);
            case 'd' -> new Color(255, 85, 255);
            case 'e' -> new Color(255, 255, 85);
            case 'f' -> new Color(255, 255, 255);
            default -> null;
        };
    }

    public static List<Run> parse(String input, Color defaultColor) {
        List<Run> runs = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return runs;
        }
        StringBuilder current = new StringBuilder();
        Color color = defaultColor;
        boolean bold = false;
        for (int i = 0; i < input.length(); i++) {
            char chr = input.charAt(i);
            if (chr != SECTION || i + 1 >= input.length()) {
                current.append(chr);
                continue;
            }
            char code = Character.toLowerCase(input.charAt(++i));
            Color parsed = colorOf(code);
            if (parsed == null && code != 'l' && code != 'r') {
                continue;
            }
            if (!current.isEmpty()) {
                runs.add(new Run(current.toString(), color, bold));
                current.setLength(0);
            }
            if (parsed != null) {
                color = parsed;
                bold = false;
            } else if (code == 'l') {
                bold = true;
            } else {
                color = defaultColor;
                bold = false;
            }
        }
        if (!current.isEmpty()) {
            runs.add(new Run(current.toString(), color, bold));
        }
        return runs;
    }

    public static String strip(String input) {
        StringBuilder builder = new StringBuilder();
        for (Run run : parse(input, Color.WHITE)) {
            builder.append(run.text());
        }
        return builder.toString();
    }

    public record Run(String text, Color color, boolean bold) {
    }
}

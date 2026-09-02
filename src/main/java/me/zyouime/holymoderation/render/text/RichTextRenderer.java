package me.zyouime.holymoderation.render.text;

import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.render.text.FormattedText.Run;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class RichTextRenderer {

    private static final float THIN = 0.05f;
    private static final float BOLD = 0.14f;
    private static final String ELLIPSIS = "...";
    private final BuiltText text;

    public RichTextRenderer(float size) {
        this.text = Builder.text()
                .text("")
                .size(size)
                .color(Color.WHITE)
                .thickness(THIN)
                .smoothness(0.5f)
                .font(Fonts.UI.get())
                .build();
    }

    public void setSize(float size) {
        this.text.setSize(size);
    }

    public float getLineHeight() {
        return this.text.getLineHeight();
    }

    private float measure(Run run) {
        this.text.setThickness(run.bold() ? BOLD : THIN);
        return this.text.measureWidth(run.text());
    }

    public List<Run> ellipsize(List<Run> runs, float maxWidth) {
        if (maxWidth <= 0.0f || width(runs) <= maxWidth) {
            return runs;
        }
        List<Run> result = new ArrayList<>();
        float used = 0.0f;
        for (Run run : runs) {
            this.text.setThickness(run.bold() ? BOLD : THIN);
            float ellipsisWidth = this.text.measureWidth(ELLIPSIS);
            float runWidth = this.text.measureWidth(run.text());
            if (used + runWidth <= maxWidth - ellipsisWidth) {
                result.add(run);
                used += runWidth;
                continue;
            }
            String trimmed = trimToWidth(run, maxWidth - used - ellipsisWidth);
            if (!trimmed.isEmpty() || result.isEmpty()) {
                result.add(new Run(trimmed + ELLIPSIS, run.color(), run.bold()));
                break;
            }
            Run last = result.remove(result.size() - 1);
            result.add(new Run(last.text() + ELLIPSIS, last.color(), last.bold()));
            break;
        }
        return result;
    }

    private String trimToWidth(Run run, float available) {
        this.text.setThickness(run.bold() ? BOLD : THIN);
        String source = run.text();
        for (int length = source.length(); length > 0; length--) {
            String candidate = source.substring(0, length);
            if (this.text.measureWidth(candidate) <= available) {
                return candidate;
            }
        }
        return "";
    }

    public float width(List<Run> runs) {
        float total = 0.0f;
        for (Run run : runs) {
            total += this.measure(run);
        }
        return total;
    }

    public void render(Matrix4fStack matrices, List<Run> runs, float x, float y) {
        float cursor = x;
        for (Run run : runs) {
            this.text.setThickness(run.bold() ? BOLD : THIN);
            this.text.setColor(run.color());
            this.text.setText(run.text());
            this.text.render(matrices, cursor, y);
            cursor += this.text.getTextWidth();
        }
    }

    public List<List<Run>> wrap(List<Run> runs, float maxWidth) {
        List<List<Run>> lines = new ArrayList<>();
        List<Run> line = new ArrayList<>();
        float lineWidth = 0.0f;
        for (Run run : runs) {
            StringBuilder pending = new StringBuilder();
            for (String word : splitKeepingSpaces(run.text())) {
                this.text.setThickness(run.bold() ? BOLD : THIN);
                float wordWidth = this.text.measureWidth(word);
                boolean lineEmpty = line.isEmpty() && pending.isEmpty();
                if (!lineEmpty && lineWidth + wordWidth > maxWidth) {
                    if (!pending.isEmpty()) {
                        line.add(new Run(pending.toString(), run.color(), run.bold()));
                        pending.setLength(0);
                    }
                    lines.add(line);
                    line = new ArrayList<>();
                    lineWidth = 0.0f;
                    if (word.isBlank()) {
                        continue;
                    }
                }
                pending.append(word);
                lineWidth += wordWidth;
            }
            if (!pending.isEmpty()) {
                line.add(new Run(pending.toString(), run.color(), run.bold()));
            }
        }
        if (!line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private static List<String> splitKeepingSpaces(String value) {
        List<String> parts = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char chr = value.charAt(i);
            word.append(chr);
            if (chr == ' ') {
                parts.add(word.toString());
                word.setLength(0);
            }
        }
        if (!word.isEmpty()) {
            parts.add(word.toString());
        }
        return parts;
    }
}

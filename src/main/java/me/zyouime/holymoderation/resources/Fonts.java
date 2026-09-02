package me.zyouime.holymoderation.resources;

import com.google.common.base.Suppliers;
import me.zyouime.holymoderation.render.msdf.MsdfFont;

import java.util.function.Supplier;

public final class Fonts {

    public static final Supplier<MsdfFont> UI = getFont("roundbold");
    public static final Supplier<MsdfFont> ICONS = getFont("icons");

    private static Supplier<MsdfFont> getFont(String name) {
        return getFont(name, name);
    }

    private static Supplier<MsdfFont> getFont(String atlasName, String dataName) {
        return Suppliers.memoize(() -> MsdfFont.builder().atlas(atlasName).data(dataName).build());
    }
}

package com.imjustdoom.doomlauncher.justdoomlauncher.util;

import com.imjustdoom.doomlauncher.justdoomlauncher.files.Config;

public class DebugUtil {

    public static void debug(Object obj) {
        if (!Config.Settings.DEBUG) return;

        System.out.println(obj);
    }
}

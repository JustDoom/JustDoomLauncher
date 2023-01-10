package com.imjustdoom.justdoomlauncher.old.util;

import com.imjustdoom.justdoomlauncher.old.files.Config;

public class DebugUtil {

    public static void debug(Object obj) {
        if (!Config.Settings.DEBUG) return;

        System.out.println(obj);
    }
}

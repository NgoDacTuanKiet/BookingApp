package com.bookingapp.utils;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {

    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dwobta54t");
            MediaManager.init(context, config);
            isInitialized = true;
        }
    }

    /**
     * @param type "hotels", "rooms", hoặc "users"
     * @param id   ID của đối tượng tương ứng
     * @param fileName Tên file mong muốn (vd: thumbnail, avatar, hoặc uuid cho room)
     * @return folder path theo cấu trúc yêu cầu
     */
    public static String getPublicId(String type, String id, String fileName) {
        return "booking_app/" + type + "/" + type.substring(0, type.length() - 1) + "_" + id + "/" + fileName;
    }

    public static String getUploadPreset() {
        return "booking_app_preset";
    }
}

package com.f0x1d.logfox.feature.logging.impl.data

/**
 * Well-known Android UIDs mapping.
 * Reference: https://stackoverflow.com/a/28057167
 */
internal val WELL_KNOWN_UIDS: Map<String, Int> = mapOf(
    // Core system
    "root" to 0,
    "system" to 1000,

    // Telephony & connectivity
    "radio" to 1001,
    "bluetooth" to 1002,
    "wifi" to 1010,
    "vpn" to 1016,
    "nfc" to 1027,
    "clat" to 1029,
    "loop_radio" to 1030,

    // Hardware & devices
    "graphics" to 1003,
    "input" to 1004,
    "audio" to 1005,
    "camera" to 1006,
    "compass" to 1008,
    "usb" to 1018,
    "gps" to 1021,

    // System services
    "log" to 1007,
    "mount" to 1009,
    "adb" to 1011,
    "install" to 1012,
    "dhcp" to 1014,
    "keystore" to 1017,
    "mdnsr" to 1020,
    "mtp" to 1024,
    "dbus" to 1038,
    "tlsdate" to 1039,
    "debuggerd" to 1045,
    "firewall" to 1048,
    "trunks" to 1049,
    "nvram" to 1050,

    // Media & DRM
    "media" to 1013,
    "drm" to 1019,
    "drmrpc" to 1026,
    "mediadrm" to 1031,
    "mediaex" to 1040,
    "audioserver" to 1041,
    "mediacodec" to 1046,
    "cameraserver" to 1047,

    // Storage
    "sdcard_rw" to 1015,
    "media_rw" to 1023,
    "sdcard_r" to 1028,
    "sdcard_pics" to 1033,
    "sdcard_av" to 1034,
    "sdcard_all" to 1035,
    "media_audio" to 1055,
    "media_video" to 1056,
    "media_image" to 1057,

    // System utilities
    "logd" to 1036,
    "shared_relro" to 1037,
    "metrics_coll" to 1042,
    "metricsd" to 1043,
    "webserv" to 1044,
    "dns" to 1051,
    "dns_tether" to 1052,
    "webview_zygote" to 1053,
    "vehicle_network" to 1054,
    "package_info" to 1032,

    // Shell & debug
    "shell" to 2000,
    "cache" to 2001,
    "diag" to 2002,

    // Network capabilities (supplemental groups)
    "net_bt_admin" to 3001,
    "net_bt" to 3002,
    "inet" to 3003,
    "net_raw" to 3004,
    "net_admin" to 3005,
    "net_bw_stats" to 3006,
    "net_bw_acct" to 3007,
    "net_bt_stack" to 3008,
    "readproc" to 3009,
    "wakelock" to 3010,

    // Special users
    "everybody" to 9997,
    "misc" to 9998,
    "nobody" to 9999,
)

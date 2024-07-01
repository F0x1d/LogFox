package com.f0x1d.logfox.model

// Thanks to https://stackoverflow.com/a/28057167
val UIDS_MAPPINGS = mapOf(
    0 reverseTo "root", /* traditional unix root user */
    1000 reverseTo "system", /* system server */
    1001 reverseTo "radio", /* telephony subsystem reverseTo RIL */
    1002 reverseTo "bluetooth", /* bluetooth subsystem */
    1003 reverseTo "graphics", /* graphics devices */
    1004 reverseTo "input", /* input devices */
    1005 reverseTo "audio", /* audio devices */
    1006 reverseTo "camera", /* camera devices */
    1007 reverseTo "log", /* log devices */
    1008 reverseTo "compass", /* compass device */
    1009 reverseTo "mount", /* mountd socket */
    1010 reverseTo "wifi", /* wifi subsystem */
    1011 reverseTo "adb", /* android debug bridge adbd, */
    1012 reverseTo "install", /* group for installing packages */
    1013 reverseTo "media", /* mediaserver process */
    1014 reverseTo "dhcp", /* dhcp client */
    1015 reverseTo "sdcard_rw", /* external storage write access */
    1016 reverseTo "vpn", /* vpn system */
    1017 reverseTo "keystore", /* keystore subsystem */
    1018 reverseTo "usb", /* USB devices */
    1019 reverseTo "drm", /* DRM server */
    1020 reverseTo "mdnsr", /* MulticastDNSResponder service discovery, */
    1021 reverseTo "gps", /* GPS daemon */
// 1022 is deprecated and not used.
    1023 reverseTo "media_rw", /* internal media storage write access */
    1024 reverseTo "mtp", /* MTP USB driver access */
// 1025 is deprecated and not used.
    1026 reverseTo "drmrpc", /* group for drm rpc */
    1027 reverseTo "nfc", /* nfc subsystem */
    1028 reverseTo "sdcard_r", /* external storage read access */
    1029 reverseTo "clat", /* clat part of nat464 */
    1030 reverseTo "loop_radio", /* loop radio devices */
    1031 reverseTo "mediadrm", /* MediaDrm plugins */
    1032 reverseTo "package_info", /* access to installed package details */
    1033 reverseTo "sdcard_pics", /* external storage photos access */
    1034 reverseTo "sdcard_av", /* external storage audio/video access */
    1035 reverseTo "sdcard_all", /* access all users external storage */
    1036 reverseTo "logd", /* log daemon */
    1037 reverseTo "shared_relro", /* creator of shared GNU RELRO files */
    1038 reverseTo "dbus", /* dbus-daemon IPC broker process */
    1039 reverseTo "tlsdate", /* tlsdate unprivileged user */
    1040 reverseTo "mediaex", /* mediaextractor process */
    1041 reverseTo "audioserver", /* audioserver process */
    1042 reverseTo "metrics_coll", /* metrics_collector process */
    1043 reverseTo "metricsd", /* metricsd process */
    1044 reverseTo "webserv", /* webservd process */
    1045 reverseTo "debuggerd", /* debuggerd unprivileged user */
    1046 reverseTo "mediacodec", /* mediacodec process */
    1047 reverseTo "cameraserver", /* cameraserver process */
    1048 reverseTo "firewall", /* firewalld process */
    1049 reverseTo "trunks", /* trunksd process TPM daemon, */
    1050 reverseTo "nvram", /* Access-controlled NVRAM */
    1051 reverseTo "dns", /* DNS resolution daemon system: netd, */
    1052 reverseTo "dns_tether", /* DNS resolution daemon tether: dnsmasq, */
    1053 reverseTo "webview_zygote", /* WebView zygote process */
    1054 reverseTo "vehicle_network", /* Vehicle network service */
    1055 reverseTo "media_audio", /* GID for audio files on internal media storage */
    1056 reverseTo "media_video", /* GID for video files on internal media storage */
    1057 reverseTo "media_image", /* GID for image files on internal media storage */

    2000 reverseTo "shell", /* adb and debug shell user */
    2001 reverseTo "cache", /* cache access */
    2002 reverseTo "diag", /* access to diagnostic resources */

    /* The range 2900-2999 is reserved for OEMs */

// The 3000 series are intended for use as supplemental group id's only. They indicate
// special Android capabilities that the kernel is aware of.
    3001 reverseTo "net_bt_admin", /* bluetooth: get any socket */
    3002 reverseTo "net_bt", /* bluetooth: get sco reverseTo rfcomm or l2cap sockets */
    3003 reverseTo "inet", /* can get AF_INET and AF_INET6 sockets */
    3004 reverseTo "net_raw", /* can get raw INET sockets */
    3005 reverseTo "net_admin", /* can configure interfaces and routing tables. */
    3006 reverseTo "net_bw_stats", /* read bandwidth statistics */
    3007 reverseTo "net_bw_acct", /* change bandwidth statistics accounting */
    3008 reverseTo "net_bt_stack", /* bluetooth: access config files */
    3009 reverseTo "readproc", /* Allow /proc read access */
    3010 reverseTo "wakelock", /* Allow system wakelock read/write access */

    /* The range 5000-5999 is also reserved for OEMs. */

    9997 reverseTo "everybody", /* shared between all apps in the same profile */
    9998 reverseTo "misc", /* access to misc storage */
    9999 reverseTo "nobody"
)

private infix fun <A, B> A.reverseTo(that: B): Pair<B, A> = Pair(that, this)

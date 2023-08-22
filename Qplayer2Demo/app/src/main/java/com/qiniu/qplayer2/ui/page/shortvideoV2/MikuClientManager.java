package com.qiniu.qplayer2.ui.page.shortvideoV2;


import android.app.Application;

import com.qiniu.miku.delivery.CacheConfig;
import com.qiniu.miku.delivery.Config;
import com.qiniu.miku.delivery.MikuDeliveryClient;
import com.qiniu.qplayer2.application.QPlayerApplicationContext;

public class MikuClientManager {
    private static final String APP_ID = "44zpao7x7vyw9ncu";
    private static final String APP_SALT = "916c9boaawdlnxlle6k7472asee6h7y8";

    private static final String CACHE_PATH = "";

    private static final int CACHE_SIZE_MB = 100;

    private static final int MAX_WORKERS = 8;

    private static final boolean IS_USE_HTTP_DNS = false;

    private static MikuDeliveryClient sMikuClient;

    public static MikuDeliveryClient getInstance() {
        if (sMikuClient == null) {
            init();
        }
        return sMikuClient;
    }

    public static void uninit() {
        if (sMikuClient != null) {
            sMikuClient.close();
        }
    }

    public static void init() {
        if (sMikuClient != null) {
            sMikuClient.close();
        }
        String cachePath = CACHE_PATH;
        int cacheSizeMb = CACHE_SIZE_MB;
        int maxWorkers = MAX_WORKERS;
        boolean httpDNS = IS_USE_HTTP_DNS;

        Config config = new Config();
        config.workers = maxWorkers;
        config.httpDNS = httpDNS;
        config.cacheConfig = new CacheConfig(cacheSizeMb * 1024 * 1024, cachePath, null);

        sMikuClient = MikuDeliveryClient.create(QPlayerApplicationContext.applicationContext, APP_ID, APP_SALT, config);
        if (sMikuClient == null) {
//            ToastUtils.showShort("MikuDeliveryClient 创建失败！");
        }
    }
}

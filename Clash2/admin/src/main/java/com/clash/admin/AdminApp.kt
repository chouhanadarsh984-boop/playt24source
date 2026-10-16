package com.clash.admin

import android.app.Application
import android.net.Uri
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.map.Mapper
import coil.memory.MemoryCache
import coil.request.Options

class AdminApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        AdminPush.ensureChannel(this)
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(120)
        .components { add(TgImageMapper()) }
        .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.2).build() }
        .diskCache { DiskCache.Builder().directory(cacheDir.resolve("img")).maxSizeBytes(150L * 1024 * 1024).build() }
        .respectCacheHeaders(false)
        .build()
}

/** Lets every AsyncImage(...) accept "tgimg:<id>" and load it from the backend (cached by Coil). */
class TgImageMapper : Mapper<String, Uri> {
    override fun map(data: String, options: Options): Uri? =
        if (data.startsWith("tgimg:")) Uri.parse(BackendConfig.resolve(data)) else null
}

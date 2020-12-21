package ro.unibuc.cs.memeow.injection

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import ro.unibuc.cs.memeow.R

@GlideModule
class GlideAppModule : AppGlideModule() {
    /*override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis().toShort()))
        }
    }*/

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultTransitionOptions(Drawable::class.java, DrawableTransitionOptions.withCrossFade())
        builder.setDefaultRequestOptions {
            RequestOptions()
                .error(R.drawable.ic_baseline_broken_image_24)
        }
    }

    override fun isManifestParsingEnabled() = false
}
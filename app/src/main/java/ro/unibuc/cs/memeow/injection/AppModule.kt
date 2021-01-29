package ro.unibuc.cs.memeow.injection

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.API_KEY
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TAG = "AppModule"

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("ro.unibuc.cs.memeow_preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideRetrofit(sharedPrefs: SharedPreferences): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                var requestBuilder = originalRequest.newBuilder()

                // Executes the block only if the receiver of ?.let is nonNull
                sharedPrefs.getString(API_KEY, null)?.let {
                    requestBuilder = requestBuilder.header("Authorization", it)
                }

                return@addInterceptor chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(MemeowApi.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMemeowApi(retrofit: Retrofit): MemeowApi =
        retrofit.create(MemeowApi::class.java)
}
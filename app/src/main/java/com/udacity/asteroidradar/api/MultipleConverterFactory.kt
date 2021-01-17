package com.udacity.asteroidradar.api


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type

class MultipleConverterFactory: Converter.Factory() {

    companion object{
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        annotations.forEach {
            return when(it.annotationClass){
                UseScalarisConverter::class -> ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
                UseMoshiConverter::class -> MoshiConverterFactory.create(moshi).responseBodyConverter(type, annotations, retrofit)
                else -> null
            }
        }
        return null
    }
}
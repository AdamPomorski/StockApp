package com.plcoding.stockmarketapp.data.remote

import com.plcoding.stockmarketapp.BuildConfig
import com.plcoding.stockmarketapp.data.remote.dto.CompanyInfoDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apiKey: String = BuildConfig.API_KEY_STOCK
    ): ResponseBody


    @GET("query?function=TIME_SERIES_MONTHLY&datatype=csv")
    suspend fun getMonthlyData(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY_STOCK
    ): ResponseBody

    @GET("query?function=OVERVIEW")
    suspend fun getCompanyInfo(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY_STOCK
    ): CompanyInfoDto

    companion object {
        const val BASE_URL = "https://alphavantage.co"
    }

}
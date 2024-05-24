package com.plcoding.stockmarketapp.domain.repository

import androidx.room.Query
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.MonthlyData
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String

    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getMonthlyData(
        symbol: String
    ): Flow<Resource<List<MonthlyData>>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}
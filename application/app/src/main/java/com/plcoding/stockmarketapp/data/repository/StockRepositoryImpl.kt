package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.csv.MonthlyDataParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.*
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.MonthlyData
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val monthlyDataParser: CSVParser<MonthlyData>

) : StockRepository {
    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {

        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }


            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
                emit(
                    Resource.Success(
                        data = dao
                            .searchCompanyListing("")
                            .map { it.toCompanyListing() })
                )
                emit(Resource.Loading(false))
            }

        }
    }

    override suspend fun getMonthlyData(symbol: String): Flow<Resource<List<MonthlyData>>> {
        return flow {
            emit(Resource.Loading(true))
            val companyId: Int? = dao.getCompanyIdFromSymbol(symbol)
            if (companyId == null) {
                emit(Resource.Error(message = "There is no such company on the market"))
                emit(Resource.Loading(false))
                return@flow
            }

            val localMonthlyData = dao.getMonthlyData(companyId)
            emit(Resource.Success(
                data = localMonthlyData.map { it.toMonthlyDataDto().toMonthlyData() }
            ))
            val shouldLoadFromCache = localMonthlyData.isNotEmpty()
            if (shouldLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteMonthlyData = try {
                val response = api.getMonthlyData(symbol)
                monthlyDataParser.parse(response.byteStream())

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load monthly data"))
                null

            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load monthly data"))
                null
            }

            remoteMonthlyData?.let { data ->
                dao.insertMonthlyData(data.map { it.toMonthlyDataDto().toMonthlyDataEntity(companyId) })
                emit(Resource.Success(data))
                emit(Resource.Loading(false))

            }


        }


    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())

        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company info")

        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }


}
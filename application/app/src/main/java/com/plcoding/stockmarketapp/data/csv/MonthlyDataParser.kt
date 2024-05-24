package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toMonthlyData
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyDataDto
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.MonthlyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MonthlyDataParser @Inject constructor(): CSVParser<MonthlyData> {
    override suspend fun parse(stream: InputStream): List<MonthlyData> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO){
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = MonthlyDataDto(timestamp, close.toDouble() )
                    dto.toMonthlyData()

                }.sortedBy { it.date.year }
                .also {
                    csvReader.close()
                }
        }
    }

}

package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.data.local.MonthlyDataEntity
import com.plcoding.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyDataDto
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.MonthlyData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun MonthlyDataDto.toMonthlyData(): MonthlyData{
    val pattern = "yyyy-MM-dd"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDate = LocalDate.parse(timestamp,formatter)

    return MonthlyData(localDate,close)
}

fun MonthlyData.toMonthlyDataDto(): MonthlyDataDto{
    return MonthlyDataDto(timestamp = date.toString(), close = close)
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}

    fun MonthlyDataDto.toMonthlyDataEntity(companyId: Int): MonthlyDataEntity {
        return MonthlyDataEntity(
            timestamp = timestamp,
            closeValue = close,
            companyId = companyId


        )
    }

    fun MonthlyDataEntity.toMonthlyDataDto(): MonthlyDataDto {
        return MonthlyDataDto(
            timestamp = timestamp,
            close = closeValue
        )
}
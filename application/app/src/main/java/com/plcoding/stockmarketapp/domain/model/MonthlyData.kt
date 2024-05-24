package com.plcoding.stockmarketapp.domain.model

import java.time.LocalDate

data class MonthlyData(
    val date: LocalDate,
    val close: Double
)

package com.plcoding.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [CompanyListingEntity::class, MonthlyDataEntity::class],
    version = 2
)
abstract class StockDatabase: RoomDatabase() {
    abstract val dao: StockDao
}
package com.plcoding.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monthly_data",
    foreignKeys = [ForeignKey(
        entity = CompanyListingEntity::class,
        parentColumns = ["id"],
        childColumns = ["companyId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["companyId"])] // Index for faster queries
)
data class MonthlyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int ?= null,
    val companyId: Int,
    val timestamp: String,
    val closeValue: Double
)


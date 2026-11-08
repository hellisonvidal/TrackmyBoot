package com.trackmyboot.app.data.db.converter

import androidx.room.TypeConverter
import com.trackmyboot.app.domain.model.ProductCategory

class ProductCategoryConverter {
    @TypeConverter
    fun fromString(value: String?): ProductCategory? {
        return value?.let { ProductCategory.valueOf(it) }
    }

    @TypeConverter
    fun toString(category: ProductCategory?): String? {
        return category?.name
    }
}
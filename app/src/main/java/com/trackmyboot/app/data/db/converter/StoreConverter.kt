package com.trackmyboot.app.data.db.converter

import androidx.room.TypeConverter
import com.trackmyboot.app.domain.model.Store

class StoreConverter {
    @TypeConverter
    fun fromString(value: String?): Store? {
        return value?.let { Store.valueOf(it) }
    }

    @TypeConverter
    fun toString(store: Store?): String? {
        return store?.name
    }
}
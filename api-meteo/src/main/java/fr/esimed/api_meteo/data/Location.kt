package fr.esimed.api_meteo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class Location(var title: String = "", var woeid: Int = 0): Serializable
{
    override fun toString(): String
    {
        return String.format(title, "$woeid")
    }
}
package com.chatchatabc.parkingclient.android.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.chatchatabc.parking.model.Route
import com.chatchatabc.parking.model.response.RouteNodesAndEdges
import com.chatchatabc.parking.model.route.RouteEdge
import com.chatchatabc.parking.model.route.RouteNode
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Entity
data class RouteDBObject(
    @PrimaryKey
    var routeUuid: String,
    var name: String?,
    var description: String?,
    var status: Int,
    @TypeConverters(LocalDateTimeTypeConverter::class)
    var createdAt: LocalDateTime,
    @TypeConverters(LocalDateTimeTypeConverter::class)
    var updatedAt: LocalDateTime?,
    var slug: String
)

@ProvidedTypeConverter
object LocalDateTimeTypeConverter {
    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime = LocalDateTime.parse(value)

    @TypeConverter
    fun fromISOString(value: LocalDateTime): String = value.toString()
}

val RouteDBObject.route: Route
    get() = Route(
        routeUuid = routeUuid,
        name = name,
        description = description,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        slug = slug
    )

val Route.dbObject: RouteDBObject
    get() = RouteDBObject(
        routeUuid = routeUuid,
        name = name,
        description = description,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        slug = slug
    )

@Serializable
@Entity
data class RouteNodesAndEdgesDBObject(
    @PrimaryKey
    val routeUuid: String,
    @TypeConverters
    val nodes: List<RouteNode>,
    @TypeConverters
    val edges: List<RouteEdge>
)

val RouteNodesAndEdgesDBObject.routeNodeAndEdges: RouteNodesAndEdges
    get() = RouteNodesAndEdges(
        routeUuid = routeUuid,
        nodes = nodes,
        edges = edges
    )

val RouteNodesAndEdges.dbObject: RouteNodesAndEdgesDBObject
    get() = RouteNodesAndEdgesDBObject(
        routeUuid = routeUuid,
        nodes = nodes,
        edges = edges
    )

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromRouteNodeStrings(value: String): List<RouteNode> = Json.decodeFromString(value)

    @TypeConverter
    fun toRouteNodeStrings(value: List<RouteNode>): String = Json.encodeToString(value)

    @TypeConverter
    fun fromRouteEdgeStrings(value: String): List<RouteEdge> = Json.decodeFromString(value)

    @TypeConverter
    fun toRouteEdgeStrings(value: List<RouteEdge>): String = Json.encodeToString(value)
}
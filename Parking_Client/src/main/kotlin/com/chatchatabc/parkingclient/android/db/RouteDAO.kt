package com.chatchatabc.parkingclient.android.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.chatchatabc.parkingclient.android.db.entity.RouteDBObject
import com.chatchatabc.parkingclient.android.db.entity.RouteNodesAndEdgesDBObject

@Dao
interface RouteDAO {
    @Query("SELECT * FROM routedbobject")
    fun getAllRoutes(): List<RouteDBObject>

    @Query("SELECT * FROM routedbobject WHERE routeUuid = :routeUuid")
    fun getRouteById(routeUuid: String): RouteDBObject

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoute(route: RouteDBObject)

    @Transaction
    fun addAll(routes: List<RouteDBObject>) {
        routes.forEach { insertRoute(it) }
    }

    @Delete
    fun deleteRoute(route: RouteNodesAndEdgesDBObject)

    @Query("SELECT * FROM RouteNodesAndEdgesDBObject WHERE routeUuid = :routeUuid")
    fun getRouteNodesAndEdgesByRouteId(routeUuid: String): RouteNodesAndEdgesDBObject

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRouteNodesAndEdges(routeNodesAndEdges: RouteNodesAndEdgesDBObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoutes(map: List<RouteDBObject>)
}
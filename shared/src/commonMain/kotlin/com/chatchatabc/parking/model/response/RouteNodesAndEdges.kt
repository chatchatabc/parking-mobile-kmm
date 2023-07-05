package com.chatchatabc.parking.model.response

import com.chatchatabc.parking.model.route.RouteEdge
import com.chatchatabc.parking.model.route.RouteNode
import kotlinx.serialization.Serializable

@Serializable
data class RouteNodesAndEdges(
    val routeUuid: String,
    val nodes: List<RouteNode>,
    val edges: List<RouteEdge>
)

fun RouteNodesAndEdges.buildPath(): List<RouteNode> {
    val map: MutableMap<Long, MutableList<RouteEdge>> = mutableMapOf()
    val nodes: MutableList<Long> = mutableListOf()

    // Group the edges by their starting point
    for (edge in this.edges) {
        val start = edge.nodeFrom
        if (map[start] == null) {
            map[start] = mutableListOf()
        }
        map[start]!!.add(edge)
    }

    val startNode = map.keys.first() // Save the starting point
    var current = startNode
    nodes.add(current)

    while (map.isNotEmpty()) {
        // Take the first edge that starts from the current point and remove it from the map
        val edge = map[current]!!.removeAt(0)
        if (map[current]!!.isEmpty()) {
            map.remove(current)
        }

        // Move to the next point
        current = edge.nodeTo
        nodes.add(current)
    }

    // Check if we need to add an edge back to the start
    if (nodes.last() != startNode) {
        nodes.add(startNode)
    }

    return nodes.map { id ->
        this.nodes.first { it.id == id }
    }.also {
        println("Path: $it")
    }
}
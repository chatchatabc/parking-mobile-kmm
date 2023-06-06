package com.chatchatabc.parking.service

import com.chatchatabc.parking.Config.NATS_BASE_URL
import io.nats.client.Connection
import io.nats.client.ConnectionListener
import io.nats.client.Consumer
import io.nats.client.ErrorListener
import io.nats.client.Message
import io.nats.client.Options
import io.nats.client.impl.NatsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NatsService() {
//    val connection = Nats.connect(NATS_BASE_URL)

    var notificationId: String = "notificationId"
    var onMessageRecieved: (Message) -> Unit = { println(it) }
    var started = false

    lateinit var connection: Connection

    fun init(onError: (Exception) -> Unit = {}, callback: NatsService.() -> Unit) {
        Options.Builder()
            .server(NATS_BASE_URL)
            .connectionListener { _, events ->
                when (events) {
                    ConnectionListener.Events.DISCONNECTED -> {
                        println("Disconnected from NATS server")
                    }

                    ConnectionListener.Events.RECONNECTED -> {
                        println("Reconnected to NATS server")
                    }

                    ConnectionListener.Events.CLOSED -> {
                        println("Closed connection to NATS server")
                    }

                    ConnectionListener.Events.CONNECTED -> {
                        println("Connected to NATS server")
                    }

                    else -> {
                        println("Unknown event: $events")
                    }
                }
            }
            .errorListener(object : ErrorListener {
                override fun errorOccurred(conn: Connection?, error: String?) {
                    println("The server notificed the client with: $error")
                }

                override fun exceptionOccurred(conn: Connection?, exp: Exception?) {
                    println("The connection handled an exception: ${exp!!.localizedMessage}")
                }

                override fun slowConsumerDetected(conn: Connection?, consumer: Consumer?) {
                    println("The server detected a slow consumer: $consumer")
                }
            }
            )
            .build().let {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        connection = NatsImpl.createConnection(it, true)
                        callback(this@NatsService)
                    }
                } catch (exception: Exception) {
                    onError(exception)
                }
            }
    }

    fun listen() {
        if (started) {
            return
        }

        connection.createDispatcher(onMessageRecieved).subscribe(notificationId)
        started = true
    }
}

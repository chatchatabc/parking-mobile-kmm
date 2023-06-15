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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class NatsService() {
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTED
    }

    var notificationId: String = "notificationId"
    var onMessageRecieved: (Message) -> Unit = { println(it) }
    var state = MutableStateFlow(ConnectionState.DISCONNECTED)

    lateinit var connection: Connection

    fun init(onError: (Exception) -> Unit = {}, callback: NatsService.() -> Unit) {
        Options.Builder()
            .server(NATS_BASE_URL)
            .connectionListener { _, events ->
                when (events) {
                    ConnectionListener.Events.DISCONNECTED -> {
                        println("Disconnected from NATS server")
                        state.value = ConnectionState.DISCONNECTED
                    }

                    ConnectionListener.Events.RECONNECTED -> {
                        println("Reconnected to NATS server")
                        state.value = ConnectionState.CONNECTED
                    }

                    ConnectionListener.Events.CLOSED -> {
                        println("Closed connection to NATS server")
                        state.value = ConnectionState.DISCONNECTED
                    }

                    ConnectionListener.Events.CONNECTED -> {
                        println("Connected to NATS server")
                        state.value = ConnectionState.CONNECTED
                    }
                    else -> {
                        println("Unknown event: $events")
                    }
                }
            }
            .errorListener(object : ErrorListener {
                override fun errorOccurred(conn: Connection?, error: String?) {
                    println("The server notified the client with: $error")
                    state.value = ConnectionState.DISCONNECTED
                }

                override fun exceptionOccurred(conn: Connection?, exp: Exception?) {
                    println("The connection handled an exception: ${exp!!.localizedMessage}")
                    state.value = ConnectionState.DISCONNECTED
                }

                override fun slowConsumerDetected(conn: Connection?, consumer: Consumer?) {
                    println("The server detected a slow consumer: $consumer")
                    state.value = ConnectionState.DISCONNECTED
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

    fun listen(notifId: String) {
        if (state.value == ConnectionState.CONNECTED) {
            return
        }

        notificationId = notifId

        connection.createDispatcher(onMessageRecieved).subscribe(notificationId)
        state.value = ConnectionState.CONNECTED
    }
}

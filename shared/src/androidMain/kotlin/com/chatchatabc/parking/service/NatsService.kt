package com.chatchatabc.parking.service


import com.chatchatabc.parking.Config.NATS_BASE_URL
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message
import io.nats.client.Nats
import io.nats.client.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class NatsService() {
    private var connection: Connection? = null
    private val subscriptions = ConcurrentHashMap<String, Pair<String, Dispatcher>>()
    private val mutex = Mutex()

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            return@withLock try {
                val options = Options.Builder()
                    .server(NATS_BASE_URL)
                    .build()

                connection = Nats.connect(options)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun subscribeToSubject(subject: String, onMessageReceived: (Message) -> Unit) {
        mutex.withLock {
            println("Subscribing to $subject")
            val dispatcher = connection?.createDispatcher(onMessageReceived)
            dispatcher?.let {
                println("Dispatcher not null, contnuing to subscribe to $subject")
                it.subscribe(subject)
                subscriptions[subject] = Pair(subject, it)
            }
        }
    }


    suspend fun unsubscribeFromSubject(subject: String) {
        mutex.withLock {
            println("Unsubbing to $subject")
            subscriptions.remove(subject)?.let {
                it.second.unsubscribe(it.first)
            }
        }
    }

    suspend fun shutdown() = withContext(Dispatchers.IO) {
        mutex.withLock {
            subscriptions.values.forEach {
                it.second.unsubscribe(it.first)
            }
            subscriptions.clear()
            connection?.close()
            connection = null
        }
    }
}


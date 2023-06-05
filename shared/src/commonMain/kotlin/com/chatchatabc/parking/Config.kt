package com.chatchatabc.parking

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface Platform {
    val name: String
}

expect fun getPlatform(): com.chatchatabc.parking.Platform


expect fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient

object Config {
    const val BASE_URL = "http://192.168.1.47:5080"
    const val OSS_URL = "https://davao-parking.oss-ap-southeast-6.aliyuncs.com/"
}
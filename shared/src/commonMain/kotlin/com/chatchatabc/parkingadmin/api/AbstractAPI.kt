package com.chatchatabc.parkingadmin.api

import com.chatchatabc.parkingadmin.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

// TODO: Reduce code duplication
// TODO: Maybe think of a better implementation for this one.
// TODO: Better error handling
abstract class AbstractAPI(val httpClient: HttpClient, val token: String? = null) {

    companion object {
        // TODO: Make it configurable inside a config file.
        val BASE_URL = "http://192.168.1.14:5080"
    }

    private var endpoint: String = ""

    var ENDPOINT: String
        get() = BASE_URL + endpoint
        set(value) {
            endpoint = value
        }

    fun <T> defaultUnknownError(): ApiResponse<T> {
        return ApiResponse(
            error = true,
            message = "Something went wrong. Please try again.",
            code = 500
        )
    }

    suspend inline fun <reified T, reified U> apiPost(url: String, body: U): ApiResponse<T> {
        return try {
            httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
                token?.let {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
            }.body()
        } catch (e: Exception) {
            defaultUnknownError()
        }
    }

    suspend inline fun <reified T> apiUpload(url: String, body: ByteArray): ApiResponse<T> {
        return try {
            httpClient.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    append("file", body, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpg")
                        append(HttpHeaders.ContentDisposition, "filename=\"upload.jpg\"")
                    })
                }
            ) {
                token?.let {
                    headers {
                        println("Token: $token")
                        append("Authorization", "Bearer $token")
                    }
                }
            }.body()
        } catch (e: Exception) {
            defaultUnknownError()
        }
    }
    suspend inline fun <reified T, reified U> apiPut(url: String, body: U): ApiResponse<T> {
        return try {
            httpClient.put(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
                token?.let {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
            }.body()
        } catch (e: Exception) {
            defaultUnknownError()
        }
    }
    suspend inline fun <reified T> apiPut(url: String): ApiResponse<T> {
        return try {
            httpClient.put(url) {
                contentType(ContentType.Application.Json)
                token?.let {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
            }.body()
        } catch (e: Exception) {
            defaultUnknownError()
        }
    }

    suspend inline fun <reified U, reified T> apiGet(url: String, body: U): ApiResponse<T> {
            return httpClient.get(url) {
                contentType(ContentType.Application.Json)
                token?.let {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
                body?.let {
                    setBody(body)
                }
            }.body()
    }

    suspend inline fun <reified T> apiGet(url: String): ApiResponse<T> {
        return try {
            httpClient.get(url) {
                contentType(ContentType.Application.Json)
                token?.let {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
            }.body()
        } catch (e: Exception) {
            defaultUnknownError()
        }
    }
}
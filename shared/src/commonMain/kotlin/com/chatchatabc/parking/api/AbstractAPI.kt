package com.chatchatabc.parking.api

import com.chatchatabc.parking.Config.BASE_URL
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import com.chatchatabc.parking.model.response.ErrorElement
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

// TODO: Reduce code duplication
// TODO: Maybe think of a better implementation for this one.
// TODO: Better error handling
abstract class AbstractAPI(val httpClient: HttpClient) {
    private var token: String = ""

    fun setToken(token: String) {
        this.token = token
    }

    suspend inline fun <reified T> makeRequest(
        method: HttpMethod,
        url: String,
        body: Any? = null,
        headers: Headers? = null,
        pagination: Pagination? = null
    ): ApiResponse<T> {
        return runCatching {
            return httpClient.request(BASE_URL + url) {
                this.method = method
                this.contentType(ContentType.Application.Json)
                pagination?.let { this.parameter("pageable", it) }
                body?.let { setBody(it) }
                headers?.let { this.headers.appendAll(it) }
                setAuthorizationHeader()
            }.body()
        }.getOrElse {
            it.printStackTrace()
            defaultUnknownError(it.message)
        }
    }

    suspend inline fun <reified T> makeSimpleRequest(
        method: HttpMethod,
        url: String,
        headers: Headers? = null
    ): T? = runCatching {
            return httpClient.request(BASE_URL + url) {
                this.method = method
                this.contentType(ContentType.Application.Json)
                headers?.let { this.headers.appendAll(it) }
                setAuthorizationHeader()
            }.body()
        }.getOrElse {
            it.printStackTrace()
            null
        }

    suspend inline fun <reified T> makeUploadRequest(
        url: String,
        data: ByteArray,
        headers: Headers? = null,
        crossinline onUploadUpdate: (bytesSentTotal: Long, contentLength: Long) -> Unit
    ): ApiResponse<T> {
        return runCatching {
            return httpClient.submitFormWithBinaryData(
                url = BASE_URL + url,
                formData = formData {
                    append("file", data, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpg")
                        append(HttpHeaders.ContentDisposition, "filename=\"upload.jpg\"")
                    })
                },
            ) {
                onUpload { bytesSentTotal, contentLength ->
                    onUploadUpdate(bytesSentTotal, contentLength)
                }
                headers?.let { this.headers.appendAll(it) }
                contentType(ContentType.Application.Json)
                setAuthorizationHeader()
            }.body()
        }.getOrElse {
            it.printStackTrace()
            defaultUnknownError(it.message)
        }
    }

    fun HttpRequestBuilder.setAuthorizationHeader() {
        token.let { headers.append("Authorization", "Bearer $token") }
    }

    fun <T> defaultUnknownError(message: String? = null): ApiResponse<T> {
        return ApiResponse(
            data = null,
            errors = listOf(ErrorElement("ERROR", "Something went wrong. Please try again.")),
        )
    }
}
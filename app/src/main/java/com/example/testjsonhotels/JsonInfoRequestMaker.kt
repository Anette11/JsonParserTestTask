package com.example.testjsonhotels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class JsonInfoRequestMaker(private val context: Context) {

    fun makeRequestText(request: String): String {
        val url = URL(request)
        var bufferedInputStream: BufferedInputStream? = null
        val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        var text = context.resources.getString(R.string.empty)

        try {
            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                bufferedInputStream = BufferedInputStream(httpURLConnection.inputStream)
                text = bufferedInputStream.bufferedReader().use { it.readText() }
            } else {
                text = context.getString(R.string.not_found)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bufferedInputStream?.close()
            httpURLConnection.disconnect()
        }
        return text
    }

    fun makeRequestImage(request: String): Bitmap? {
        val url = URL(request)
        var bufferedInputStream: BufferedInputStream? = null
        val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        var bitmap: Bitmap? = null

        try {
            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                bufferedInputStream = BufferedInputStream(httpURLConnection.inputStream)
                bitmap = BitmapFactory.decodeStream(bufferedInputStream)
            } else {
                bitmap = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bufferedInputStream?.close()
            httpURLConnection.disconnect()
        }
        return bitmap
    }
}

package BDLayer

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class GoogleDriveRestBackup (private val accessToken: String){
    private val httpClient = OkHttpClient()

    fun backupDatabaseToDrive(backupFile: File): Boolean {
        val fileBody = backupFile.asRequestBody("application/x-sqlite3".toMediaType())
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", backupFile.name, fileBody)
            .build()

        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        return response.isSuccessful
    }
}
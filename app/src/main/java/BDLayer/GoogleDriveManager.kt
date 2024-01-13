package BDLayer

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveResourceClient
import com.google.android.gms.drive.MetadataChangeSet
import java.io.File
import java.io.FileInputStream


class GoogleDriveManager(private val context: Context) {

    private val driveResourceClient: DriveResourceClient

    init {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, signInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(context)

        driveResourceClient = Drive.getDriveResourceClient(context, account!!)
    }

    fun backupAndUploadDatabaseToDrive(databasePath: String) {
        try {
            val backupFile = File(databasePath)

            val metadataChangeSet = MetadataChangeSet.Builder()
                .setTitle("BackupDatabase.sqlite")
                .setMimeType("application/x-sqlite3")
                .build()

            driveResourceClient.createContents()
                .addOnSuccessListener { driveContents ->
                    val outputStream = driveContents.outputStream
                    FileInputStream(backupFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }

                    driveResourceClient.rootFolder.addOnSuccessListener { rootFolder ->
                        driveResourceClient.createFile(rootFolder, metadataChangeSet, driveContents)
                            .addOnSuccessListener {
                                // Successfully uploaded the backup to Google Drive
                                showToast("Archivo subido exitosamente a Google Drive")
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
            // Handle exception
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
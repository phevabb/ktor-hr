package com.hr.account.importer

import com.hr.config.DatabaseFactory
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime

fun main() = runBlocking {

    val projectDirectory =
        File(System.getProperty("user.dir"))

    val logFile =
        File(
            projectDirectory,
            "account_import_log.txt"
        )

    fun writeLog(message: String) {
        println(message)

        logFile.appendText(
            "$message\n"
        )
    }

    /*
     * Clear the previous log before starting.
     */
    logFile.writeText(
        "ACCOUNT IMPORT LOG\n" +
                "Started: ${LocalDateTime.now()}\n" +
                "========================================\n"
    )

    writeLog("")
    writeLog("========================================")
    writeLog("STAFF EXCEL IMPORT STARTING")
    writeLog("========================================")

    try {

        writeLog(
            "Working directory: " +
                    projectDirectory.absolutePath
        )

        /*
         * Change only the filename below if your physical
         * Excel filename is different.
         */
        val excelFile = File(
            "C:\\Users\\pheva\\Desktop\\KOTLIN_android\\acode\\ktor-hr\\src\\main\\kotlin\\account\\importer\\staff_data.xlsx"
        )

        writeLog(
            "Excel file path: " +
                    excelFile.absolutePath
        )

        writeLog(
            "Excel file exists: " +
                    excelFile.exists()
        )

        writeLog(
            "Excel path is a file: " +
                    excelFile.isFile
        )

        if (!excelFile.exists()) {
            throw IllegalArgumentException(
                "Excel file was not found at: " +
                        excelFile.absolutePath
            )
        }

        if (!excelFile.isFile) {
            throw IllegalArgumentException(
                "The Excel path is not a file: " +
                        excelFile.absolutePath
            )
        }

        if (
            !excelFile.extension.equals(
                other = "xlsx",
                ignoreCase = true
            )
        ) {
            throw IllegalArgumentException(
                "The import file must be an XLSX file. " +
                        "Found: ${excelFile.name}"
            )
        }

        writeLog("Initializing database connection...")

        DatabaseFactory.init()

        writeLog("Database connection initialized.")
        writeLog("Starting Excel account import...")

        val result =
            ExcelAccountImporter.importExcel(
                filePath = excelFile.absolutePath
            )

        writeLog("")
        writeLog("========================================")
        writeLog("STAFF EXCEL IMPORT COMPLETED")
        writeLog("========================================")

        writeLog(
            "Total data rows: ${result.totalRows}"
        )

        writeLog(
            "Imported rows: ${result.importedRows}"
        )

        writeLog(
            "Skipped rows: ${result.skippedRows}"
        )

        writeLog(
            "Import errors: ${result.errors.size}"
        )

        if (result.errors.isNotEmpty()) {

            writeLog("")
            writeLog("ROW IMPORT ERRORS")
            writeLog("----------------------------------------")

            result.errors.forEachIndexed {
                    index,
                    errorMessage ->

                writeLog(
                    "${index + 1}. $errorMessage"
                )
            }

            writeLog("----------------------------------------")
        }

        writeLog(
            "Completed: ${LocalDateTime.now()}"
        )

        writeLog("========================================")

    } catch (exception: Exception) {

        val stringWriter =
            StringWriter()

        exception.printStackTrace(
            PrintWriter(stringWriter)
        )

        val completeStackTrace =
            stringWriter.toString()

        writeLog("")
        writeLog("========================================")
        writeLog("STAFF EXCEL IMPORT FAILED")
        writeLog("========================================")

        writeLog(
            "Error type: " +
                    (
                            exception::class.qualifiedName
                                ?: "Unknown exception"
                            )
        )

        writeLog(
            "Error message: " +
                    (
                            exception.message
                                ?: "No error message was provided"
                            )
        )

        writeLog("")
        writeLog("COMPLETE STACK TRACE")
        writeLog("----------------------------------------")
        writeLog(completeStackTrace)
        writeLog("----------------------------------------")

        writeLog(
            "Failed: ${LocalDateTime.now()}"
        )

        writeLog("========================================")

        /*
         * Do not rethrow here.
         *
         * The complete exception has already been saved
         * to account_import_log.txt. Not rethrowing prevents
         * IntelliJ from displaying only the generic failed
         * Gradle task message again.
         */
    }

    println()
    println(
        "Import report saved to: " +
                logFile.absolutePath
    )
}
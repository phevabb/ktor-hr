package com.hr.account.importer

data class ImportResult(
    val totalRows: Int,
    val importedRows: Int,
    val skippedRows: Int,
    val errors: List<String>
)
package DocumentsPDF

class ShowTableStudentSituations {

  /*  fun showStudentsSituation(color: BaseColor, cursor: Cursor, beginDate: String, endDate: String, up: Float, right: Float, writer: PdfWriter): Float {
        val fontBold = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD, BaseColor.WHITE)
        val fontSmallM = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.MAGENTA))
        val fontSmallH = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))

        val table = createPdfPTable(color, fontBold)
        fillTableWithData(table, cursor, fontSmallM, fontSmallH, beginDate)

        putPosition(up, right, writer, table)
        table.calculateHeights()

        return table.totalHeight
    }

    private fun createPdfPTable(color: BaseColor, fontBold: Font): PdfPTable {
        val table = PdfPTable(2)
        val columnWidths = floatArrayOf(17f, 400f)
        table.setTotalWidth(columnWidths)
        table.isLockedWidth = true

        val cellLista = createPdfPCell("N_lista", color, fontBold, 90f, 15f)
        val nombreCell = createPdfPCell("ALUMNO", color, fontBold)
        table.addCell(cellLista)
        table.addCell(nombreCell)

        return table
    }

    private fun createPdfPCell(content: String, backgroundColor: BaseColor, font: Font, rotation: Float = 0f, fixedHeight: Float = 0f): PdfPCell {
        val cell = PdfPCell(Phrase(content, font))
        cell.backgroundColor = backgroundColor
        cell.rotation = rotation
        cell.fixedHeight = fixedHeight
        return cell
    }

    private fun fillTableWithData(table: PdfPTable, cursor: Cursor, fontSmallM: Font, fontSmallH: Font, beginDate: String) {
        var N_lista = 0

        if (cursor.moveToFirst()) {
            do {
                val sexo = if (cursor.getInt(6) == 0) "M" else "H"
                val font = if (cursor.getInt(6) == 0) fontSmallM else fontSmallH
                N_lista++

                val cellN_lista = createPdfPCell(N_lista.toString(), BaseColor.WHITE, font, fixedHeight = 15f)
                val cellNombre = createPdfPCell("${cursor.getString(1)} ${cursor.getString(2)} ${cursor.getString(3)}", BaseColor.WHITE, font, fixedHeight = 15f)
                val cellCurp = createPdfPCell(cursor.getString(4), BaseColor.WHITE, font)
                val cellRegistro = createPdfPCell(cursor.getString(5), BaseColor.WHITE, font)
                val cellSexo = createPdfPCell(sexo, BaseColor.WHITE, font)
                val cellEdad = createPdfPCell(cursor.getString(7), BaseColor.WHITE, font)

                val cellSituacion = if (cursor.getString(8) == "ALTA") {
                    createPdfPCell(cursor.getString(8), BaseColor.YELLOW, font)
                } else {
                    createPdfPCell(cursor.getString(8), BaseColor.WHITE, font)
                }

                val cellBaja = if (compareDates(cursor.getString(9), beginDate) > 0) {
                    createPdfPCell("BAJA : ${cursor.getString(9)}", BaseColor.BLACK, font)
                } else {
                    createPdfPCell(" ", BaseColor.WHITE, font)
                }

                table.addCell(cellN_lista)
                table.addCell(cellNombre)
                table.addCell(cellCurp)
                table.addCell(cellRegistro)
                table.addCell(cellSexo)
                table.addCell(cellEdad)
                table.addCell(cellSituacion)
                table.addCell(cellBaja)
            } while (cursor.moveToNext())
        }
    }*/
}
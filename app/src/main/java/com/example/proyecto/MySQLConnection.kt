package com.example.proyecto

import android.content.Context
import android.os.AsyncTask
import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MySQLConnection(private val context: Context) {
    private val url = context.getString(R.string.db_url)
    private val user = context.getString(R.string.db_user)
    private val password = context.getString(R.string.db_password)

    private fun getConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }

    // Tarea AsyncTask para operaciones de inserción
    inner class InsertDataTask(
        private val query: String,
        private val params: Array<out String>,
        private val callback: (Boolean) -> Unit
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg voids: Void?): Boolean {
            return try {
                val connection = getConnection()
                val preparedStatement = connection.prepareStatement(query)
                for ((index, param) in params.withIndex()) {
                    preparedStatement.setString(index + 1, param)
                }
                preparedStatement.executeUpdate()
                connection.close()
                true
            } catch (e: SQLException) {
                e.printStackTrace()
                false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            // Llamada al callback para devolver el resultado a la actividad o fragmento
            callback(result)
        }
    }

    // Tarea AsyncTask para operaciones de selección
    inner class SelectDataTask(
        private val query: String,
        private val params: Array<out String>,
        private val callback: (List<Map<String, String>>) -> Unit
    ) : AsyncTask<Void, Void, List<Map<String, String>>>() {

        override fun doInBackground(vararg voids: Void?): List<Map<String, String>> {
            val result = mutableListOf<Map<String, String>>()
            try {
                val connection = getConnection()
                val preparedStatement = connection.prepareStatement(query)
                for ((index, param) in params.withIndex()) {
                    preparedStatement.setString(index + 1, param)
                }
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    val row = mutableMapOf<String, String>()
                    for (i in 1..resultSet.metaData.columnCount) {
                        row[resultSet.metaData.getColumnName(i)] = resultSet.getString(i)
                    }
                    result.add(row)
                }
                resultSet.close()
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<Map<String, String>>) {
            super.onPostExecute(result)
            // Llamada al callback para devolver el resultado a la actividad o fragmento
            callback(result)
        }
    }

    // Método para iniciar la tarea de inserción
    fun insertDataAsync(query: String, vararg params: String, callback: (Boolean) -> Unit) {
        InsertDataTask(query, params, callback).execute()
    }

    // Método para iniciar la tarea de selección
    fun selectDataAsync(query: String, vararg params: String, callback: (List<Map<String, String>>) -> Unit) {
        SelectDataTask(query, params, callback).execute()
    }
}

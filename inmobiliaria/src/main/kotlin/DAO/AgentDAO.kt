package main.kotlin.DAO

import DTO.Agent
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.sql.SQLException

sealed class AgentResult (val message: String) {
    class Success: AgentResult("La operación se realizó correctamente")
    class Failure: AgentResult("La operación no se pudo realizar")
    class FoundList(val agents: List<Agent>): AgentResult("Se encontraron multiples agentes")
    class Found(val agent: Agent): AgentResult("Se encontró el Agente buscado")
    class NotFound: AgentResult("El agente a buscar no existe")
    class DBError(private val errorMessage: String): AgentResult(errorMessage)
    class WrongAgent: AgentResult("Los datos del agente son incorrectos")
}

class AgentDAO {
    private val dbConnection = DataBaseConnection().connection

     fun add (agent: Agent): AgentResult {
         if (!agent.isValid()) {
             return AgentResult.WrongAgent()
         }

         return try {
             val query = dbConnection.prepareStatement("INSERT INTO agent (accountId, personelNumber) VALUES (?, ?);")

             query.setInt(1, agent.accountId.toInt())
             query.setString(2, agent.personelNumber)


             if (query.executeUpdate() > 0) {
                 AgentResult.Success()
             }
             else {
                 AgentResult.Failure()
             }
         }
         catch (error: SQLException) {
             AgentResult.DBError(error.message.toString())
         }
     }

     fun getById (agentId: UInt): AgentResult {
         val query = "SELECT accountId, personelNumber FROM agent WHERE id=${agentId};"
         val result = DataFrame.readSqlQuery(dbConnection, query)

         return if (result.isEmpty()) {
             AgentResult.NotFound()
         } else {
             AgentResult.Found(Agent.fromDataRow(result.first()))
         }
     }

     fun getAll (): AgentResult {
         val result = DataFrame.readSqlTable(dbConnection, "agent")

         return AgentResult.FoundList(Agent.fromDataFrame(result))
     }
}

package main.kotlin.DAO

import DTO.Agent

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
    // fun add (agent: Agent): AgentResult {}
    // fun modify (agent: Agent): AgentResult {}
    // fun getById (agentId: UInt): AgentResult {}
    // fun getAll (): AgentResult {}
    // fun delete (agentId: UInt): AgentResult {}
}

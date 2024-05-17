package DAO

import DTO.Property
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import java.sql.SQLException

sealed class PropertyResult (val message: String) {
    class Success: PropertyResult("La operación se realizó correctamente")
    class Failure: PropertyResult("La operación no se pudo realizar")
    class FoundList(val properties: List<Property>): PropertyResult("Se encontraron multiples propiedades")
    class Found(val property: Property): PropertyResult("Se encontró la propiedad")
    class NotFound: PropertyResult("La propiedad a buscar no existe")
    class DBError(private val errorMessage: String): PropertyResult(errorMessage)
    class WrongProperty: PropertyResult("Los datos de la propiedad son incorrectos")
}

class PropertyDAO {
    private val dbConnection = DataBaseConnection().connection

    fun add (property: Property): PropertyResult {
        if (!property.isValid()) {
            return PropertyResult.WrongProperty()
        }

        return try {
            val query = dbConnection.prepareStatement("INSERT INTO property (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")

            query.setString(1, property.title)
            query.setString(2, property.shortDescription)
            query.setString(3, property.fullDescription)
            query.setString(4, property.type.toString())
            query.setFloat(5, property.price)
            query.setString(6, property.state.toString())
            query.setString(7, property.direction)
            query.setInt(8, property.houseOwner.toInt())
            query.setString(9, property.action.toString())

            if (query.executeUpdate() > 0) {
                PropertyResult.Success()
            } else {
                PropertyResult.Failure()
            }
        }
        catch (error: SQLException) {
             PropertyResult.DBError(error.message.toString())
        }
    }

    fun modify (property: Property): PropertyResult {
        // validar datos

        // ejecutar consulta a base de datos
        var result: Int = -1

        try {

        }
        catch (error: SQLException) {

        }

        if (result > 0) {
            return PropertyResult.Success()
        }
        else {
            return PropertyResult.Failure()
        }
    }

    fun getById (propertyId: Int): PropertyResult {
        if (propertyId < 1) {
            return PropertyResult.NotFound()
        }

        val query = "SELECT * FROM property WHERE id=${propertyId}"
        val result = DataFrame.readSqlQuery(dbConnection, query)

        return PropertyResult.Found(Property.fromDataRow(result.first()))
    }

    //fun getByHouseOwner (houseOwnerId: Int): PropertyResult {}

    //fun getAll (): PropertyResult {}

    //fun remove (propertyId: Int): PropertyResult {}
}
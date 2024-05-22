package DAO

import DTO.Property
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.awt.Image
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO
import java.sql.SQLException

sealed class PropertyResult (val message: String) {
    class Success: PropertyResult("La operación se realizó correctamente")
    class Failure: PropertyResult("La operación no se pudo realizar")
    class FoundList<T>(val list: List<T>): PropertyResult("Se encontraron multiples propiedades")
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
        if (!property.isValid()) {
            return PropertyResult.WrongProperty()
        }
        else if (property.id == null) {
            return PropertyResult.NotFound()
        }

        return try {
            val query =
                dbConnection.prepareStatement("UPDATE property SET title=?, shortDescription=?, fullDescription=?, type=?, price=?, state=?, action=? where id=?;")
            query.setString(1, property.title)
            query.setString(2, property.shortDescription)
            query.setString(3, property.fullDescription)
            query.setString(4, property.type.toString())
            query.setFloat(5, property.price)
            query.setString(6, property.state.toString())
            query.setString(7, property.action.toString())
            query.setInt(8, property.id.toInt())

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

    fun getById (propertyId: UInt): PropertyResult {
        if (propertyId < 1u) {
            return PropertyResult.NotFound()
        }

        val query = "SELECT * FROM property WHERE id=${propertyId}"
        val result = DataFrame.readSqlQuery(dbConnection, query)

        return if (result.isEmpty()) {
            PropertyResult.NotFound()
        } else {
            PropertyResult.Found(Property.fromDataRow(result.first()))
        }
    }

    fun getByHouseOwner (houseOwnerId: Int): PropertyResult {
        if (houseOwnerId < 1) {
            return PropertyResult.NotFound()
        }

        val query = "SELECT * FROM property WHERE id=${houseOwnerId}"
        val result = DataFrame.readSqlQuery(dbConnection, query)

        return PropertyResult.FoundList(Property.fromDataFrame(result))
    }

    fun getAll (): PropertyResult {
        val result = DataFrame.readSqlTable(dbConnection, "property")

        return PropertyResult.FoundList(Property.fromDataFrame(result))
    }

    fun addImage (propertyId: UInt, image: File): PropertyResult {
        val imageStream = FileInputStream(image)

        return try {
            val query = dbConnection.prepareStatement("INSERT INTO propertyPictures (picture, propertyId) VALUES (?, ?);")

            query.setBinaryStream(1, imageStream, image.length())
            query.setInt(2, propertyId.toInt())

            if (query.executeUpdate() < 1) {
                PropertyResult.Failure()
            }
            else {
                PropertyResult.Success()
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

    fun getImages (propertyId: UInt): PropertyResult {
        val pictures = ArrayList<Image>()

        return try {
            val query = dbConnection.prepareStatement("SELECT picture FROM propertyPictures WHERE propertyId=?")

            query.setInt(1, propertyId.toInt())

            val result = query.executeQuery()

            while (result.next()) {
                val image: Image = ImageIO.read(result.getBinaryStream(1))
                pictures.add(image)
            }

            if (pictures.isEmpty())
                PropertyResult.NotFound()
            else
                PropertyResult.FoundList(pictures)
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }
}
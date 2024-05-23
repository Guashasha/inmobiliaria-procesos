package main.kotlin.DAO

import DTO.Property
import DTO.PropertyType
import DataAccess.DataBaseConnection
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
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
    class WrongQuery: PropertyResult("Por favor borre cualquiera de los siguientes caracteres: ', \", -, #, / y *")
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
            return PropertyResult.WrongProperty()
        }

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM property WHERE id=?;")

            query.setInt(1, propertyId.toInt())

            val result = query.executeQuery()

            if (result.next()) {
                PropertyResult.Found(Property.fromResultSet(result))
            } else {
                PropertyResult.NotFound()
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

    fun getByQuery (query: String, propertyType: PropertyType): PropertyResult {
        val unsafeString = Regex("""[-*/\"'#]+""")

        if (unsafeString.containsMatchIn(query)) {
            return PropertyResult.WrongQuery()
        }

        return try {
            val result = if (propertyType == PropertyType.all) {
                val dbQuery =
                    dbConnection.prepareStatement("SELECT * FROM property WHERE state=\"available\" AND (fullDescription LIKE \"%$query%\" OR shortDescription LIKE \"%$query%\" OR title LIKE \"%$query%\" OR direction LIKE \"%$query%\");")

                dbQuery.executeQuery()
            } else {
                val dbQuery =
                    dbConnection.prepareStatement("SELECT * FROM property WHERE type=\"$propertyType\" AND state=\"available\" AND (fullDescription LIKE \"%$query%\" OR shortDescription LIKE \"%$query%\" OR title LIKE \"%$query%\" OR direction LIKE \"%$query%\");")

                dbQuery.executeQuery()
            }

            val list = ArrayList<Property>()

            while (result.next()) {
                list.add(Property.fromResultSet(result))
            }

            if (list.isNotEmpty()) {
                PropertyResult.FoundList(list)
            } else {
                PropertyResult.NotFound()
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

    fun getByHouseOwner (houseOwnerId: Int): PropertyResult {
        if (houseOwnerId < 1) {
            return PropertyResult.NotFound()
        }

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM property WHERE id=?;")

            query.setInt(1, houseOwnerId)

            val result = query.executeQuery()
            val list = ArrayList<Property>()

            while (result.next()) {
                list.add(Property.fromResultSet(result))
            }

            if (list.isNotEmpty()) {
                PropertyResult.FoundList(list)
            } else {
                PropertyResult.NotFound()
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

    fun getAll (): PropertyResult {
        return try {
            val result = dbConnection.prepareStatement("SELECT * FROM property;").executeQuery()
            val list = ArrayList<Property>()

            while (result.next()) {
                list.add(Property.fromResultSet(result))
            }

            if (list.isNotEmpty()) {
                PropertyResult.FoundList(list)
            }
            else {
                PropertyResult.NotFound()
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
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
            val query = dbConnection.prepareStatement("SELECT picture FROM propertyPictures WHERE propertyId=?;")

            query.setInt(1, propertyId.toInt())

            val result = query.executeQuery()

            while (result.next()) {
                val bufferedImage = ImageIO.read(result.getBinaryStream(1))
                val image: Image = SwingFXUtils.toFXImage(bufferedImage, null)
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
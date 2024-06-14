package main.kotlin.DAO

import DTO.HouseOwner
import DTO.Property
import DTO.PropertyType
import DataAccess.DataBaseConnection
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO
import java.sql.SQLException

sealed class PropertyResult (var message: String) {
    class Success: PropertyResult("La operación se realizó correctamente")
    class Failure: PropertyResult("La operación no se pudo realizar")
    class OwnerFound(val houseOwner: HouseOwner): PropertyResult("Se encontró el propietario")
    class FoundList<T>(val list: List<T>): PropertyResult("Se encontraron multiples propiedades")
    class Found(val property: Property): PropertyResult("Se encontró la propiedad")
    class FoundImage(val image: Image): PropertyResult("Se encontró ninguna imagen")
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
            val query = dbConnection.prepareStatement("INSERT INTO property (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action, numRooms, numBathrooms, garage, garden, city, size) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")

            query.setString(1, property.title)
            query.setString(2, property.shortDescription)
            query.setString(3, property.fullDescription)
            query.setString(4, property.type.toString())
            query.setLong(5, property.price)
            query.setString(6, property.state.toString())
            query.setString(7, property.direction)
            query.setInt(8, property.houseOwner.toInt())
            query.setString(9, property.action.toString())
            query.setInt(10, property.numRooms)
            query.setInt(11, property.numBathrooms)
            query.setBoolean(12, property.garage)
            query.setBoolean(13, property.garden)
            query.setString(14, property.city)
            query.setLong(15, property.size)

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
            query.setLong(5, property.price)
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
        val unsafeString = Regex("""[*/\"']+""")

        if (unsafeString.containsMatchIn(query)) {
            return PropertyResult.WrongQuery()
        }

        return try {
            val result = if (propertyType == PropertyType.all) {
                val dbQuery =
                    dbConnection.prepareStatement("SELECT * FROM property WHERE state=\"available\" AND (fullDescription LIKE \"%$query%\" OR shortDescription LIKE \"%$query%\" OR title LIKE \"%$query%\" OR direction LIKE \"%$query%\" OR city LIKE \"%$query%\");")

                dbQuery.executeQuery()
            } else {
                val dbQuery =
                    dbConnection.prepareStatement("SELECT * FROM property WHERE type=\"$propertyType\" AND state=\"available\" AND (fullDescription LIKE \"%$query%\" OR shortDescription LIKE \"%$query%\" OR title LIKE \"%$query%\" OR direction LIKE \"%$query%\" OR city LIKE \"%$query%\");")

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

    fun getOwnerByEmail (email: String): PropertyResult {
        return try {
            val query = dbConnection.prepareStatement("""SELECT * FROM houseOwner WHERE email=?;""")
            query.setString(1, email)

            val result = query.executeQuery();

            return if (result.next()) {
                PropertyResult.OwnerFound(HouseOwner.fromResultSet(result))
            }
            else {
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

    fun getCities (): PropertyResult {
        return try {
            val result = dbConnection.prepareStatement("SELECT * FROM city;").executeQuery()
            val list = ArrayList<String>()

            while (result.next()) {
                list.add(result.getString(1))
            }

            if (list.isNotEmpty()) {
                PropertyResult.FoundList(list)
            }
            else {
                val result = PropertyResult.NotFound()
                result.message = "No se pudieron recuperar las ciudades"
                result
            }
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

    fun addImage (image: File, propertyId: Int): PropertyResult {
        val imageStream = FileInputStream(image)

        return try {
            val query = dbConnection.prepareStatement("UPDATE property SET image=? WHERE id=?;")

            query.setBinaryStream(1, imageStream, image.length())
            query.setInt(2, propertyId);

            if (query.executeUpdate() < 1) {
                PropertyResult.Failure()
            }
            else {
                PropertyResult.Success()
            }
        }
        catch (error: SQLException) {
            if (error.errorCode == 1406) {
                PropertyResult.DBError("Tamaño de imagen demasiado grande, no se pudo agregar")
            }
            else {
                PropertyResult.DBError(error.message.toString())
            }
        }
    }

    fun getImage (propertyId: UInt): PropertyResult {
        return try {
            val query = dbConnection.prepareStatement("SELECT image FROM property WHERE id=?;")

            query.setInt(1, propertyId.toInt())

            val result = query.executeQuery()
            var image: Image? = null

            if (result.next()) {
                val bufferedImage = ImageIO.read(result.getBinaryStream(1)?: return PropertyResult.NotFound())
                image = SwingFXUtils.toFXImage(bufferedImage, null)
            }

            PropertyResult.FoundImage(image!!)
        }
        catch (error: SQLException) {
            PropertyResult.DBError(error.message.toString())
        }
    }

}
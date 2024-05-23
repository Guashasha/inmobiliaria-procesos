import DTO.Property
import DTO.PropertyAction
import DTO.PropertyState
import DTO.PropertyType
import DataAccess.DataBaseConnection
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class PropertyCUsTest {
    private val dao = PropertyDAO()

    @Test
    fun searchZoneProperties () {
        val expectedList = arrayListOf(
            Property(1u, "casita en xalapa", "casa a la orilla de los lagos en xalapa veracruz", "casa en el centro, en la orilla de los lagos, cuenta con todos los servicios basicos y tiene cerca muchas tiendas", PropertyType.house, 2500f, PropertyState.available, "xalapa veracruz colonia centro, calle benito juarez num 5", 1u, PropertyAction.rent, null),
            Property(2u, "Casa rustica", "casa rustica a la orilla de xalapa", "casa rustica a la orilla de xalapa, todos los servicios basicosdisponibles: internet, agua potable, electricidad, television por cable, multiples negocios cerca", PropertyType.house, 5000f, PropertyState.available, "xalapa, ver. col. el haya", 1u, PropertyAction.rent, null))

        when (val result = dao.getByQuery("xalapa", PropertyType.all)) {
            is PropertyResult.FoundList<*> -> assertEquals(expectedList, result.list)
            else -> fail(result.message)
        }
    }

    @Test
    fun searchEmptyZoneProperties () {
        assert(dao.getByQuery("Alemania", PropertyType.all) is PropertyResult.NotFound)
    }

    @Test
    fun getPropertyDetails () {
        val expectedProperty = Property(2u, "Casa rustica", "casa rustica a la orilla de xalapa", "casa rustica a la orilla de xalapa, todos los servicios basicosdisponibles: internet, agua potable, electricidad, television por cable, multiples negocios cerca", PropertyType.house, 5000f, PropertyState.available, "xalapa, ver. col. el haya", 1u, PropertyAction.rent, null)
        val result = dao.getById(2u)

        if (result is PropertyResult.Found) {
            assertEquals(expectedProperty, result.property)
        }
        else {
            fail("No se encontró la propiedad que debería")
        }
    }

    @Test
    fun modifyPropertyState () {
        val expectedProperty = Property(1u, "casita en xalapa", "casa a la orilla de los lagos en xalapa veracruz", "casa en el centro, en la orilla de los lagos, cuenta con todos los servicios basicos y tiene cerca muchas tiendas", PropertyType.house, 2500f, PropertyState.suspended, "xalapa veracruz colonia centro, calle benito juarez num 5", 1u, PropertyAction.rent, null)
        val result = dao.modify(expectedProperty)

        assert(result is PropertyResult.Success)

        val resultProperty = dao.getById(expectedProperty.id!!)

        if (resultProperty is PropertyResult.Found) {
            assertEquals(expectedProperty, resultProperty.property)
        }
        else {
            fail("No se encontró la propiedad modificada")
        }
    }

    @Test
    fun addProperty () {
        val property = Property(null, "edificio ejecutivo de 4 pisos", "edificio de 4 pisos en la zona centro de xalapa", "edificio ejecutivo con todos los servicios necesarios, zona centrica con mucha actividad, 4 pisos, todos con baño, sistema de tuberias y electricidad", PropertyType.building, 400500f, PropertyState.available, "xalapa veracruz, colonia centro, calle Miguel Idalgo numero 35, 91010", 1u, PropertyAction.sell, null)
        val result = dao.add(property)

        assert(result is PropertyResult.Success)

        val resultProperty = dao.getByQuery("edificio ejecutivo de 4 pisos", PropertyType.building)

        if (resultProperty is PropertyResult.FoundList<*>) {
            assertEquals(property, resultProperty.list.get(0))
        }
        else {
            fail(resultProperty.message)
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp(): Unit {
            TestHelper.addTestData()
        }
    }
}
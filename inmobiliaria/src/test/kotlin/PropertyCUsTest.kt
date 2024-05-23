import DTO.Property
import DTO.PropertyAction
import DTO.PropertyState
import DTO.PropertyType
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class PropertyCUsTest {
    private val dao = PropertyDAO()

    @Test
    fun buscarPropiedadesDeZona () {
        val expectedList = arrayListOf(
            Property(1u, "casita en xalapa", "casa a la orilla de los lagos en xalapa veracruz", "casa en el centro, en la orilla de los lagos, cuenta con todos los servicios basicos y tiene cerca muchas tiendas", PropertyType.house, 2500f, PropertyState.available, "xalapa veracruz colonia centro, calle benito juarez num 5", 1u, PropertyAction.rent, null),
            Property(2u, "Casa rustica", "casa rustica a la orilla de xalapa", "casa rustica a la orilla de xalapa, todos los servicios basicosdisponibles: internet, agua potable, electricidad, television por cable, multiples negocios cerca", PropertyType.house, 5000f, PropertyState.available, "xalapa, ver. col. el haya", 1u, PropertyAction.rent, null))

        when (val result = dao.getByQuery("xalapa", PropertyType.all)) {
            is PropertyResult.FoundList<*> -> assertEquals(expectedList, result.list)
            else -> fail(result.message)
        }
    }

    @Test
    fun buscarPropiedadesDeZonaVacia () {
        assert(dao.getByQuery("Alemania", PropertyType.all) is PropertyResult.NotFound)
    }

}
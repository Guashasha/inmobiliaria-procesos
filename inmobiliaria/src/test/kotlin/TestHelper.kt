import java.sql.Connection
import java.sql.DriverManager

class TestHelper {
    companion object {
        private val dbConnection: Connection = DriverManager.getConnection("jdbc:mariadb://localhost:3307/PXNGAgency", "adminPXNG", "RobaloBurbuja")

        fun addTestData() {
            clearTables()

            addHouseOwners()
            addAccounts()
            addAgent()

            addProperties()

            addQuery()
            addSearch()
            addVisit()

            dbConnection.close()
        }

        private fun addHouseOwners () {
            dbConnection.prepareStatement("""insert into houseOwner (name, email, phone) values ("david carrion", "davidcarrion@hotmail.com", "2288554477");""").execute()
        }

        private fun addAccounts () {
            dbConnection.prepareStatement("""insert into account (name, type, email, phone, password) values ("pale molina", "client", "pale@hotmail.com", "2255447788", "habitacionDeVuelo");""").execute()
            dbConnection.prepareStatement("""insert into account (name, type, email, phone, password) values ("fer molina", "agent", "fer@hotmail.com", "2255447788", "habitacionDeVuelo2");""").execute()
            dbConnection.prepareStatement("""insert into account (name, type, email, phone, password) values ("david carrion", "agent", "david@hotmail.com", "2554445555", "contrasena")""").execute()
        }

        private fun addProperties () {
            dbConnection.prepareStatement("""insert into property (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action) values ("casita en xalapa", "casa a la orilla de los lagos en xalapa veracruz", "casa en el centro, en la orilla de los lagos, cuenta con todos los servicios basicos y tiene cerca muchas tiendas", "house", 2500.0, "available", "xalapa veracruz colonia centro, calle benito juarez num 5", 1, "rent");""").execute()
            dbConnection.prepareStatement("""insert into property (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action) values ("Casa rustica", "casa rustica a la orilla de xalapa", "casa rustica a la orilla de xalapa, todos los servicios basicosdisponibles: internet, agua potable, electricidad, television por cable, multiples negocios cerca", "house", 5000, "available", "xalapa, ver. col. el haya", 1, "rent");""").execute()
            dbConnection.prepareStatement("""insert into property (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action) values ("Casa rustica", "casa rustica a la orilla de coatepec", "casa rustica a la orilla de coatepec, todos los servicios basicosdisponibles: internet, agua potable, electricidad, television por cable, multiples negocios cerca", "house", 5000, "available", "coatepec, ver. col. el haya", 1, "rent");""").execute()
        }

        private fun addAgent () {
            dbConnection.prepareStatement("""insert into agent (accountId, personelNumber) values (2, "21457896332165498712");""").execute()
        }

        private fun addQuery () {
            dbConnection.prepareStatement("""insert into query () values ();""")
        }

        private fun addSearch () {
        }

        private fun addVisit () {
        }

        private fun clearTables () {
            dbConnection.prepareStatement("delete from visit;").execute()
            dbConnection.prepareStatement("alter table visit AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from search;").execute()
            dbConnection.prepareStatement("alter table search AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from query;").execute()
            dbConnection.prepareStatement("alter table query AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from property;").execute()
            dbConnection.prepareStatement("alter table property AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from agent;").execute()
            dbConnection.prepareStatement("alter table agent AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from account;").execute()
            dbConnection.prepareStatement("alter table account AUTO_INCREMENT=0;").execute()
            dbConnection.prepareStatement("delete from houseOwner;").execute()
            dbConnection.prepareStatement("alter table houseOwner AUTO_INCREMENT=0;").execute()
        }
    }
}
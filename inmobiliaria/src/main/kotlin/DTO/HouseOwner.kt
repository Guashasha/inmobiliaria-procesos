package DTO

import java.sql.ResultSet

class HouseOwner (
    val id: UInt?,
    val name: String,
    val email: String,
    val phone: String
) {
    companion object {
        fun fromResultSet (result: ResultSet): HouseOwner {
            val id: UInt = result.getInt(1).toUInt()
            val name = result.getString(2)
            val email = result.getString(3)
            val phone = result.getString(4)

            return HouseOwner(id, name, email, phone)
        }
    }
}
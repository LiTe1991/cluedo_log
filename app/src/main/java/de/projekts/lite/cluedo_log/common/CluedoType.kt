package de.projekts.lite.cluedo_log.common

/**
 * Created by lite on 07.12.17.
 */

enum class CluedoType(nameType: String) {
    STATIC(""),
    KLASSIK_2016("Klassisch - 2016"),
    TBBT("The Bing Bang Theory");

    val nameType = nameType

    fun getListOfTypes(): ArrayList<String> {
        val _temp = ArrayList<String>()

        _temp.add(KLASSIK_2016.nameType)
        _temp.add(TBBT.nameType)

        return _temp
    }
}

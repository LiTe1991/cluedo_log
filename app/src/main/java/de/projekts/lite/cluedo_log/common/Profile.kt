package de.projekts.lite.cluedo_log.common

import java.io.Serializable

/**
 * Created by lite on 05.12.17.
 */

class Profile constructor(val profileName: String, val cluedoType: String, val numberOfPlayers:
Int, val gamers: ArrayList<Gamer>) : Serializable
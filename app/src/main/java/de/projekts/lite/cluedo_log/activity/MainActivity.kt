package de.projekts.lite.cluedo_log.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import de.projekts.lite.cluedo_log.R
import de.projekts.lite.cluedo_log.common.CluedoType
import de.projekts.lite.cluedo_log.common.Gamer
import de.projekts.lite.cluedo_log.common.Profile
import de.projekts.lite.cluedo_log.common.ProfileViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    private val configName = "profile_config"

    private val dropdown_number = ArrayList<String>()
    private var _tempList = ArrayList<Profile>()

    private var error = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (2..8).mapTo(dropdown_number) { it.toString() }
    }

    override fun onStart() {
        super.onStart()

        readPropertieFile()

        if (!error.isEmpty()) {
            val _tempErrorList = ArrayList<String>()
            _tempErrorList.add(error)

            listView.textAlignment = ListView.TEXT_ALIGNMENT_CENTER
            listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _tempErrorList)
        } else {
            reloadListView()
        }

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            createInputDialog(view)
        }
    }

    private fun createInputDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)

        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.inputdialog_profile, null)
        builder.setView(dialogView)

        builder.setTitle("Profile erstellen")

        val spin_type = dialogView.findViewById<Spinner>(R.id.custom_spinner_type)
        val adapter_type = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                CluedoType.STATIC.getListOfTypes())
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_type.adapter = adapter_type

        val spin_number = dialogView.findViewById<Spinner>(R.id.custom_spinner_players)
        val adapter_number = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dropdown_number)
        adapter_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_number.adapter = adapter_number

        builder.setNegativeButton("Abbrechen", { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        builder.setPositiveButton("Weiter", { dialogInterface, i ->
            val profileName = dialogView.findViewById<EditText>(R.id.editText_profileName).text.toString()
            val cluedoType = dialogView.findViewById<Spinner>(R.id.custom_spinner_type).selectedItem
            val numberPlayers = dialogView.findViewById<Spinner>(R.id.custom_spinner_players).selectedItem

            createInputGamerDialog(view, profileName, cluedoType as String, numberPlayers as String)

            dialogInterface.dismiss()
        })

        builder.create().show()
    }

    private fun createInputGamerDialog(view: View, profileName: String, cluedoType: String, numberOfPlayers: String) {
        var count = 0
        val _tempGamers = ArrayList<Gamer>()

        val builder = AlertDialog.Builder(view.context)

        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.inputdialog_gamer, null)

        builder.setView(dialogView)

        builder.setTitle("Spieler anlegen")
        builder.setCancelable(false)

        builder.setNegativeButton("Abbrechen", { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        builder.setPositiveButton("Fertig", { dialogInterface, i ->
            val profile = Profile(profileName, cluedoType, numberOfPlayers.toInt(), _tempGamers)
            _tempList.add(profile)

            writePropertieFile()

            reloadListView()

            Snackbar.make(view, "Neues Profile hinzugefügt", Snackbar.LENGTH_SHORT).setAction("Action", null).show()

            dialogInterface.dismiss()
        })

        val dialog = builder.create()
        dialog.show()

        val nextButton = dialogView.findViewById(R.id.imageButton_nextGamer) as Button
        nextButton.setOnClickListener {
            val playerName = dialogView.findViewById<EditText>(R.id.editText_gamerName1).text.toString()
            val picturPath = dialogView.findViewById<EditText>(R.id.editText_picturePath1).text.toString()

            _tempGamers.add(Gamer(playerName, picturPath))

            System.out.println(playerName + " " + picturPath)

            dialogView.findViewById<EditText>(R.id.editText_gamerName1).text = null
            dialogView.findViewById<EditText>(R.id.editText_picturePath1).text = null

            count++

            if(count == numberOfPlayers.toInt()) {
                dialogView.findViewById<EditText>(R.id.editText_gamerName1).isClickable = false
                dialogView.findViewById<EditText>(R.id.editText_gamerName1).isEnabled = false
                dialogView.findViewById<EditText>(R.id.editText_picturePath1).isClickable = false
                dialogView.findViewById<EditText>(R.id.editText_picturePath1).isEnabled = false

                nextButton.isClickable = false
                nextButton.isEnabled = false

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isClickable = true
            }

            dialogView.findViewById<EditText>(R.id.editText_gamerName1).requestFocus()
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isClickable = false
    }

    private fun reloadListView() {
        error = ""

        listView.adapter = ProfileViewAdapter(this, _tempList)
        listView.invalidate()
    }

    private fun writePropertieFile() {
        var fos: FileOutputStream? = null

        try {
            fos = openFileOutput(configName, Context.MODE_PRIVATE)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            val oos = ObjectOutputStream(fos)
            oos.writeObject(_tempList)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readPropertieFile() {
        val fis: FileInputStream?

        try {
            fis = openFileInput(configName)

            if (fis == null)
                error = "Keine Profile vorhanden"
        } catch (e: FileNotFoundException) {
            error = "Keine Profile vorhanden"
            e.printStackTrace()
            return
        }

        try {
            val ois = ObjectInputStream(fis)
            _tempList = ois.readObject() as ArrayList<Profile>

            if (_tempList.isEmpty()) {
                error = "Keine Profile vorhanden"
            } else {
                for(item in _tempList) {
                    Log.i("LOAD-INFO", "##----------------------------------------------------##")
                    Log.i("LOAD-INFO", "Profilname: " + item.profileName)
                    Log.i("LOAD-INFO", "Cluedo-Type: " + item.cluedoType)
                    Log.i("LOAD-INFO", "Anzahl-Spieler: " + item.numberOfPlayers.toString())

                    for (item2 in item.gamers) {
                        Log.i("LOAD-INFO", "Spieler: " + item2.name + " Path: " + item2.path)
                    }

                    Log.i("LOAD-INFO", "##----------------------------------------------------##")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}
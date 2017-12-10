package de.projekts.lite.cluedo_log.common

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import de.projekts.lite.cluedo_log.R
import java.io.InputStream
import java.lang.ref.WeakReference


/**
 * Created by lite on 06.12.17.
 */

class ProfileViewAdapter(context: Context, profiles: ArrayList<Profile>) : ArrayAdapter<Profile>(context, R.layout.element_profile, profiles) {
    private var _context: WeakReference<Context> = WeakReference<Context>(context)
    private var _profiles: ArrayList<Profile> = profiles

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val _tempContext = _context.get() as Context

        val inflater = _tempContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.element_profile, viewGroup, false)
        val textView_profileName = rowView.findViewById(R.id.textView_profileName) as TextView
        val textView_cluedoType = rowView.findViewById(R.id.textView_cluedoType) as TextView
        val textView_numberPlayers = rowView.findViewById(R.id.textView_numberPlayers) as TextView
        val imageView = rowView.findViewById(R.id.imageView_cluedoType) as ImageView

        textView_profileName.text = _profiles[i].profileName
        textView_cluedoType.text = _tempContext.getString(R.string.cluedo, _profiles[i].cluedoType)
        textView_numberPlayers.text = _tempContext.getString(R.string.numberPlayers, _profiles[i].numberOfPlayers.toString())

        var _path = "no_image.jpg"
        if (_profiles[i].cluedoType == CluedoType.KLASSIK_2016.nameType) {
            _path = "cluedo_klassik.jpg"
        } else if (_profiles[i].cluedoType == CluedoType.TBBT.nameType) {
            _path = "cluedo_tbbt.jpg"
        } else {
            _path = "no_image.jpg"
        }

        val _assetManager = _tempContext.assets as AssetManager
        val _image = _assetManager.open(_path) as InputStream
        val _drawable = BitmapFactory.decodeStream(_image)

        imageView.setImageBitmap(Bitmap.createScaledBitmap(_drawable, _drawable.width / 2,
                _drawable.height / 2, false))

        return rowView
    }
}

package com.example.control_escolar

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_act_principal.*
import kotlinx.android.synthetic.main.fragment_asistencia.*

class act_Principal : AppCompatActivity(){//, NavigationView.OnNavigationItemSelectedListener{

   // private lateinit var drawer:DrawerLayout
    //private lateinit var toggle: ActionBarDrawerToggle



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_principal)

        var asistenciaFragment =  AsistenciaFragment()
        var actividadFragment =  ActividadFragment()
        var calificarFragment =  CalificarFragment()
        //val toolbar: androidx.appcompat.widget.Toolbar =  findViewById(R.id.toolbar_main)
        //setSupportActionBar(toolbar)

       // drawer =  findViewById(R.id.drawer_layout)

        //toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        //drawer.addDrawerListener(toggle)//aqui esta el problema

       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
       // supportActionBar?.setHomeButtonEnabled(true)

        //val navigationView:NavigationView = findViewById(R.id.nav_view)
        //navigationView.setNavigationItemSelectedListener(this)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_asistencia ->{
                    setCurrentFragment(asistenciaFragment)
                    var inten = Intent(this, act_Asistencia::class.java)
                    startActivity((inten))
                    true
                }
                R.id.nav_actividad ->{
                    setCurrentFragment(actividadFragment)
                    //Toast.makeText(this, "Actividad", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_calificar ->{
                    setCurrentFragment(calificarFragment)
                    //Toast.makeText(this, "Calificacion", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false

            }
    }



    /*override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/



   /* override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_one -> Toast.makeText(this, "Item 1", Toast.LENGTH_SHORT).show()
            R.id.nav_item_two -> Toast.makeText(this, "Item 2", Toast.LENGTH_SHORT).show()
            R.id.nav_item_three -> Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }*/

}

    private fun setCurrentFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.containerPrincipal, fragment)
            commit()
        }
    }

    }












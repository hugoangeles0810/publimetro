package pe.joedayz.publimetro.controller;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.adapter.NavDrawerListAdapter;
import pe.joedayz.publimetro.controller.fragment.ConfiguracionFragment;
import pe.joedayz.publimetro.controller.fragment.EstablecimientosFragment;
import pe.joedayz.publimetro.model.Ciudad;
import pe.joedayz.publimetro.model.NavDrawerItem;
import pe.joedayz.publimetro.model.Rubro;


public class DashboardActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    private static String TAG = DashboardActivity.class.getSimpleName();

    private String urlJsonArry = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getRubrosEstablecimientos&idUbicacion=";


    private Ciudad ciudad;


    /*Nav Drawer start*/

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;

    /*Nav Drawer end*/


    /*
    Carga de Rubros

     */

    // Progress dialog
    private ProgressDialog pDialog;
    // temporary string to show the parsed response
    private String jsonResponse;
    private List<Rubro> rubrosList = new ArrayList<Rubro>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        ciudad = (Ciudad) getIntent().getSerializableExtra("ciudadSeleccionada");

        Rubro rubro = new Rubro();
        rubro.setCodigo("0");
        rubro.setDescripcion(ciudad.getDescripcion() + " - Todos");
        rubrosList.add(rubro);


        makeJsonRubrosRequest();

        ArrayAdapter<Rubro> ad = new ArrayAdapter<Rubro>(this, R.layout.custom_spinner_rubros, rubrosList);
        ad.setDropDownViewResource(R.layout.custom_spinner_popup);

        getActionBar().setListNavigationCallbacks(ad, this);

        mTitle = mDrawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);


        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);




        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayFragment(0);
        }

        handleIntent(getIntent());

    }

    private void makeJsonRubrosRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonArry + ciudad.getCodigo(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject lista = response.getJSONObject("lista");
                    JSONArray rubros = lista.getJSONArray("rubro");

                    jsonResponse = "";

                    for (int i = 0; i < rubros.length(); i++) {
                        JSONObject rubroJSON = (JSONObject) rubros
                                .get(i);
                        Rubro rubro = new Rubro();
                        rubro.setCodigo(rubroJSON.getString("codigo"));
                        rubro.setDescripcion(rubroJSON.getString("descripcion"));


                        rubrosList.add(rubro);

                        jsonResponse += "rubro: " + rubro + "\n\n";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    protected void onPause() {
        super.onPause();
        hidepDialog();

    }
    private void showpDialog() {
        if(pDialog==null){
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Por favor, espere...");
            pDialog.setCancelable(false);
        }

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        getIntent().putExtra("rubro", rubrosList.get(itemPosition));
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(fragment instanceof EstablecimientosFragment) {
            displayFragment(0);
        }
        return false;
    }



    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayFragment(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private void displayFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new EstablecimientosFragment();
                fragment.setArguments(getIntent().getExtras());
                break;
            case 1:
                fragment = new ConfiguracionFragment();
                fragment.setArguments(getIntent().getExtras());
                break;
            case 2:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();


            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);


            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /** Inicio Menu del Action Bar **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.mapa:
                displayFragment(3);
                return false;
            case R.id.ofertas:
                displayFragment(4);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);


        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SpannableString s1 = new SpannableString(searchMenuItem.getTitle());
        s1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s1.length(), 0);
        searchMenuItem.setTitle(s1);

        MenuItem mapaMenuItem = menu.findItem(R.id.mapa);
        SpannableString s2 = new SpannableString(mapaMenuItem.getTitle());
        s2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s2.length(), 0);
        mapaMenuItem.setTitle(s2);


        return super.onPrepareOptionsMenu(menu);
    }


<<<<<<< HEAD
    //Busqueda por texto

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            showResults(query);
        }
    }

    private void showResults(String query) {
        getIntent().putExtra("queryText", query);
        displayFragment(0);

    }

=======
    /** Fin del menu del actionbar **/
>>>>>>> 1.7-ofertas-establecimiento
}

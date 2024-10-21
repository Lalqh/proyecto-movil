package com.example.proyecto

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.GeoApiContext
import com.google.maps.DirectionsApi
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.EncodedPolyline

class Ubi_proveedores : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ubi_proveedores, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        view.findViewById<FloatingActionButton>(R.id.fab_show_polyline).setOnClickListener {
            showPolylines()
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 999)
        }

        val fixedLocation = LatLng(20.70316752926864, -103.38911335966066)
        mMap.addMarker(MarkerOptions().position(fixedLocation).title("Fixed Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 15f))
    }

    private fun showPolylines() {
        val apikey = getString(R.string.apiKey)
        val fixedLocation = LatLng(20.70316752926864, -103.38911335966066)

        val destinations = listOf(
            LatLng(20.67599778054562, -103.37891959070663),
            LatLng(20.656220276853915, -103.38566259533701),
            LatLng(20.656501371961753, -103.38142470537436),
            LatLng(20.654714914986894, -103.38293144133087)
        )

        val context = GeoApiContext.Builder().apiKey(apikey).build()

        destinations.forEach { destination ->
            try {
                val result: DirectionsResult = DirectionsApi.newRequest(context)
                    .origin(com.google.maps.model.LatLng(fixedLocation.latitude, fixedLocation.longitude))
                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .await()

                val route: DirectionsRoute = result.routes[0]
                val polyline: EncodedPolyline = route.overviewPolyline
                val points: List<com.google.maps.model.LatLng> = polyline.decodePath()
                val latLngList = points.map { LatLng(it.lat, it.lng) }

                val polylineOptions = PolylineOptions()
                    .geodesic(true)
                    .color(Color.parseColor("#F7A541"))
                    .width(5f)
                    .addAll(latLngList)

                mMap.addPolyline(polylineOptions)

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 12f))

                mMap.addMarker(MarkerOptions().position(destination).title("Destino"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

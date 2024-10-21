package com.example.proyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyecto.databinding.FragmentUbicacionBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class Ubicacion : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var mMap: GoogleMap
    private var minimumDistance = 30
    private val PERMISSION_LOCATION = 999
    private lateinit var binding: FragmentUbicacionBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 1000
            smallestDisplacement = minimumDistance.toFloat()
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.e("APP 06", "${locationResult.lastLocation?.latitude}, ${locationResult.lastLocation?.longitude}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUbicacionBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        binding.fabDirections.setOnClickListener {
            openGoogleMapsDirections()
        }
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_LOCATION)
        }

        val gdl = LatLng(20.70316752926864, -103.38911335966066)
        mMap.addMarker(MarkerOptions().position(gdl).title("La esquina del sabor").snippet("Tienda de abarrotes"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gdl, 15f))
        mMap.setOnMapClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION, ignoreCase = true) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
        }
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onMapClick(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
        mMap.addMarker(
            MarkerOptions()
                .title("Marca personal")
                .snippet("Mi sitio marcado")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
                .position(latLng)
        )
    }

    private fun openGoogleMapsDirections() {
        val gdl = LatLng(20.70316752926864, -103.38911335966066)
        val gmmIntentUri = Uri.parse("google.navigation:q=${gdl.latitude},${gdl.longitude}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        }
    }
}
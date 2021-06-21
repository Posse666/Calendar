package com.posse.kotlin1.calendar.view.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentGoogleMapsBinding
import com.posse.kotlin1.calendar.utils.setWindowSize
import java.time.LocalDate

private const val ARG_LONGITUDE = "longitude"
private const val ARG_LATITUDE = "latitude"
private const val ARG_DATE = "current Date"

class GoogleMapsFragment : DialogFragment() {

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var date: String = ""
    private var _binding: FragmentGoogleMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val initialPlace = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(initialPlace).title(date))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPlace, 13f))
        activateMyLocation(googleMap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE, "")
            longitude = it.getDouble(ARG_LONGITUDE)
            latitude = it.getDouble(ARG_LATITUDE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        setWindowSize(this)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(date: LocalDate, latitude: Double, longitude: Double) =
            GoogleMapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date.toString())
                    putDouble(ARG_LATITUDE, latitude)
                    putDouble(ARG_LONGITUDE, longitude)
                }
            }
    }
}
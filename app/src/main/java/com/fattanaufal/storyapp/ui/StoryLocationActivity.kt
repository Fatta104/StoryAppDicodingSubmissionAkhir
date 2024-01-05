package com.fattanaufal.storyapp.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.animation.AnimationPackage

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.fattanaufal.storyapp.databinding.ActivityStoryLocationBinding
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.ui.story.StoryViewModel
import com.fattanaufal.storyapp.ui.story.StoryViewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class StoryLocationActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryLocationBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var geocoder: Geocoder
    private val boundsBuilder = LatLngBounds.Builder()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geocoder = Geocoder(this)
        storyViewModel = obtainViewModel(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        defaultConfigMap()
        changeMapType()

        storyViewModel.locationStories.observe(this) { stories ->
            if (stories != null) {
                storiesNearMe(stories)
            }
        }
        getMyLocation()

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            storyViewModel.getDetailStoryByLatLong(
                marker.position.latitude,
                marker.position.longitude
            ).observe(this) { story ->
                if (story != null) {
                    val location = LatLng(story.lat, story.lon)
                    customInfoWindow(
                        location,
                        story
                    )
                }
            }
            true
        }

        binding.btnOptionMap.setOnClickListener(this)
        binding.btnBackHome.setOnClickListener(this)
        binding.btnHide.setOnClickListener(this)


    }


    private fun defaultConfigMap() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()
    }

    private fun changeMapType() {
        binding.btnDefaultMap.setOnClickListener(this)
        binding.btnSatelliteMap.setOnClickListener(this)
        binding.btnTerrainMap.setOnClickListener(this)
        binding.btnHybridMap.setOnClickListener(this)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Error Style parsing failed.!", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, "Error load map style!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun animateShowInfoWindow() {
        binding.boxShow.visibility = View.VISIBLE
        val boxFadeIn = AnimationPackage.fadeIn(binding.boxShow, 500)

        val boxMoveY = AnimationPackage.translateY(
            binding.boxShow, 500, 120f, 0f
        )

        AnimatorSet().apply {
            play(boxFadeIn).with(boxMoveY)
            start()
        }
    }

    private fun animateShowOptionMap() {
        binding.optionMap.visibility = View.VISIBLE
        val boxFadeIn = AnimationPackage.fadeIn(binding.optionMap, 500)
        val boxMoveY = AnimationPackage.translateY(binding.optionMap, 500, -100f, 0f)

        AnimatorSet().apply {
            play(boxFadeIn).with(boxMoveY)
            start()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_option_map -> {
                if (binding.optionMap.alpha == 0f || binding.optionMap.visibility == View.GONE) {
                    animateShowOptionMap()
                } else {
                    binding.optionMap.visibility = View.GONE
                }
            }

            R.id.btn_default_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                binding.optionMap.visibility = View.GONE
            }

            R.id.btn_satellite_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                binding.optionMap.visibility = View.GONE
            }

            R.id.btn_terrain_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                binding.optionMap.visibility = View.GONE
            }

            R.id.btn_hybrid_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                binding.optionMap.visibility = View.GONE
            }

            R.id.btn_hide -> {
                binding.boxShow.visibility = View.GONE
            }

            R.id.btn_back_home -> {
                finish()
            }
        }
    }

    private fun customInfoWindow(latLng: LatLng, storyEntity: StoryEntity) {
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)

                    binding.tvLocation.text = address.toString()
                    binding.tvSender.text = storyEntity.name
                    binding.tvDesc.text = storyEntity.description

                    binding.btnToDetail.setOnClickListener {
                        val intentToDetail = Intent(this, DetailActivity::class.java)
                        intentToDetail.putExtra(DetailActivity.ITEM_STORY, storyEntity)
                        startActivity(intentToDetail)
                    }

                    animateShowInfoWindow()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error Load Location!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    private fun storiesNearMe(stories: List<StoryEntity>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            )
        }
        val indonesiaBounds = LatLngBounds(LatLng(-11.0, 95.0), LatLng(6.0, 141.0))
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(indonesiaBounds, 0))

    }

    private fun obtainViewModel(appCompatActivity: AppCompatActivity): StoryViewModel {
        val factory = StoryViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[StoryViewModel::class.java]
    }


    companion object {
        const val TAG = "StoryLocationActivity"
    }
}

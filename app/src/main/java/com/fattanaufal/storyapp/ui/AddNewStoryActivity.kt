package com.fattanaufal.storyapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.databinding.ActivityAddNewStoryBinding
import com.fattanaufal.storyapp.ui.story.StoryViewModel
import com.fattanaufal.storyapp.ui.story.StoryViewModelFactory
import com.fattanaufal.storyapp.utils.getImageUri
import com.fattanaufal.storyapp.utils.reduceFileImage
import com.fattanaufal.storyapp.utils.uriToFile
import com.fattanaufal.storyapp.utils.wrapEspressoIdlingResource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class AddNewStoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var addBinding: ActivityAddNewStoryBinding
    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var locationName: String? = null
    private lateinit var storyViewModel: StoryViewModel
    private var currentImageUri: Uri? = null
    private lateinit var dialogConfirm: AlertDialog
    private var latLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.CAMERA] == true
            ) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                // No location access granted.
                Toast.makeText(this, "Permission Rejected!", Toast.LENGTH_SHORT).show()
            }
        }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBinding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(addBinding.root)
        setSupportActionBar(addBinding.appBarList)



        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            !checkPermission(Manifest.permission.CAMERA)
        ) {
            wrapEspressoIdlingResource {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                    )
                )
            }
        }


        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(this)



        storyViewModel = obtainViewModel(this@AddNewStoryActivity)

        storyViewModel.uploadResponse.observe(this) { upload ->
            if (upload != null && !upload.error) {
                successDialog()
            }
        }

        storyViewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.imageUri.observe(this) { uri ->
            if (uri != null) {
                currentImageUri = uri
                addBinding.previewImg.setImageURI(uri)
            }

        }

        val name = intent.getStringExtra(NAME)

        if (name != null) {
            addBinding.edtDesc.hint = getString(R.string.hintDescription, name)
        }

        addBinding.btnCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        addBinding.btnGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        addBinding.switchLoc.setOnCheckedChangeListener { buttonView, isChecked ->
            val fragmentManager = supportFragmentManager.beginTransaction()
            val trackColor = if (isChecked) {
                addBinding.svLoc.visibility = View.GONE
                fragmentManager.hide(mapFragment)
                getMyLocation()
                ContextCompat.getColor(this, R.color.white)
            } else {
                addBinding.svLoc.visibility = View.VISIBLE
                fragmentManager.show(mapFragment)
                latLng = null
                ContextCompat.getColor(this, R.color.white)
            }

            fragmentManager.commit()
            addBinding.switchLoc.trackTintList = ColorStateList.valueOf(trackColor)
        }

        addBinding.svLoc.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    locationName = query.toString().lowercase()
                    if (locationName != null) {
                        getLatLongFromLocationName(locationName)
                    }

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != "") {
                    addBinding.btnSearchLoc.visibility = View.VISIBLE
                    locationName = newText
                } else {
                    addBinding.btnSearchLoc.visibility = View.GONE
                }
                return false
            }

        })

        addBinding.btnSearchLoc.setOnClickListener {
            if (locationName != null) {
                getLatLongFromLocationName(locationName)
            }
        }


        addBinding.btnSend.setOnClickListener {
            dialogConfirm = confirmDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showImage() {
        currentImageUri?.let {
            addBinding.previewImg.setImageURI(it)
            storyViewModel.imageUri.postValue(it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_confirm_add, null)
        val builder = AlertDialog.Builder(this@AddNewStoryActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            uploadStory()
            dialog.dismiss()
        }

        return dialog
    }

    private fun warningDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_error_add, null)
        val builder = AlertDialog.Builder(this@AddNewStoryActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            dialogConfirm.dismiss()
        }
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_add, null)
        val builder = AlertDialog.Builder(this@AddNewStoryActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            val intentToHome = Intent(this@AddNewStoryActivity, MainActivity::class.java)
            intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentToHome)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadStory() {
        wrapEspressoIdlingResource {
            val description = addBinding.edtDesc.text.toString()
            if (currentImageUri == null || description.isEmpty()) {
                warningDialog()
            } else {
                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, this).reduceFileImage()
                    val requestBody = description.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )
                    storyViewModel.uploadStory(multipartBody, requestBody, latLng?.latitude?.toFloat(), latLng?.longitude?.toFloat())
                }
            }
        }


    }

    private fun obtainViewModel(appCompatActivity: AppCompatActivity): StoryViewModel {
        val factory = StoryViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[StoryViewModel::class.java]
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun getLatLongFromLocationName(location: String?) {
        if (location != null) {
            try {
                val addresses = geocoder.getFromLocationName(location, 1)
                if (addresses != null) {
                    latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                    mMap.addMarker(
                        MarkerOptions().position(latLng!!)
                            .title(location)
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 15f))
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    companion object {
        val NAME = "NAME"
    }


}
package com.fattanaufal.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.databinding.ActivityMainBinding
import com.fattanaufal.storyapp.paging.LoadingStateAdapter
import com.fattanaufal.storyapp.preference.UserPrefViewModel
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.preference.UserPreferencesFactory
import com.fattanaufal.storyapp.preference.dataStore
import com.fattanaufal.storyapp.ui.story.StoryAdapter
import com.fattanaufal.storyapp.ui.story.StoryViewModel
import com.fattanaufal.storyapp.ui.story.StoryViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var userPrefViewModel: UserPrefViewModel
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.BarList)

        val pref = UserPreferences.getInstance(application.dataStore)
        userPrefViewModel =
            ViewModelProvider(this, UserPreferencesFactory(pref))[UserPrefViewModel::class.java]
        storyViewModel = obtainViewModel(this@MainActivity)

        alertDialog = loadingDialog()

        userPrefViewModel.getSessionUser().observe(this) { session ->
            if (session != null) {
                alertDialog.show()
                mainBinding.tvUsername.text = session.name
                loadDataRecyclerView()
            }
        }


        mainBinding.btnLogout.setOnClickListener {
            confirmDialog()
        }

        mainBinding.btnStoryAdd.setOnClickListener {
            val intentToAdd = Intent(this, AddNewStoryActivity::class.java)
            intentToAdd.putExtra(AddNewStoryActivity.NAME, mainBinding.tvUsername.text)
            startActivity(intentToAdd)
        }

        mainBinding.btnSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        mainBinding.btnMap.setOnClickListener {
            startActivity(Intent(this, StoryLocationActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        alertDialog.show()
        loadDataRecyclerView()
        return super.onSupportNavigateUp()
    }

    private fun obtainViewModel(appCompatActivity: AppCompatActivity): StoryViewModel {
        val factory = StoryViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[StoryViewModel::class.java]
    }

    private fun loadDataRecyclerView() {
        storyAdapter = StoryAdapter(this)
        mainBinding.rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
        mainBinding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storyViewModel.listStories().observe(this@MainActivity) { stories ->
            if (stories != null) {
                storyAdapter.submitData(lifecycle, stories)
                alertDialog.dismiss()
            }
        }


    }

    private fun confirmDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            userPrefViewModel.logoutUser()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun loadingDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }
}
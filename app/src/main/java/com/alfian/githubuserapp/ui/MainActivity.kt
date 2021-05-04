package com.alfian.githubuserapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfian.githubuserapp.*
import com.alfian.githubuserapp.adapter.ListUserAdapter
import com.alfian.githubuserapp.databinding.ActivityMainBinding
import com.alfian.githubuserapp.db.UserContract
import com.alfian.githubuserapp.entity.User
import com.alfian.githubuserapp.helper.MappingHelper
import com.alfian.githubuserapp.preference.PreferenceSetting

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: ListUserAdapter
    private lateinit var progressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvUser.setHasFixedSize(true)

        adapter = ListUserAdapter()
        adapter.notifyDataSetChanged()

        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        mainViewModel.getUsers().observe(this, { userItems ->
            if (userItems != null) {
                adapter.setData(userItems)
                showLoading(false)
                binding.tvWelcome.text = ""
                binding.imgAnonymous.setImageResource(0)
            }
        })

        mainViewModel.getError().observe(this, { error ->
            val showErrorDialog = AlertDialog.Builder(this)
                .setTitle("Network Error")
                .setMessage(error)
                .setPositiveButton("ok") { _, _ -> }
                .create()

            showErrorDialog.show()
            showLoading(false)
        })
        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val isFavorite = isFavoriteState(data)
                val showDetail = Intent(this@MainActivity, DetailActivity::class.java)
                showDetail.putExtra(DetailActivity.EXTRA_FAVORITE, isFavorite)
                showDetail.putExtra(DetailActivity.EXTRA_USER, data.username)
                startActivity(showDetail)
            }
        })

        progressLayout = findViewById(R.id.progress_view)
    }

    override fun onBackPressed() {
        if (progressLayout.visibility == View.VISIBLE) {
            progressLayout.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun isFavoriteState(data: User): Boolean {
        var isFavorite = false
        val uriWithId = Uri.parse(UserContract.CONTENT_URI.toString() + "/" + data.id)
        val cursorDataUser = contentResolver?.query(uriWithId, null, null, null, null)
        val listDataUser = MappingHelper.mapCursorToArrayList(cursorDataUser)
        for (i in listDataUser) {
            if (i.id == data.id) isFavorite = true
        }
        return isFavorite
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        inflater.inflate(R.menu.favorite_menu, menu)
        inflater.inflate(R.menu.setting_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel.setSearchGithub(query)
                showLoading(true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.favorite -> {
                val showFavorite = Intent(this, FavoriteActivity::class.java)
                startActivity(showFavorite)
            }
            R.id.setting -> {
                val showSetting = Intent(this, PreferenceSetting::class.java)
                startActivity(showSetting)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressLayout.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
        }
    }
}
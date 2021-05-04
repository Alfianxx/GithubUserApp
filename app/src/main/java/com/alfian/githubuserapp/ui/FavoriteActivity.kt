package com.alfian.githubuserapp.ui

import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfian.githubuserapp.R
import com.alfian.githubuserapp.adapter.ListUserAdapter
import com.alfian.githubuserapp.databinding.ActivityFavoriteBinding
import com.alfian.githubuserapp.db.UserContract
import com.alfian.githubuserapp.db.UserContract.CONTENT_URI
import com.alfian.githubuserapp.entity.User
import com.alfian.githubuserapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: ListUserAdapter
    private lateinit var progressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressLayout = findViewById(R.id.progress_view)
        showLoading(true)
        supportActionBar?.title = "Favorite User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvFavorite.setHasFixedSize(true)
        adapter = ListUserAdapter()
        adapter.notifyDataSetChanged()

        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.adapter = adapter


        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadUserAsync()
            }
        }
        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val isFavorite = isFavoriteState(data)
                val showDetail = Intent(this@FavoriteActivity, DetailActivity::class.java)
                showDetail.putExtra(DetailActivity.EXTRA_FAVORITE, isFavorite)
                showDetail.putExtra(DetailActivity.EXTRA_USER, data.username)
                startActivity(showDetail)
            }
        })

        loadUserAsync()
        showLoading(false)
    }

    fun isFavoriteState(data: User): Boolean {
        var isFavorite = false
        val uriWithId = Uri.parse(UserContract.CONTENT_URI.toString() + "/" + data.id)
        val cursorDataUser = contentResolver?.query(uriWithId, null, null, null, null)
        val listDataUser = MappingHelper.mapCursorToArrayList(cursorDataUser)
        for (i in listDataUser) {
            if (i.id == data.id) isFavorite = true
        }
        return isFavorite
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadUserAsync() {

        GlobalScope.launch(Dispatchers.Main) {
            val deferredUser = async(Dispatchers.IO) {
                val cursor = contentResolver.query(UserContract.CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }

            val favorite = deferredUser.await()
            if (favorite.size > 0) adapter.setData(favorite)
            else {
                adapter.setData(ArrayList())
                Snackbar.make(binding.rvFavorite, "Data User Empty", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressLayout.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
        }
    }

}
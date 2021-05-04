package com.alfian.githubuserapp.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.alfian.githubuserapp.MainViewModel
import com.alfian.githubuserapp.R
import com.alfian.githubuserapp.adapter.SectionsPagerAdapter
import com.alfian.githubuserapp.databinding.ActivityDetailBinding
import com.alfian.githubuserapp.db.UserContract
import com.alfian.githubuserapp.db.UserContract.CONTENT_URI
import com.alfian.githubuserapp.entity.User
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var progressLayout: FrameLayout

    private var isFavorite = false
    private var menuItem: Menu? = null
    private var dataUser: User? = null
    private lateinit var uriWithId: Uri


    companion object {
        const val EXTRA_USER = "extra_user"
        const val EXTRA_FAVORITE = "extra_favorite"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        progressLayout = findViewById(R.id.progress_view)

        val username = intent.getStringExtra(EXTRA_USER)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = username
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f


        val isFav = intent.getBooleanExtra(EXTRA_FAVORITE, false)
        isFavorite = isFav

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        mainViewModel.setDetailUser(username)
        showLoading(true)

        mainViewModel.getDetail().observe(this, { user ->
            if (user != null) {
                dataUser = user
                uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user.id)
                binding.tvName.text = user.name
                binding.tvUsername.text = user.username
                val followers = "${user.follower} \n followers"
                binding.tvFollowers.text = followers
                val following = "${user.following} \n following"
                binding.tvFollowing.text = following
                val repository = "${user.repository} \n repository"
                binding.tvRepository.text = repository
                binding.tvCompany.text = user.company
                binding.tvLocation.text = user.location
                Glide.with(this)
                    .load(user.avatar)
                    .into(binding.imgAvatar)

                showLoading(false)
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
    }

    override fun onBackPressed() {
        if (progressLayout.visibility == View.VISIBLE) {
            progressLayout.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        setIconFavorite(menu)
        menuItem = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.favorite -> setUserFavorite()
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

    private fun setIconFavorite(menu: Menu) {
        if (isFavorite) {
            menu.getItem(0).icon =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24)
        } else {
            menu.getItem(0).icon =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_unfavorite_border_24)
        }
    }

    private fun setUserFavorite() {
        if (isFavorite) {
            contentResolver.delete(uriWithId, null, null)
            Snackbar.make(
                binding.viewPager,
                "Un favorite ${dataUser?.name}",
                Snackbar.LENGTH_SHORT
            ).show()
            isFavorite = false
            setIconFavorite(menuItem!!)
        } else {
            val values = ContentValues()
            values.put(UserContract.UserColumns.USERNAME, dataUser?.username)
            values.put(UserContract.UserColumns._ID, dataUser?.id)
            values.put(UserContract.UserColumns.URL, dataUser?.url)
            values.put(UserContract.UserColumns.AVATAR, dataUser?.avatar)
            contentResolver.insert(CONTENT_URI, values)
            Snackbar.make(
                binding.viewPager,
                "${dataUser?.name} add to favorite",
                Snackbar.LENGTH_SHORT
            ).show()
            isFavorite = true
            setIconFavorite(menuItem!!)
        }
    }


}
package com.alfian.githubuserapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfian.githubuserapp.MainViewModel
import com.alfian.githubuserapp.R
import com.alfian.githubuserapp.adapter.ListUserAdapter
import com.alfian.githubuserapp.databinding.FragmentFollowingBinding
import com.alfian.githubuserapp.entity.User


class FollowingFragment : Fragment() {

    companion object {
        private const val ARG_USERNAME = "username"
        fun newInstance(username: String?): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var progressLayout: FrameLayout
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFollowing.setHasFixedSize(true)
        val adapter = ListUserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvFollowing.layoutManager = LinearLayoutManager(activity)
        binding.rvFollowing.adapter = adapter
        progressLayout = view.findViewById(R.id.progress_view)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        val username = arguments?.getString(ARG_USERNAME)
        mainViewModel.setFollowersOrFollowing(username, "following")
        showLoading(true)

        mainViewModel.getFollowers().observe(viewLifecycleOwner, { userItems ->
            if (userItems != null) {
                adapter.setData(userItems)
                showLoading(false)
            }
        })

        mainViewModel.getError().observe(viewLifecycleOwner, { error ->
            val showErrorDialog = AlertDialog.Builder(context!!)
                .setTitle("Network Error")
                .setMessage(error)
                .setPositiveButton("ok") { _, _ -> }
                .create()

            showErrorDialog.show()
            showLoading(false)
        })

        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {}
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressLayout.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
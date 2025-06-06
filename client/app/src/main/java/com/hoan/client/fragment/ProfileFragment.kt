package com.hoan.client.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hoan.client.LoginActivity
import com.hoan.client.R
import com.hoan.client.databinding.FragmentProfileBinding
import com.hoan.client.network.response.UserResponse
import com.squareup.picasso.Picasso

class ProfileFragment(
    private var user: UserResponse
) : Fragment(R.layout.fragment_profile), EditProfileFragment.EditedUserListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"

    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        Log.d("PROFILE_FRAGMENT", user.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editButton.setOnClickListener {
            val listPostsFragment = requireActivity().supportFragmentManager.findFragmentByTag("LIST_POST_FRAGMENT")
            val listeners = mutableListOf<EditProfileFragment.EditedUserListener>()
            listeners.add(this)
            if (listPostsFragment is EditProfileFragment.EditedUserListener) {
                listeners.add(listPostsFragment)
            }
            val fragment = EditProfileFragment.newInstance(user, listeners)
            replaceFragment(fragment)
        }

        binding.logoutButton.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.remove("username")
            editor.remove("email")
            editor.remove("jwt")
            editor.remove("expiration_time")
            editor.apply()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            requireActivity().finish()
            startActivity(intent)
        }

        rebuildView()
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_view, fragment)
        fragmentTransaction.addToBackStack(fragment.id.toString())
        fragmentTransaction.commit()
    }

    override fun updateUserDetails(user: UserResponse) {
        Log.d("UPDATING_USER_DETAILS", user.toString())
        this.user = user
        refreshView()
    }

    @SuppressLint("DetachAndAttachSameFragment")
    private fun refreshView() {
        requireActivity().supportFragmentManager.beginTransaction()
            .detach(this)
            .attach(this)
            .commit()
        rebuildView()
        Log.d("PROFILE_FRAGMENT", "Refreshed")
    }

    private fun rebuildView() {
        binding.etFullName.text = user.fullName ?: ""
        binding.etUsername.text = user.username
        binding.etBiography.text = user.biography ?: ""
        binding.etLocation.text = user.location ?: ""
        binding.etCalendar.text = "Joined social in ${user.registration_date}" ?: ""
        picasso.load(user.profilePicture).placeholder(R.color.primaryAccent).into(binding.civProfilePicture)
    }

    companion object {
        @JvmStatic
        fun newInstance(user: UserResponse) = ProfileFragment(user)
    }
}

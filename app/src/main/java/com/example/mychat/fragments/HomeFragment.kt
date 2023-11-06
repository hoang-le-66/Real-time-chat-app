package com.example.mychat.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.activity.SignInActivity
import com.example.mychat.adapter.OnUserClickListener
import com.example.mychat.adapter.RecentChatAdapter
import com.example.mychat.adapter.UserAdapter
import com.example.mychat.adapter.onRecentChatClicked
import com.example.mychat.databinding.FragmentHomeBinding
import com.example.mychat.model.RecentChats
import com.example.mychat.model.Users
import com.example.mychat.viewmodel.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView


@Suppress("DEPRECATION")
//??
class HomeFragment : Fragment(), OnUserClickListener, onRecentChatClicked {
    lateinit var rvUsers: RecyclerView
    lateinit var usersAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var fbAuth: FirebaseAuth
    //lateinit var homePressDialog: ProgressDialog
    lateinit var toolBar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentChatAdapter: RecentChatAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)


        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        fbAuth = FirebaseAuth.getInstance()
        //homePressDialog= ProgressDialog(activity)
        toolBar = view.findViewById(R.id.toolbarMain)
        circleImageView= toolBar.findViewById(R.id.tlImage)
        fragmentHomeBinding.lifecycleOwner = viewLifecycleOwner

        usersAdapter = UserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)


        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
        rvUsers.layoutManager = layoutManagerUsers

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {

            usersAdapter.setUserList(it)
            usersAdapter.setOnUserClickListener(this)
            rvUsers.adapter = usersAdapter
        })

        fragmentHomeBinding.logOut.setOnClickListener {
            fbAuth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))

        }
        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {

            Glide.with(requireContext()).load(it).into(circleImageView)

        })

        recentChatAdapter= RecentChatAdapter()
        userViewModel.getRecentChats().observe(viewLifecycleOwner, Observer {
            fragmentHomeBinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentChatAdapter.setOnRecentList(it)
            fragmentHomeBinding.rvRecentChats.adapter = recentChatAdapter
        })

        recentChatAdapter.setOnResentChatListener(this)

        circleImageView.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_settingFragment)
        }

    }

    override fun onUserSelected(position: Int, users: Users) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
        Log.e("HOMEFRAGMENT", "Clicked on ${users.username}")
    }

    override fun getOnRecentChatClicked(position: Int, recentChatList: RecentChats) {
        val action= HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentChatList)
        view?.findNavController()?.navigate(action)
    }

}
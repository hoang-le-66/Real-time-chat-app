package com.example.mychat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.Utils
import com.example.mychat.adapter.MessageAdapter
import com.example.mychat.adapter.RecentChatAdapter
import com.example.mychat.adapter.UserAdapter
import com.example.mychat.databinding.FragmentChatBinding
import com.example.mychat.databinding.FragmentChatfromHomeBinding
import com.example.mychat.databinding.FragmentHomeBinding
import com.example.mychat.model.Messages
import com.example.mychat.model.Users
import com.example.mychat.viewmodel.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class ChatFromHomeFragment : Fragment() {
    private lateinit var args: ChatFromHomeFragmentArgs
    private lateinit var chatFromHomeBinding: FragmentChatfromHomeBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var tvChatUserName: TextView
    private lateinit var tvChatUserStatus: TextView
    private lateinit var imvBack: ImageView
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatFromHomeBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return chatFromHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        toolbar = view.findViewById(R.id.toolBarChat)
        circleImageView= toolbar.findViewById(R.id.chatImageViewUser)
        tvChatUserStatus= view.findViewById(R.id.tvChatUserStatus)
        tvChatUserName= view.findViewById(R.id.tvChatUserName)
        imvBack= toolbar.findViewById(R.id.imvBack)

        imvBack.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }

//        chatBinding.imvBack.setOnClickListener{
//            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
//        }

        Glide.with(requireContext()).load(args.recentchats.friendsimage!!).into(circleImageView)

        tvChatUserName.setText(args.recentchats.name)
        //tvChatUserStatus.setText(args.recentchats.status)
        //Work for status
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users").document(args.recentchats.friendid!!)
            .addSnapshotListener { value, error ->

            if(error!=null){
                return@addSnapshotListener
            }
            if(value!=null && value.exists()){

                val userModel = value.toObject(Users::class.java)
                tvChatUserStatus.setText(userModel!!.status.toString())

            }

        }

        //??
        chatFromHomeBinding.viewModel = chatAppViewModel
        chatFromHomeBinding.lifecycleOwner = viewLifecycleOwner
        //Whenever the message is sent, we have to come to sendMessage() in viewmodel
        //Then access parameters
        chatFromHomeBinding.btnSendChatFromHome.setOnClickListener {
            chatAppViewModel.sendMessage(
                Utils.getUILoggedIn(),args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!)
        }
        //??
        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)

        })

    }

    private fun initRecyclerView(it: List<Messages>) {

        messageAdapter= MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatFromHomeBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it!!)
        messageAdapter.notifyDataSetChanged()
        chatFromHomeBinding.messagesRecyclerView.adapter = messageAdapter

    }


}
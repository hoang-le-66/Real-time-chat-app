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
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.Utils
import com.example.mychat.adapter.MessageAdapter
import com.example.mychat.databinding.FragmentChatBinding
import com.example.mychat.model.Messages
import com.example.mychat.viewmodel.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatFragment : Fragment() {
    private lateinit var args: ChatFragmentArgs
    private lateinit var chatBinding: FragmentChatBinding
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
        chatBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return chatBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //??
        args = ChatFragmentArgs.fromBundle(requireArguments())
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        toolbar = view.findViewById(R.id.toolBarChat)
        circleImageView= toolbar.findViewById(R.id.chatImageViewUser)
        tvChatUserStatus= view.findViewById(R.id.tvChatUserStatus)
        tvChatUserName= view.findViewById(R.id.tvChatUserName)
        imvBack= toolbar.findViewById(R.id.imvBack)

        imvBack.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

//        chatBinding.imvBack.setOnClickListener{
//            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
//        }

        Glide.with(requireContext()).load(args.users.imageUrl).into(circleImageView)
        tvChatUserStatus.setText(args.users.status)
        tvChatUserName.setText(args.users.username)




        //??
        chatBinding.viewModel = chatAppViewModel
        chatBinding.lifecycleOwner = viewLifecycleOwner
        //Whenever the message is sent, we have to come to sendMessage() in viewmodel
        //Then access parameters
        chatBinding.btnChatSend.setOnClickListener {
            chatAppViewModel.sendMessage(Utils.getUILoggedIn(),args.users.userid!!,
                args.users.username!!,
                args.users.imageUrl!!)
        }
        //??
        chatAppViewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)

        })

    }

    private fun initRecyclerView(it: List<Messages>) {

        messageAdapter= MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it!!)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter

    }


}
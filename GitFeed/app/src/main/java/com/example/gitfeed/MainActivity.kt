package com.example.gitfeed

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitfeed.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private val adapter = EventAdapter(mutableListOf())
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeContainer.setOnRefreshListener {
            adapter.clear()
            viewModel.fetchEvents("RxKotlin")
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.eventLiveData.observe(this, Observer { events ->
            adapter.updateEvents(events)
            binding.swipeContainer.isRefreshing = false
        })

        viewModel.fetchEvents("RxKotlin")
    }
}

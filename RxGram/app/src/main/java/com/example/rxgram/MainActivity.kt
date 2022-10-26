/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.rxgram

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rxgram.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = getString(R.string.collage)

        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        binding.addButton.setOnClickListener {
            actionAdd()
        }

        binding.clearButton.setOnClickListener {
            actionClear()
        }

        binding.saveButton.setOnClickListener {
            actionSave()
        }

        viewModel.getSelectedPhotos().observe(this, Observer { photos ->
            photos?.let {
                if (photos.isNotEmpty()) {
                    val bitmaps = photos.map {
                        BitmapFactory.decodeResource(resources, it.drawable)
                    }
                    val newBitmap = combineImages(bitmaps)
                    binding.collageImage.setImageDrawable(
                        BitmapDrawable(resources, newBitmap)
                    )
                } else {
                    binding.collageImage.setImageResource(android.R.color.transparent)
                }
                updateUi(photos)
            }
        })

        viewModel.getThumbnailStatus().observe(this, Observer { status ->
            if (status == ThumbnailStatus.READY) {
                binding.apply {
                    thumbnail.setImageDrawable(collageImage.drawable)
                }
            }
        })

        viewModel.getCollageStatus().observe(this, Observer { status ->
            Log.d("MainActivity", status.toString())
            if (status == CollageStatus.COMPLETE) {
                Toast.makeText(this, getString(R.string.image_limit_reached), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUi(photos: List<Photo>) {
        binding.apply {
            saveButton.isEnabled = photos.isNotEmpty() && photos.size % 2 == 0
            clearButton.isEnabled = photos.isNotEmpty()
            addButton.isEnabled = photos.size < 6

            title = if (photos.isNotEmpty()) {
                resources.getQuantityString(R.plurals.photos_format, photos.size, photos.size)
            } else {
                getString(R.string.collage)
            }
        }
    }

    private fun actionAdd() {
        val addPhotoBottomDialogFragment = PhotosBottomDialogFragment.newInstance()
        addPhotoBottomDialogFragment.show(supportFragmentManager, "PhotosBottomDialogFragment")
        viewModel.subscribeSelectedPhotos(addPhotoBottomDialogFragment)
    }

    private fun actionClear() {
        viewModel.clearPhotos()
        binding.collageImage.setImageResource(android.R.color.transparent)
        binding.thumbnail.setImageResource(android.R.color.transparent)
        updateUi(listOf())
    }

    private fun actionSave() {
        viewModel.saveBitmapFromImageView(binding.collageImage, this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { file ->
                Toast.makeText(this, "$file saved", Toast.LENGTH_SHORT).show()
            }, onError = { e ->
                Toast.makeText(this, "Error saving file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            })
    }
}

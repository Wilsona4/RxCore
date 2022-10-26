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

package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app.databinding.ActivityWindowBinding
import com.example.app.utils.timer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/*Acts like the buffer but emits observables of buffered items instead of array*/
class WindowActivity : AppCompatActivity() {
    private val elementsPerSecond = 3
    private val windowTimeSpan = 4L
    private val windowMaxCount = 10L

    private val disposables = CompositeDisposable()

    private lateinit var binding: ActivityWindowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sourceObservable = PublishSubject.create<String>()


//        sourceObservable
//            .window(windowTimeSpan, TimeUnit.SECONDS, AndroidSchedulers.mainThread(), windowMaxCount)
//            .flatMap { windowedObservable: Observable<String> ->
//                val marbleView = MarbleView(this)
//                binding.marbleViews.addView(marbleView)
//                windowedObservable
//                    .map { value -> value to marbleView }
//                    .concatWith(Observable.just("" to marbleView))
//            }
//            .subscribe { pair ->
//                val (value, marbleView) = pair
//                if (value.isEmpty()) {
//                    marbleView.onComplete()
//                } else {
//                    marbleView.onNext(value)
//                }
//            }
//            .addTo(disposables)
        val windowedObservable = sourceObservable
            .window(windowTimeSpan, TimeUnit.SECONDS, AndroidSchedulers.mainThread(), windowMaxCount)


//        val marbleViewObservable = flatMap { windowedObservable: Observable<String> ->
//                val marbleView = MarbleView(this)
//                binding.marbleViews.addView(marbleView)
//                windowedObservable
//                    .map { value -> value to marbleView }
//                    .concatWith(Observable.just("" to marbleView))
//            }
//            .subscribe { pair ->
//                val (value, marbleView) = pair
//                if (value.isEmpty()) {
//                    marbleView.onComplete()
//                } else {
//                    marbleView.onNext(value)
//                }
//            }
//            .addTo(disposables)


        timer(elementsPerSecond) {
            sourceObservable.onNext("🐱")
        }.addTo(disposables)

        sourceObservable.subscribe(binding.windowSource)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}

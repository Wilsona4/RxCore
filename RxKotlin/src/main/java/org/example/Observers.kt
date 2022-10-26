package org.example

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.pow
import kotlin.math.roundToInt


fun main() {
    exampleOf("just & subscribe") {
        val observable = Observable.just(1, 2, 3)

        observable.subscribe {
            println(it)
        }
    }

    exampleOf("range") {
        // 1
        val observable: Observable<Int> = Observable.range(1, 10)

        observable.subscribe {
            // 2
            val n = it.toDouble()
            val fibonacci = ((1.61803.pow(n) - 0.61803.pow(n)) / 2.23606).roundToInt()
            println(fibonacci)
        }

    }/*Returns empty value and terminates*/
    exampleOf("empty") {
        val observable = Observable.empty<Unit>()

        observable.subscribeBy(onNext = {
            println(it)
        }, onComplete = {
            println("completed")
        })
    }

    /*Returns empty value and never terminates*/
    exampleOf("never") {
        val observable = Observable.never<Any>()

        val subscription = observable.subscribeBy(onNext = {
            println(it)
        }, onComplete = {
            println("completed") /*This is never called effectively leaking memory*/
        })

        subscription.dispose()
    }

    exampleOf("CompositeDisposable") {
        val compositeDisposable = CompositeDisposable()

        val observable = Observable.just("A", "B", "C").subscribe {
            println(it)
        }
        compositeDisposable.add(observable)

        compositeDisposable.dispose()
    }

    exampleOf("create") {

        Observable.create { emitter ->
            emitter.onNext("Wilson")
            emitter.onError(RuntimeException("Error"))
            emitter.onComplete()
            emitter.onNext("Ahanmisi")
        }.subscribeBy(onNext = {
            println(it)
        }, onComplete = {
            println("Completed")
        }, onError = {
            println(it)
        }).dispose()
    }

    exampleOf("defer") {
        val disposable = CompositeDisposable()

        var switch = false

        val factory = Observable.defer {
            switch = !switch

            if (switch) {
                Observable.just(1, 2, 3)
            } else {
                Observable.just(4, 5, 6)
            }
        }

        for (i in 0..3) {
            disposable.add(
                factory.subscribe {
                    println(it)
                }
            )
        }

        disposable.dispose()
    }

    exampleOf("single") {
        val disposable = CompositeDisposable()

        fun loadText(fileName: String): Single<String> {
            return Single.create create@{ emitter ->
                val file = File(fileName)
                if (!file.exists()) {
                    emitter.onError(FileNotFoundException("$fileName not found"))
                    return@create
                }

                val text = file.readText(Charsets.UTF_8)
                emitter.onSuccess(text)
            }
        }

        val observer = loadText("hello.txt").subscribeBy(
            onSuccess = {
                println(it)
            },
            onError = {
                println(it)
            }
        )

        disposable.add(observer)
        disposable.dispose()
    }

    exampleOf("IObserver") {

        val observer: Observer<String> = object : Observer<String> {

            lateinit var disposable: Disposable
            override fun onComplete() {
                println("All Completed")
            }

            override fun onNext(item: String) {
                println("Next $item")
                if (item.endsWith("3") && !disposable.isDisposed) {
                    disposable.dispose()
                    println("Disposed")
                }
            }

            override fun onError(e: Throwable) {
                println("Error Occurred ${e.message}")
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }
        }//Create Observer

        val observable: Observable<String> = Observable.create<String> {//1
            it.onNext("Emit 1")
            it.onNext("Emit 2")
            it.onNext("Emit 3")
            it.onNext("Emit 4")
            it.onComplete()
        }

        observable.subscribe(observer)
    }

//    org.example.sequence()
}


fun sequence() {
    val sequence = 0 until 3
    val iterator = sequence.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }
}
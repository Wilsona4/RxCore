package org.example

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

fun main() {
    exampleOf("IgnoreElements") {
        val disposable = CompositeDisposable()
        val publishSubject = PublishSubject.create<String>()

        publishSubject
            .ignoreElements()
            .subscribe {
                println("I'm Groot")
            }.addTo(disposable)

        publishSubject.onNext("What's")
        publishSubject.onNext("Your")
        publishSubject.onNext("Name")
        publishSubject.onComplete()
    }

    exampleOf("elementAt") {
        val disposable = CompositeDisposable()
        val publishSubject = PublishSubject.create<String>()

        publishSubject
            .elementAt(2)
            .subscribeBy(
                onSuccess = {
                    println("I'm Groot")
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)

        publishSubject.onNext("What's")
        publishSubject.onNext("Your")
        publishSubject.onNext("Name")
        publishSubject.onComplete() /*This will not be called*/
    }

    exampleOf("filter") {
        val disposable = CompositeDisposable()

        Observable.fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
            .filter {
                it > 5
            }
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)
    }

    exampleOf("skip") {
        val disposable = CompositeDisposable()

        Observable.fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
            .skip(5)
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)
    }

    /*Once it gets to past the skipped items it lets everyone through.
    *
    * If the first element is not affected by said predicate, it ignores everything
    *
    * It's messed up
    * */
    exampleOf("skipWhile") {
        val disposable = CompositeDisposable()

        Observable.fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
            .skipWhile { number ->
                number > 1
            }
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)
    }


    exampleOf("skipUtil") {
        val disposable = CompositeDisposable()
        val subject = PublishSubject.create<String>()
        val trigger = PublishSubject.create<String>()

        subject
            .skipUntil(trigger)
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)

        subject.onNext("What's")
        subject.onNext("Your")

        trigger.onNext("Fire")

        subject.onNext("Name")

        subject.onComplete() /*This will not be called*/
    }

    /*
    * We've got the Take operator which is opposite of the skip
    * */
    exampleOf("takeUtil") {
        val disposable = CompositeDisposable()
        val subject = PublishSubject.create<String>()
        val trigger = PublishSubject.create<String>()

        subject
            .takeUntil(trigger)
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)

        subject.onNext("What's")
        subject.onNext("Your")

        trigger.onNext("Fire")

        subject.onNext("Name")

        subject.onComplete() /*This will not be called*/
    }

    exampleOf("distinctUtilChanged") {
        val disposable = CompositeDisposable()

        Observable.fromIterable(listOf(1, 1, 2, 2, 3, 4, 4, 5, 6, 7, 8, 9))
            .distinctUntilChanged()
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)
    }

    exampleOf("distinctUtilChangedPredicate") {
        val disposable = CompositeDisposable()

        Observable.fromIterable(listOf("ABC", "BCD", "CDE", "FGH", "IJK", "JKL", "LMN"))
            .distinctUntilChanged { first, second ->
                second.any {
                    it in first
                }
            }
            .subscribeBy(
                onNext = {
                    println(it)
                },
                onComplete = {
                    println("Completed")
                }
            ).addTo(disposable)
    }
}
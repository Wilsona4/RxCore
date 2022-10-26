package org.example

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

fun main() {
    exampleOf("toList") {

        val subscriptions = CompositeDisposable()
        // 1
        val items = Observable.just("A", "B", "C")

        subscriptions.add(
            items
                // 2
                .toList()
                .subscribeBy {
                    println(it)
                }
        )
    }

    exampleOf("map") {

        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just("M", "C", "V", "I", "X")
                // 2
                .map {
                    // 3
                    it.romanNumeralIntValue()
                }
                // 4
                .subscribeBy {
                    println(it)
                })
    }

    exampleOf("flatMap") {
        val disposable = CompositeDisposable()

        val student = PublishSubject.create<Student>()
        val isaac = Student(BehaviorSubject.createDefault(50))
        val precious = Student(BehaviorSubject.createDefault(80))

        disposable.add(
            student.flatMap {
                it.score
            }.subscribeBy {
                println(it)
            }
        )

        student.onNext(isaac)

        isaac.score.onNext(60)

        student.onNext(precious)

        isaac.score.onNext(75)

        precious.score.onNext(90)

        disposable.dispose()
    }

    /*Subscribe to latest emission*/
    exampleOf("switchMap") {
        val disposable = CompositeDisposable()

        val student = PublishSubject.create<Student>()
        val isaac = Student(BehaviorSubject.createDefault(50))
        val precious = Student(BehaviorSubject.createDefault(80))


        student.switchMap {
            it.score
        }.subscribeBy {
            println(it)
        }.addTo(disposable)


        student.onNext(isaac)

        isaac.score.onNext(60)

        student.onNext(precious)
        precious.score.onNext(90)

        disposable.dispose()
    }

    exampleOf("materialize") {
        val disposable = CompositeDisposable()

        val isaac = Student(BehaviorSubject.createDefault(50))
        val precious = Student(BehaviorSubject.createDefault(80))

        val student = BehaviorSubject.createDefault(isaac)


        student.switchMap {
            it.score.materialize()
        }.subscribeBy {
            println(it)
        }.addTo(disposable)

        isaac.score.onNext(60)
        isaac.score.onError(RuntimeException("Error occurred"))
        isaac.score.onNext(66)

        student.onNext(precious)
        precious.score.onNext(90)

        disposable.dispose()
    }

    exampleOf("dematerialize") {
        val disposable = CompositeDisposable()

        val isaac = Student(BehaviorSubject.createDefault(50))
        val precious = Student(BehaviorSubject.createDefault(80))

        val student = BehaviorSubject.createDefault(isaac)


        student.switchMap {
            it.score.materialize()
        }.filter {
            if (it.error != null) {
                println(it.error)
                false
            } else true
        }.dematerialize { it }
            .subscribeBy {
                println(it)
            }.addTo(disposable)

        isaac.score.onNext(60)
        isaac.score.onError(RuntimeException("Error occurred"))
        isaac.score.onNext(66)

        student.onNext(precious)
        precious.score.onNext(90)

        disposable.dispose()
    }
}

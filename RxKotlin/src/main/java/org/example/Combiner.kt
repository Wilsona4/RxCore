package org.example

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.timer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

fun main() {
    exampleOf("startWith") {

        val disposable = CompositeDisposable()
        // 1
        val missingNumbers = Observable.just(3, 4, 5)
        // 2
        val completeSet = missingNumbers.startWithIterable(listOf(1, 2))

        completeSet.subscribe { number ->
            println(number)
        }.addTo(disposable)
    }

    exampleOf("concat") {

        val disposable = CompositeDisposable()
        // 1
        val missingNumbers = Observable.just(3, 4, 5)
        // 2
        val completeSet = missingNumbers.startWithIterable(listOf(1, 2))

        Observable.concat(completeSet, missingNumbers).subscribe { number ->
            println(number)
        }.addTo(disposable)
    }

    exampleOf("concatWith") {

        val disposable = CompositeDisposable()
        // 1
        val missingNumbers = Observable.just(3, 4, 5)
        // 2
        val completeSet = missingNumbers.startWithIterable(listOf(1, 2))

        missingNumbers.concatWith(completeSet).subscribe { number ->
            println(number)
        }.addTo(disposable)
    }

    /*Like flatmap but ensures order*/
    exampleOf("concatMap") {
        val disposable = CompositeDisposable()
        // 1
        val countries = Observable.just("Germany", "Spain")
        // 2
        val observable = countries.concatMap {
            when (it) {
                "Germany" -> Observable.just("Berlin", "Münich", "Frankfurt")

                "Spain" -> Observable.just("Madrid", "Barcelona", "Valencia")

                else -> Observable.empty()
            }
        }
        // 3
        observable.subscribe { city ->
            println(city)
        }.addTo(disposable)
    }

    /*Acts like concat but maintains the order to which next items are emitted*/
    exampleOf("merge") {
        val disposable = CompositeDisposable()
        // 1
        val first = BehaviorSubject.createDefault(1)
        // 2
        val second = BehaviorSubject.createDefault(3)

        Observable.merge(first, second).subscribe { number ->
            println(number)
        }.addTo(disposable)

        first.onNext(2)
        first.onNext(3)
        second.onNext(1)
        second.onNext(2)
        first.onNext(4)
        second.onNext(3)
        first.onComplete()
        second.onComplete()
    }

    /*Combine the latest emission of two observables*//*Permits combining observables of different types*/
    exampleOf("combineLatest") {

        val subscriptions = CompositeDisposable()

        val left = PublishSubject.create<String>()
        val right = PublishSubject.create<String>()

        Observables.combineLatest(left, right) { leftString, rightString ->
            "$leftString $rightString"
        }.subscribe {
            println(it)
        }.addTo(subscriptions)

        left.onNext("Hello")
        right.onNext("World")
        left.onNext("It’s nice to")
        right.onNext("be here!")
        left.onNext("Actually, it’s super great to")
    }

    exampleOf("zip") {

        val disposable = CompositeDisposable()

        val left = PublishSubject.create<String>()
        val right = PublishSubject.create<String>()

        Observables.zip(left, right) { leftString, rightString ->
            "It's is $leftString in $rightString"
        }.subscribe {
            println(it)
        }.addTo(disposable)

        left.onNext("sunny")
        right.onNext("Lisbon")
        left.onNext("cloudy")
        right.onNext("Copenhagen")
        left.onNext("cloudy")
        right.onNext("London")
        left.onNext("sunny")
        right.onNext("Madrid")
        right.onNext("Vienna")
        right.onNext("Lagos")
    }

    /*Triggers*/
    exampleOf("withLatestFrom") {
        val subscriptions = CompositeDisposable()

        // 1 : Button serves as a trigger to emmit the latest values of the edit-text
        val button = PublishSubject.create<Unit>()
        val editText = PublishSubject.create<String>()

        // 2
        button.withLatestFrom(editText) { _: Unit, value: String ->
            value
        }.subscribe {
            println(it)
        }.addTo(subscriptions)

        // 3
        editText.onNext("Par")
        editText.onNext("Pari")
        editText.onNext("Paris")
        button.onNext(Unit)
        button.onNext(Unit)
    }

    exampleOf("sample") {
        val subscriptions = CompositeDisposable()

        // 1 : Button serves as a trigger to emmit the latest values of the edit-text
        // if it changed after last emission
        val button = PublishSubject.create<Unit>()
        val editText = PublishSubject.create<String>()

        // 2
        editText.sample(button).subscribe {
            println(it)
        }.addTo(subscriptions)

        // 3
        editText.onNext("Par")
        editText.onNext("Pari")
        editText.onNext("Paris")
        button.onNext(Unit)
        button.onNext(Unit)
    }

//  Note: reduce produces its summary (accumulated) value only when the source Observable completes.
//  Applying this operator to sequences that never complete won’t emit anything.
//  This is a frequent source of confusion and hidden problems.
    exampleOf("reduce") {

        val subscriptions = CompositeDisposable()

        val source = Observable.just(1, 3, 5, 7, 9)
        source
            .reduce(0) { a, b -> a + b }
            .subscribeBy(onSuccess = {
                println(it)
            })
            .addTo(subscriptions)
    }
    /*This returns the value of each intermediate operation unlike reduce that waits until completion*/
    exampleOf("scan") {

        val subscriptions = CompositeDisposable()

        val source = Observable.just(1, 3, 5, 7, 9)
        source
            .scan(0) { a, b -> a + b }
            .subscribe {
                println(it)
            }
            .addTo(subscriptions)

    }
}


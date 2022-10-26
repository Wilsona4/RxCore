package org.example

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import kotlin.ranges.random

/*
* There are four subject types in RxJava:
PublishSubject: Starts empty and only emits new elements to subscribers.

BehaviorSubject: Starts with an optional initial value and replays it
* or the latest element to new subscribers.

ReplaySubject: Initialized with a buffer size and will maintain a
buffer of elements up to that size and replay it to new subscribers.

* AsyncSubject: Starts empty and only emits the last item it receives before it’s completed to subscribers.”
* */

fun main() {
    exampleOf("PublishSubject") {
        val subject = PublishSubject.create<Int>()

        subject.onNext(0)

        val subscriberOne = subject.subscribe {
            println(it)
        }

        subject.onNext(1)

        val subscriberTwo = subject.subscribe {
            printWithLabel("2: ", it)
        }

        subject.onNext(2)

        subscriberOne.dispose()

        subject.onNext(3)

        /*When it receives a terminal event it will emit said event to new subscribers and no longer emit next events*/
        subject.onComplete()

        subject.onNext(5)

        subscriberTwo.dispose()

        val subscriptionThree = subject.subscribeBy(
            onNext = { printWithLabel("3)", it) },
            onComplete = { printWithLabel("3)", "Complete") }
        )
        /*This will not be emitted*/
        subject.onNext(6)

        subscriptionThree.dispose()
    }

    exampleOf("BehaviorSubject") {
        val subject = BehaviorSubject.createDefault(0)

        val subscriberOne = subject.subscribe {
            println(it)
        }

        subject.onNext(1)

        val subscriberTwo = subject.subscribe {
            printWithLabel("2: ", it)
        }

        subject.onNext(2)

        subscriberOne.dispose()

        subject.onNext(3)

        /*When it receives a terminal event it will emit said event to new subscribers and no longer emit next events*/
        subject.onComplete()

        subject.onNext(5)

        subscriberTwo.dispose()

        /*Behavior Subject will replay the last onNext event*/
        val subscriptionThree = subject.subscribeBy(
            onNext = { printWithLabel("3)", it) },
            onComplete = { printWithLabel("3)", "Complete") }
        )
        /*This will not be emitted*/
        subject.onNext(6)

        subscriptionThree.dispose()
    }

    exampleOf("BehaviorSubject State") {

        val disposable = CompositeDisposable()
        val behaviorSubject = BehaviorSubject.createDefault(0)

        behaviorSubject.value?.let {
            println(it)
        }

        disposable.add(
            behaviorSubject.subscribeBy {
                printWithLabel("1:", it)
            }
        )

        behaviorSubject.onNext(1)

        behaviorSubject.value?.let { println(it) }

        disposable.dispose()
    }

    exampleOf("ReplaySubject") {
        val disposable = CompositeDisposable()

        val subject = ReplaySubject.createWithSize<Int>(2)

        subject.onNext(1)
        subject.onNext(2)
        subject.onNext(3)

        disposable.add(
            subject.subscribeBy(
                onError = {
                    printWithLabel("1:", it)
                }
            ) {
                printWithLabel("1:", it)
            }
        )

        disposable.add(
            subject.subscribeBy(
                onError = {
                    printWithLabel("2:", it)
                }
            ) {
                printWithLabel("2:", it)
            }
        )

        subject.onNext(4)

        subject.onError(RuntimeException("Error occurred"))

        disposable.add(
            subject.subscribeBy(
                onError = {
                    printWithLabel("3:", it)
                }
            ) {
                printWithLabel("3:", it)
            }
        )

        disposable.dispose()
    }

    /*
    * Subscribers will only see the last value you passed into the subject before it's completed then a complete event.
    * If the subject receives an error event, subscribers will see nothing!*/
    exampleOf("AsyncSubject") {
        val subscriptions = CompositeDisposable()
        // 1
        val asyncSubject = AsyncSubject.create<Int>()
        // 2
        subscriptions.add(asyncSubject.subscribeBy(
            onNext = { printWithLabel("1)", it) },
            onComplete = { printWithLabel("1)", "Complete") }
        ))
        // 3
        asyncSubject.onNext(0)
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        // 4
        asyncSubject.onComplete()

        subscriptions.dispose()
    }

    /*For an infinite stream that will never terminate thus can call onComplete or onError*/
    exampleOf("RxRelay") {
        val disposable = CompositeDisposable()

        val publishRelay = PublishRelay.create<Int>()

        disposable.add(publishRelay.subscribeBy(
            onNext = { printWithLabel("1)", it) }
        ))

        publishRelay.accept(1)
        publishRelay.accept(2)
        publishRelay.accept(3)

        disposable.dispose()
    }

    exampleOf("Black Jack") {
        val disposable = CompositeDisposable()

        val dealtHand = PublishSubject.create<List<Pair<String, Int>>>()

        fun deal(cardCount: Int) {
            val deck = cards
            var cardsRemaining = 52
            val hand = mutableListOf<Pair<String, Int>>()

            (0 until cardCount).forEach { _ ->
                val randomIndex = (0 until cardsRemaining).random()
                hand.add(deck[randomIndex])
                deck.removeAt(randomIndex)
                cardsRemaining -= 1
            }

            // Add code to update dealtHand here
            if (points(hand) > 21) {
                dealtHand.onError(HandError.Busted())
            } else {
                dealtHand.onNext(hand)
            }
        }

        disposable.add(dealtHand.subscribeBy(
            onNext = { println("Hand: ${cardString(it)}\nPoints: ${points(it)}") },
            onError = { println(it) }
        ))

        deal(3)
    }


}

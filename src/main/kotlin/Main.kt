import io.reactivex.rxjava3.disposables.Disposable

fun main(args: Array<String>) {
//    println("Hello World!")

    var t = QueryClien().requestNotice()
    var subscribe = t.subscribe(
        /* onSuccess = */
        fun(it: List<Any>) {
            for (a in it) {
                println(a)
            }
        },
    )

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
}